package com.it.collect;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.it.bean.StockBasicInfo;
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
        Set<StockBasicInfo> stockList = stockCollector.getStockList();
        Assert.assertTrue(stockList.size() > 0);
        System.out.println(stockList);
    }

    @Test
    public void testMergeStockData() {
        stockCollector.mergeStockData();
    }



    @Test
    public void test(){
        Query query = new Query();

        query.addCriteria(Criteria.where("key").is(Constant.STOCK_NEED_CATCHED));
        List<Map> maps1 = mongoTemplate.find(query, Map.class,Constant.STOCK_CONFIG);
        Set<StockBasicInfo> stockList = stockCollector.getStockList();
        List<Map> maps = mongoTemplate.findAll(Map.class, Constant.STOCK_CONFIG);
        List<String> codes = (List<String>) maps.get(0).get(Constant.STOCK_CATCHED);
        stockList.removeAll(codes);
        System.out.println(stockList);
    }

}