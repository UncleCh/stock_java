package com.it.service;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.it.bean.ContinueStockDesc;
import com.it.bean.SelectStrategyType;
import com.it.bean.Stock;
import com.it.service.analysis.SelectStrategy;
import com.it.service.analysis.SelectStrategyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.DoublePredicate;
import java.util.function.Function;

@Service
public class StockAnalysis {

    @Autowired
    private SelectStrategyManager strategyManager;

    public Map<Integer, List<LinkedList<Stock>>> calContinueGrowthByPeriod(int period, SelectStrategyType strategyType, List<Stock> stocks) {
        Map<Integer, List<LinkedList<Stock>>> result = Maps.newHashMap();
        int endPosition, startPostion = 0, curPeriodIndex = 1;
        if (stocks.size() < period)
            endPosition = stocks.size();
        else
            endPosition = period;
        while (endPosition <= stocks.size()) {
            SelectStrategy selectStrategy = strategyManager.getSelectStrategy(strategyType);
            result.put(curPeriodIndex, selectStrategy.calContinueGrowth(curPeriodIndex, new ArrayList<>(stocks.subList(startPostion, endPosition))));
            curPeriodIndex++;
            startPostion = endPosition;
            if (stocks.size() < endPosition + period) {
                if (stocks.size() - endPosition == 0)
                    break;
                endPosition += stocks.size() - endPosition;

            } else
                endPosition += period;
        }
        return result;
    }


    public Map<Integer, LinkedList<ContinueStockDesc>> getStockDataByContinuePercent(int period, DoublePredicate predicate, SelectStrategyType strategyType, List<Stock> stocks) {
        Map<Integer, List<LinkedList<Stock>>> periodResult = calContinueGrowthByPeriod(period, strategyType, stocks);
        if (periodResult.size() == 0)
            return Maps.newHashMap();
        Map<Integer, LinkedList<ContinueStockDesc>> result = Maps.newHashMap();
        for (Map.Entry<Integer, List<LinkedList<Stock>>> entry : periodResult.entrySet()) {
            double tempContinueIncrePercent = 0, totalPrice = 0, days = 0;
            List<LinkedList<Stock>> curLinkedList = entry.getValue();
            LinkedList<ContinueStockDesc> periodLists = Lists.newLinkedList();
            for (LinkedList<Stock> stock : curLinkedList) {
                for (Stock continuStock : stock) {
                    tempContinueIncrePercent += continuStock.getInc_percent();
                    totalPrice += continuStock.getClose_price();
                    days++;
                }
                if (predicate.test(tempContinueIncrePercent)) {
                    ContinueStockDesc continueStockDesc = new ContinueStockDesc(tempContinueIncrePercent, stock.getFirst().getDate(), stock.getLast().getDate());
                    continueStockDesc.setTotalPrice(totalPrice);
                    continueStockDesc.setAvgPrice(totalPrice / days);
                    periodLists.add(continueStockDesc);
                }
                tempContinueIncrePercent = 0;
                totalPrice = 0;
                days = 0;
            }
            result.put(entry.getKey(), periodLists);
        }
        return result;
    }

    public Map<Integer, Stock> getMaxPrice(int period, List<Stock> stocks) {
        return getStockByOperator(period, stocks, stocks1 -> {
            Stock maxStock = new Stock();
            for (Stock stock : stocks1) {
                if (stock.getMax_price() > maxStock.getMax_price())
                    maxStock = stock;
            }
            return maxStock;
        });
    }

    public Map<Integer, Stock> getMinPrice(int period, List<Stock> stocks) {
        return getStockByOperator(period, stocks, stocks1 -> {
            Stock minStock = stocks1.get(0);
            for (Stock stock : stocks1) {
                if (stock.getMin_price() < minStock.getMin_price())
                    minStock = stock;
            }
            return minStock;
        });
    }

    public Map<Integer, Stock> getStockByOperator(int period, List<Stock> stocks, Function<List<Stock>, Stock> function) {

        Map<Integer, Stock> result = Maps.newHashMap();
        int endPosition, startPostion = 0, curPeriodIndex = 1;
        if (stocks.size() < period)
            endPosition = stocks.size();
        else
            endPosition = period;
        while (endPosition <= stocks.size()) {
            Stock maxStock = function.apply(new ArrayList<>(stocks.subList(startPostion, endPosition)));
            result.put(curPeriodIndex, maxStock);
            curPeriodIndex++;
            startPostion = endPosition;
            if (stocks.size() < endPosition + period) {
                if (stocks.size() - endPosition == 0)
                    break;
                endPosition += stocks.size() - endPosition;

            } else
                endPosition += period;
        }
        return result;
    }


}
