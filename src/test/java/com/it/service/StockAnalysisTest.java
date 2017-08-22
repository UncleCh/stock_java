package com.it.service;


import com.it.bean.*;
import com.it.collect.StockCollector;
import com.it.repository.AnalysisRepository;
import com.it.repository.StockRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class StockAnalysisTest extends BaseStockTest {

    @Autowired
    StockAnalysis stockAnalysis;
    @Autowired
    StockCollector stockCollector;
    @Autowired
    AnalysisRepository analysisRepository;
    @Autowired
    StockRepository stockRepository;
    @Autowired
    private StockSelectService selectService;

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
    public void testGetMaxPrice() {
        Map<Integer, Stock> maxPrice = stockAnalysis.getMaxPrice(period, stocks);
        System.out.println(maxPrice);
    }


    @Test
    public void testAnalysis() {
        stockCollector.initCatchedStock();
//        Set<StockBasicInfo> stockSet = stockCollector.getStockSet(stockBasicInfo -> stockBasicInfo.getPeriod() != 0);
//        System.out.println(stockSet.size());
////        for (StockBasicInfo stock : stockSet) {
//            stocks = stockRepository.findByCodeOrderByDateAsc(600129);
//            Map<Integer, List<ContinueStockDesc>> result = stockAnalysis.
//                    getStockDataByContinuePercent(period, value -> value > 0.1,
//                            SelectStrategyType.CONTINUE_GROWTH, stocks);
//            List<ContinueStockDesc> continueStockDescs = result.get(result.size());
//            AnalysisStock analysisStock = selectService.analysisiStockByPeriod(period,
//                    Integer.parseInt(600129+""), continueStockDescs);
//            analysisStock.setGrowthMap(result);
//            AnalysisStock insert = analysisRepository.insert(analysisStock);
//        }
    }

}