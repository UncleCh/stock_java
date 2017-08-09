package com.it.collect;

import com.it.service.BaseStockTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;


public class StockCollectorTest extends BaseStockTest {
    @Autowired
    private StockCollector stockCollector;

    @Test
    public void getStockList() throws Exception {
        Set<String> stockList = stockCollector.getStockList();
        Assert.assertTrue(stockList.size() > 0);
        System.out.println(stockList);
    }

}