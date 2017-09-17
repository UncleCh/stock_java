package com.it.collect;

import com.it.bean.StockBasicInfo;
import com.it.service.BaseStockTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;


public class StockCollectorTest extends BaseStockTest {
    @Autowired
    private StockCollector stockCollector;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void getStockList() throws Exception {
        List<StockBasicInfo> stockList = stockCollector.getStockList(null);
        Assert.assertTrue(stockList.size() > 0);
        System.out.println(stockList);
    }

    @Test
    public void testMergeStockData() {
        stockCollector.mergeStockData();
    }



    @Test
    public void test(){

    }

}