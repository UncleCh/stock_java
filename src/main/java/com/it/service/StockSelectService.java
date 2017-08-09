package com.it.service;


import com.google.common.collect.Lists;
import com.it.bean.AnalysisStock;
import com.it.bean.ContinueStockDesc;
import com.it.bean.SelectStrategyType;
import com.it.bean.Stock;
import com.it.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class StockSelectService {

    private StockRepository stockRepository;
    private StockAnalysis stockAnalysis;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public StockSelectService(StockRepository stockRepository, StockAnalysis stockAnalysis) {
        this.stockRepository = stockRepository;
        this.stockAnalysis = stockAnalysis;
    }

    public AnalysisStock analysisiStockByPeriod(int period, int stockCode, List<ContinueStockDesc> curPeriod) {
        Sort sort = new Sort(Sort.Direction.ASC, "date");
        Stock stock = new Stock();
        stock.setCode(stockCode);
        List<Stock> stocks = stockRepository.findAll(Example.of(stock),sort);
        Map<Integer, List<ContinueStockDesc>> fallResult = stockAnalysis.getStockDataByContinuePercent(period, value -> value < -0.06, SelectStrategyType.CONTINUE_FALL_MAX, stocks);
        List<ContinueStockDesc> curPeriodStock = fallResult.get(fallResult.size());
        curPeriodStock.addAll(curPeriod);
        ArrayList<Double> prices = Lists.newArrayList();
        curPeriodStock.forEach(continueStockDesc -> prices.add(continueStockDesc.getAvgPrice()));
        Query query = new Query();
        sort = new Sort(Sort.Direction.DESC, "date");
        query = query.with(sort).limit(1);
        Stock recentStock = mongoTemplate.findOne(query, Stock.class);
        prices.add(recentStock.getClose_price());
        prices.sort(Double::compare);
        double i = prices.indexOf(recentStock.getClose_price());
        Map<Integer, Double> curPeriodMaxPrice = stockAnalysis.getMaxPrice(period, stocks);
        AnalysisStock analysisStock = new AnalysisStock(curPeriodStock.size(), i / prices.size(), curPeriodMaxPrice.get(curPeriodMaxPrice.size()));
        for (Map.Entry<Integer, Double> entry : curPeriodMaxPrice.entrySet()) {
            analysisStock.addPrice(entry.getValue());
        }
        analysisStock.setStartDate(stocks.get(0).getDate());
        return analysisStock;
    }
}
