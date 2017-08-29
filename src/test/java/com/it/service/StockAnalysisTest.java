package com.it.service;


import com.google.common.primitives.Doubles;
import com.it.bean.*;
import com.it.collect.StockCollector;
import com.it.repository.AnalysisRepository;
import com.it.repository.StockRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.*;


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
    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void testGetStockDataByContinuePercent() {
        Map<Integer, LinkedList<ContinueStockDesc>> result = stockAnalysis.getStockDataByContinuePercent(period, value -> value > 0.1, SelectStrategyType.CONTINUE_GROWTH, stocks);
        Map<Integer, LinkedList<ContinueStockDesc>> result1 = stockAnalysis.getStockDataByContinuePercent(period, value -> value > 0.1, SelectStrategyType.CONTINUE_GROWTH_MAX, stocks);
//        Map<Integer, List<LinkedList<Stock>>> fallResult = stockAnalysis.calContinueGrowthByPeriod(period, SelectStrategyType.CONTINUE_FALL_MAX, stocks);
        Map<Integer, LinkedList<ContinueStockDesc>> fallResult = stockAnalysis.getStockDataByContinuePercent(period, value -> value < -0.06, SelectStrategyType.CONTINUE_FALL_MAX, stocks);
        Assert.assertTrue(result.size() > 0);
        int mod = stocks.size() % period;
        System.out.println("period:" + period + "mod:" + mod);
        for (Map.Entry<Integer, LinkedList<ContinueStockDesc>> entry : fallResult.entrySet()) {
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
        Set<StockBasicInfo> stockSet = stockCollector.getStockSet(stockBasicInfo -> stockBasicInfo.getPeriod() != 0);
        System.out.println(stockSet.size());
        for (StockBasicInfo stock : stockSet) {
            stocks = stockRepository.findByCodeOrderByDateAsc(Double.parseDouble(stock.getCode()));
            Map<Integer, LinkedList<ContinueStockDesc>> result = stockAnalysis.getStockDataByContinuePercent(period, value -> value > 0.1,
                    SelectStrategyType.CONTINUE_GROWTH, stocks);
            List<ContinueStockDesc> continueStockDescs = result.get(result.size());
            AnalysisStock analysisStock = selectService.analysisiStockByPeriod(period,
                    Integer.parseInt(stock.getCode()), continueStockDescs);
            analysisStock.setGrowthMap(result);
            AnalysisStock insert = analysisRepository.insert(analysisStock);
        }
    }

    @Test
    public void selectStock() {
        List<AnalysisStock> all = mongoTemplate.findAll(AnalysisStock.class);
        all.stream().filter(analysisStock -> {
            //限制价格
            return analysisStock.getCurPeriodMaxPrice() > 10 && analysisStock.getCurPeriodMaxPrice() < 25;
        }).filter(analysisStock -> {
            //振幅次数
            return analysisStock.getAmplitudeCount() > 10 && analysisStock.getCurPrice() > 0;
        }).filter(analysisStock -> {
            //相对低值
            return analysisStock.curPeriodMinPecent < 0.2;
        }).filter(analysisStock -> analysisStock.getDays() < 20 )
                .sorted((o1, o2) -> {
                    // 大到小
                    return Doubles.compare(o2.getAvgDayAmplitudeCount(), o1.getAvgDayAmplitudeCount());
                }).sorted((o1, o2) -> Double.compare(o1.getDays(),o2.getDays()))
                .forEach(analysisStock -> {
                    String s = analysisStock.toString();
                    System.out.println(s);
                });

    }

}