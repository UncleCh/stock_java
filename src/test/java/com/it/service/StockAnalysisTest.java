package com.it.service;


import com.it.bean.ContinueStockDesc;
import com.it.bean.SelectStrategyType;
import com.it.bean.Stock;
import com.it.repository.StockRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;


public class StockAnalysisTest extends BaseStockTest{

    @Autowired
    StockAnalysis stockAnalysis;


    @Test
    public void testGetStockDataByContinuePercent() {
        Map<Integer, List<ContinueStockDesc>> result = stockAnalysis.getStockDataByContinuePercent(period, value -> value > 0.1, SelectStrategyType.CONTINUE_GROWTH, stocks);
        Map<Integer, List<ContinueStockDesc>> result1 = stockAnalysis.getStockDataByContinuePercent(period, value -> value > 0.1, SelectStrategyType.CONTINUE_GROWTH_MAX, stocks);
//        Map<Integer, List<LinkedList<Stock>>> fallResult = stockAnalysis.calContinueGrowthByPeriod(period, SelectStrategyType.CONTINUE_FALL_MAX, stocks);
        Map<Integer, List<ContinueStockDesc>> fallResult = stockAnalysis.getStockDataByContinuePercent(period, value -> value < -0.06, SelectStrategyType.CONTINUE_FALL_MAX, stocks);
        Assert.assertTrue(result.size() > 0);
        int mod = stocks.size() % period;
        System.out.println("period:" + period + "mod:" + mod);
        for (Map.Entry<Integer, List<ContinueStockDesc>> entry : fallResult.entrySet()) {
            entry.getValue().sort((o1, o2) -> o1.getPercent() - o2.getPercent() > 0 ? 1 : -1);
        }
        Assert.assertTrue(mod <= period);
        System.out.println(result1);
        System.out.println(result);
    }

    @Test
    public void testGetMaxPrice(){
        Map<Integer, Double> maxPrice = stockAnalysis.getMaxPrice(period, stocks);
        System.out.println(maxPrice);
    }

}