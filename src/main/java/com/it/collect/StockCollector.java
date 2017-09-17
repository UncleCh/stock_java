package com.it.collect;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Sets;
import com.it.bean.StockBasicInfo;
import com.it.bean.StockClassification;
import com.it.bean.StockInfo;
import com.it.bean.StockPeriod;
import com.it.repository.StockBasicRepository;
import com.it.repository.StockCollectRepository;
import com.it.repository.StockRepository;
import com.it.util.Constant;
import com.it.util.HttpUtils;
import com.it.util.StockConfig;
import com.mongodb.WriteResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.assertj.core.util.Lists;
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
import java.util.stream.Collectors;

@Service
public class StockCollector {

    private StockRepository stockRepository;
    private MongoTemplate mongoTemplate;
    @Autowired
    private StockBasicRepository stockBasicRepository;
    @Autowired
    StockCollectRepository collectRepository;
    @Autowired
    StockResource stockDao;
    private Logger logger = LoggerFactory.getLogger(StockCollector.class);

    @Autowired
    public StockCollector(MongoTemplate mongoTemplate, StockRepository stockRepository) {
        this.mongoTemplate = mongoTemplate;
        this.stockRepository = stockRepository;
    }

    public void initHistoryStock() {
        StockConfig stockConfig = StockConfig.getConfig();
        String stockCode = stockConfig.stockCodeList();
        Spider.create(new PagerProcess(this, mongoTemplate)).addUrl(stockConfig.historyStockUrl() + stockCode).
                addPipeline(new StockPipeline(stockRepository)).thread(1).run();
    }

    /**
     * sh、sz、hk
     *
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

    public List<StockBasicInfo> getStockList(Predicate<StockBasicInfo> condition) {
        List<StockBasicInfo> codes = Lists.newArrayList();
        List<StockBasicInfo> all = mongoTemplate.findAll(StockBasicInfo.class);
        all = new ArrayList<>(all.subList(0,3));
        if(condition == null)
            condition = stockBasicInfo -> true;
        all.stream().filter(condition).forEach(stockBasicInfo -> codes.add(stockBasicInfo));
        return codes;
    }

    public Set<StockBasicInfo> getStockSet(Predicate<StockBasicInfo> condition) {
        Set<StockBasicInfo> codes = Sets.newHashSet();
        List<StockBasicInfo> all = mongoTemplate.findAll(StockBasicInfo.class);
        all.stream().filter(condition).forEach(stockBasicInfo -> codes.add(stockBasicInfo));
        return codes;
    }




    public Set<StockBasicInfo> getUnCatchStockCode() {
        StockClassification stockClassification = stockClassification();
        Set<StockBasicInfo> needList = stockClassification.getNeedList();
        Set<StockBasicInfo> catchedStock = collectRepository.getCatchedStockBasicInfo();
        needList.removeAll(catchedStock);
        return needList;
    }

    /**
     * 对股票列表做一个基本分类
     * @return
     */
    public StockClassification stockClassification() {
        List<StockBasicInfo> stockList = getStockList(null);
        StockClassification stockFilter = new StockClassification();
        List<StockBasicInfo> objects = Lists.newArrayList();
        objects.addAll(stockList);
        List<StockInfo> stockInfo = stockDao.getRealStockInfo(objects);
        for (StockBasicInfo basicInfo : stockList) {
            double totalPrice = basicInfo.getTotalPrice();
            if (totalPrice <= 0)
                totalPrice = getStockClosePrice(basicInfo, stockInfo) * Double.parseDouble(StringUtils.
                        defaultIfEmpty(basicInfo.getTotalcapital(), "0"));
            if (totalPrice < Constant.GARBAGE_PRICE) {
                stockFilter.addGarbageList(basicInfo);
                logger.info("垃圾股: {} 总市值:{}", basicInfo.getCode(),totalPrice);
            } else {
                stockFilter.addNeedList(basicInfo);
            }
            if (basicInfo.getTotalPrice() <= 0) {
                basicInfo.setTotalPrice(totalPrice);
                stockFilter.addNeedUpdateList(basicInfo);
            }
        }
        return stockFilter;
    }

    private double getStockClosePrice(StockBasicInfo basicInfo, List<StockInfo> stockInfo) {
        List<StockInfo> collect = stockInfo.stream().filter(stockInfo1 -> stockInfo1.getCode().equals(basicInfo.getCode())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect))
            return Double.parseDouble(collect.get(0).getClosePrice());
        return 0;
    }



    /**
     * @param stocks 股票编码 。多个股票代码间以英文逗号分隔，最多输入50个代码。
     * @return List<StockInfo>
     */
    public List<StockInfo> getStockInfo(String stocks) {
        StockConfig config = StockConfig.getConfig();
        String host = config.stockHost();
        String path = "batch-real-stockinfo";
        String method = "GET";
        String appcode = config.appCode();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<>();
        querys.put("needIndex", "0");
        querys.put("stocks", stocks);
        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            String result = HttpUtils.getResult(response);
            Map<String, Object> resultMap = JSONObject.parseObject(result, new TypeReference<Map<String, Object>>() {
            });
            JSONObject showapi_res_body = (JSONObject) resultMap.get("showapi_res_body");
            return showapi_res_body.getJSONArray("list").toJavaList(StockInfo.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }








    private double getStockClosePrice(String code, int count, String host) {
        StockConfig stockConfig = StockConfig.getConfig();
        if (StringUtils.isEmpty(host)) {
            host = stockConfig.stockHost();
        }
        String path = "/real-stockinfo";
        String method = "GET";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + stockConfig.appCode());
        Map<String, String> querys = new HashMap<>();
        querys.put("code", code);
        querys.put("needIndex", "0");
        querys.put("need_k_pic", "0");
        try {
            Thread.sleep(500);
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            String result = HttpUtils.getResult(response);
            logger.info("------ result:{}", result);
            if (StringUtils.isNotEmpty(result)) {
                Map<String, Object> stringObjectMap = JSONObject.parseObject(result, new TypeReference<Map<String, Object>>() {
                });
                JSONObject resJson = (JSONObject) stringObjectMap.get("showapi_res_body");
                Integer retCode = resJson.getInteger("ret_code");
                if (retCode == 0) {
                    Map<String, String> stockMarket = (Map<String, String>) resJson.get("stockMarket");
                    if (MapUtils.isNotEmpty(stockMarket))
                        return Double.parseDouble(stockMarket.get("closePrice"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            logger.info("请求异常，等待6秒");
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (count > 2) {
            return getStockClosePrice(code, ++count, stockConfig.stockHost());
        }
        return getStockClosePrice(code, ++count, null);
    }


}
