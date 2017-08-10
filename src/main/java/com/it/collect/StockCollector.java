package com.it.collect;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Sets;
import com.it.bean.StockBasicInfo;
import com.it.bean.StockPeriod;
import com.it.repository.StockRepository;
import com.it.util.HttpUtils;
import com.it.util.StockConfig;
import com.mongodb.WriteResult;
import com.mongodb.client.MongoCollection;
import org.aeonbits.owner.ConfigFactory;
import org.apache.http.HttpResponse;
import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import java.util.*;
import java.util.function.Predicate;

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

    public void initHistoryStock() {
        StockConfig stockConfig = ConfigFactory.create(StockConfig.class);
        String stockCode = stockConfig.stockCodeList();
        Spider.create(new PagerProcess(this)).addUrl(stockConfig.historyStockUrl() + stockCode).
                addPipeline(new StockPipeline(stockRepository)).thread(1).run();
    }

    /**
     * @param page default 0
     */
    public void initStockList(int page, String market) {
        StockConfig stockConfig = StockConfig.getConfig();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + stockConfig.appCode());
        Map<String, String> querys = new HashMap<>();
        querys.put("market", market);
        if (page == 0)
            page = 1;
        querys.put("page", page + "");
        dealGetStockData(stockConfig.stockHost(), stockConfig.stockList(), headers, querys);
    }

    private void dealGetStockData(String host, String path, Map<String, String> headers, Map<String, String> querys) {
        try {
            logger.info("获取数据请求参数:{}", querys);
            HttpResponse response = HttpUtils.doGet(host, path, "GET", headers, querys);
            Map<String, Object> result = JSONObject.parseObject(HttpUtils.getResult(response), new TypeReference<Map<String, Object>>() {
            });
            JSONObject jsonObject = (JSONObject) result.get("showapi_res_body");
            int allPages = jsonObject.getIntValue("allPages");
            List<StockBasicInfo> contentlist = jsonObject.getJSONArray("contentlist").toJavaList(StockBasicInfo.class);
            mongoTemplate.insertAll(contentlist);
            for (int i = 2; i <= allPages; i++) {
                querys.put("page", i + "");
                logger.info("获取数据请求参数:{}", querys);
                response = HttpUtils.doGet(host, path, "GET", headers, querys);
                result = JSONObject.parseObject(HttpUtils.getResult(response), new TypeReference<Map<String, Object>>() {
                });
                jsonObject = (JSONObject) result.get("showapi_res_body");
                contentlist = jsonObject.getJSONArray("contentlist").toJavaList(StockBasicInfo.class);
                mongoTemplate.insertAll(contentlist);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void mergeStockData() {
        List<StockPeriod> stockPeriods = mongoTemplate.findAll(StockPeriod.class);
        stockPeriods.forEach(stockPeriod -> {
            Query query = new Query();
            String stockCode = (int) stockPeriod.getCode() + "";
            if (stockCode.length() < 6) {
                stockCode = String.format("%06d", (int) stockPeriod.getCode());
            }
            query.addCriteria(Criteria.where("code").is(stockCode));
            Update update = new Update().set("period", stockPeriod.getPeriod());
            WriteResult writeResult = mongoTemplate.updateFirst(query, update, StockBasicInfo.class);
            logger.info("update stock num:{} stockCode:{}", writeResult.getN(), stockCode);
        });

    }

    public Set<String> getStockList(Predicate<StockBasicInfo> condition) {
        Set<String> codes = Sets.newHashSet();
        List<StockBasicInfo> all = mongoTemplate.findAll(StockBasicInfo.class);
//        all = new ArrayList<>(all.subList(0, 10));
        all.stream().filter(condition).forEach(stockBasicInfo -> codes.add(stockBasicInfo.getCode()));
        return codes;
    }

    public Set<StockBasicInfo> getStockSet(Predicate<StockBasicInfo> condition) {
        Set<StockBasicInfo> codes = Sets.newHashSet();
        List<StockBasicInfo> all = mongoTemplate.findAll(StockBasicInfo.class);
        all.stream().filter(condition).forEach(stockBasicInfo -> codes.add(stockBasicInfo));
        return codes;
    }

    public Set<String> getStockList() {
        return getStockList(stockBasicInfo -> true);
    }




}
