package com.it.collect;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.it.bean.StockBasicInfo;
import com.it.bean.StockPeriod;
import com.it.repository.StockBasicRepository;
import com.it.repository.StockRepository;
import com.it.util.Constant;
import com.it.util.HttpUtils;
import com.it.util.StockConfig;
import com.mongodb.WriteResult;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
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
    @Autowired
    private StockBasicRepository stockBasicRepository;
    private Logger logger = LoggerFactory.getLogger(StockCollector.class);

    @Autowired
    public StockCollector(MongoTemplate mongoTemplate, StockRepository stockRepository) {
        this.mongoTemplate = mongoTemplate;
        this.stockRepository = stockRepository;
    }

    public void initHistoryStock() {
        StockConfig stockConfig = ConfigFactory.create(StockConfig.class);
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

    public Set<StockBasicInfo> getStockList(Predicate<StockBasicInfo> condition) {
        Set<StockBasicInfo> codes = Sets.newHashSet();
        List<StockBasicInfo> all = mongoTemplate.findAll(StockBasicInfo.class);
//        all = new ArrayList<>(all.subList(0, 3));
        all.stream().filter(condition).forEach(stockBasicInfo -> codes.add(stockBasicInfo));
        return codes;
    }

    public Set<StockBasicInfo> getStockSet(Predicate<StockBasicInfo> condition) {
        Set<StockBasicInfo> codes = Sets.newHashSet();
        List<StockBasicInfo> all = mongoTemplate.findAll(StockBasicInfo.class);
        all.stream().filter(condition).forEach(stockBasicInfo -> codes.add(stockBasicInfo));
        return codes;
    }

    public Set<StockBasicInfo> getStockList() {
        return getStockList(stockBasicInfo -> true);
    }


    public Set<StockBasicInfo> getUnCatchStockCode() {
        Map<String, Set<StockBasicInfo>> conditionStockList = getConditionStockList();
        Map<String, Object> catchCodes = Maps.newHashMap();
        catchCodes.put(Constant.STOCK_GARBAGE_CATCHED, conditionStockList.get(Constant.STOCK_GARBAGE_CATCHED));
        catchCodes.put("key", Constant.STOCK_GARBAGE_CATCHED);
        mongoTemplate.insert(catchCodes, Constant.STOCK_CONFIG);
        Set<StockBasicInfo> stockList = conditionStockList.get(Constant.STOCK_NEED_CATCHED);
        catchCodes = Maps.newHashMap();
        catchCodes.put(Constant.STOCK_NEED_CATCHED, stockList);
        catchCodes.put("key", Constant.STOCK_NEED_CATCHED);
        mongoTemplate.insert(catchCodes, Constant.STOCK_CONFIG);
        Query query = new Query();
        query.addCriteria(Criteria.where("key").is(Constant.STOCK_NEED_CATCHED));
        List<Map> maps = mongoTemplate.find(query, Map.class, Constant.STOCK_CONFIG);
        List<String> codes = (List<String>) maps.get(0).get(Constant.STOCK_CATCHED);
        if (CollectionUtils.isNotEmpty(codes)) {
            List<StockBasicInfo> temp = Lists.newArrayList();
            for (String code : codes) {
                StockBasicInfo stockBasicInfo = stockBasicRepository.findByCode(Double.parseDouble(code));
                temp.add(stockBasicInfo);
            }
            stockList.removeAll(temp);
        }
        return stockList;
    }

    //去除垃圾股,更新股票列表
    public Map<String, Set<StockBasicInfo>> getConditionStockList() {
        Set<StockBasicInfo> stockList = getStockList();
        Set<StockBasicInfo> garbageList = Sets.newHashSet();
        Set<StockBasicInfo> needList = Sets.newHashSet();
        Set<StockBasicInfo> needUpdateList = Sets.newHashSet();
        for (StockBasicInfo basicInfo : stockList) {
            double stockClosePrice = getStockClosePrice(basicInfo.getCode(), 1, null);
            double totalPrice = stockClosePrice * Double.parseDouble(StringUtils.defaultIfEmpty(basicInfo.getTotalcapital(),"0"));
            if (totalPrice < Constant.GARBAGE_PRICE) {
                garbageList.add(basicInfo);
                logger.info("垃圾股: {}", basicInfo.getCode());
            } else {
                needList.add(basicInfo);
            }
            if (basicInfo.getTotalPrice() != 0) {
                basicInfo.setTotalPrice(totalPrice);
                needUpdateList.add(basicInfo);
            }
        }
        Map<String, Set<StockBasicInfo>> result = Maps.newHashMap();
        result.put(Constant.STOCK_NEED_CATCHED, needList);
        result.put(Constant.STOCK_GARBAGE_CATCHED, garbageList);
        if (CollectionUtils.isNotEmpty(needUpdateList))
            stockBasicRepository.save(needUpdateList);
        return result;
    }

    public void initCatchedStock() {
        Query query = new Query();
        query.addCriteria(Criteria.where("key").is(Constant.STOCK_CATCHED));
        mongoTemplate.remove(query, Constant.STOCK_CONFIG);
        GroupOperation code = Aggregation.group("code").count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(code);
        AggregationResults<Map> ali_stock = mongoTemplate.aggregate(aggregation, "ali_stock", Map.class);
        List<Map> mappedResults = ali_stock.getMappedResults();
        List<String> codes = Lists.newArrayList();
        mappedResults.forEach(map -> codes.add(map.get("_id").toString()));
        Map<String, Object> catchCodes = Maps.newHashMap();
        catchCodes.put(Constant.STOCK_CATCHED, codes);
        catchCodes.put("key", Constant.STOCK_CATCHED);
        mongoTemplate.insert(catchCodes, Constant.STOCK_CONFIG);
        logger.info("更新已经完成捕获的股票代码：{}", catchCodes);
    }


    private double getStockClosePrice(String code, int count, String host) {
        if (StringUtils.isEmpty(host))
            host = "http://stock.market.alicloudapi.com";
        String path = "/real-stockinfo";
        String method = "GET";
        StockConfig stockConfig = StockConfig.getConfig();
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
