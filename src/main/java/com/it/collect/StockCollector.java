package com.it.collect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.it.bean.Stock;
import com.it.repository.StockRepository;
import com.it.util.HttpUtils;
import com.it.util.StockConfig;
import org.aeonbits.owner.ConfigFactory;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockCollector {

    private StockRepository stockRepository;
    private MongoTemplate mongoTemplate;
    private Logger logger = LoggerFactory.getLogger(StockCollector.class);

    @Autowired
    public StockCollector(MongoTemplate mongoTemplate, StockRepository stockRepository) {
        this.mongoTemplate = mongoTemplate;
        this.stockRepository = stockRepository;
    }

    public void initHistoryStock(){
        StockConfig stockConfig = ConfigFactory.create(StockConfig.class);
        String stockCode = stockConfig.stockCodeList();
        Spider.create(new PagerProcess()).addUrl(stockConfig.historyStockUrl() + stockCode).
                addPipeline(new StockPipeline(stockRepository)).thread(1).run();
    }

    public void getStockData(String stockCode) {
        long stockCount = stockRepository.count();
        StockConfig stockConfig = ConfigFactory.create(StockConfig.class);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + stockConfig.appCode());
        Map<String, String> querys = new HashMap<>();
        querys.put("begin", "2017-07-03");
        querys.put("code", stockCode);
        LocalDateTime now = LocalDateTime.now();
        String end = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (!(now.getDayOfWeek().getValue() == 6 || now.getDayOfWeek().getValue() == 7)) {
            querys.put("end", end);
        }
        if (stockCount != 0) {
            Query query = new Query();
            query.with(new Sort(Sort.Direction.DESC, "date"));
            List<Stock> stockList = mongoTemplate.find(query, Stock.class);
            String date = stockList.get(0).getDate().concat(" 00:00");
            LocalDateTime lastDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            if (!(now.getDayOfWeek().getValue() == 6 || now.getDayOfWeek().getValue() == 7) && now.compareTo(lastDate) > 0) {
                querys.put("begin", lastDate.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        }
        dealGetStockData(stockConfig.stockHost(), stockConfig.stockHistoryPath(), headers, querys);
    }

    private void dealGetStockData(String host, String path, Map<String, String> headers, Map<String, String> querys) {
        if (querys.containsKey("begin") && querys.containsKey("end")) {
            try {
                logger.info("获取数据请求参数:{}", querys);
                HttpResponse response = HttpUtils.doGet(host, path, "GET", headers, querys);
                Map<String, Object> result = JSONObject.parseObject(response.toString(), new TypeReference<Map<String, Object>>() {
                });
                Map<String, JSONArray> showapi_res_body = (Map<String, JSONArray>) result.get("showapi_res_body");
                List<Stock> stockList = showapi_res_body.get("list").toJavaList(Stock.class);
                List<Stock> insert = stockRepository.insert(stockList);
                if (!stockList.equals(insert)) {
                    logger.error("insert data error total size :{} insert size:{}", stockList.size(), insert.size());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }




}
