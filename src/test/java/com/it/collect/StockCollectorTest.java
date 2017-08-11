package com.it.collect;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.it.service.BaseStockTest;
import com.it.util.Constant;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class StockCollectorTest extends BaseStockTest {
    @Autowired
    private StockCollector stockCollector;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void getStockList() throws Exception {
        Set<String> stockList = stockCollector.getStockList();
        Assert.assertTrue(stockList.size() > 0);
        System.out.println(stockList);
    }

    @Test
    public void testMergeStockData() {
        stockCollector.mergeStockData();
    }

    @Test
    public void testCollectGroup() {
        GroupOperation code = Aggregation.group("code").count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(code);
        AggregationResults<Map> ali_stock = mongoTemplate.aggregate(aggregation, "ali_stock", Map.class);
        List<Map> mappedResults = ali_stock.getMappedResults();
        List<String> codes = Lists.newArrayList();
        mappedResults.forEach(map -> codes.add(map.get("_id").toString()));
        Map<String,Object> catchCodes = Maps.newHashMap();
        catchCodes.put(Constant.STOCK_CATCHED,codes);
        catchCodes.put("key",Constant.STOCK_CATCHED);
        mongoTemplate.insert(catchCodes,Constant.STOCK_CONFIG);
        System.out.println(mappedResults.size());
    }

    @Test
    public void test(){
        Set<String> stockList = stockCollector.getStockList();
        List<Map> maps = mongoTemplate.findAll(Map.class, Constant.STOCK_CONFIG);
        List<String> codes = (List<String>) maps.get(0).get(Constant.STOCK_CATCHED);
        stockList.removeAll(codes);
        System.out.println(stockList);
    }

}