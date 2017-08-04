package com.it.service;


import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.it.bean.SelectStrategyType;
import com.it.bean.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.DoublePredicate;

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


    public Map<Integer, List<ContinueStockDesc>> getStockDataByContinuePercent(int period, DoublePredicate predicate, SelectStrategyType strategyType, List<Stock> stocks) {
        Map<Integer, List<LinkedList<Stock>>> periodResult = calContinueGrowthByPeriod(period, strategyType, stocks);
        if (periodResult.size() == 0)
            return Maps.newHashMap();
        Map<Integer, List<ContinueStockDesc>> result = Maps.newHashMap();
        for (Map.Entry<Integer, List<LinkedList<Stock>>> entry : periodResult.entrySet()) {
            double tempContinueIncrePercent = 0;
            List<LinkedList<Stock>> curLinkedList = entry.getValue();
            List<ContinueStockDesc> periodLists = Lists.newArrayList();
            for (LinkedList<Stock> stock : curLinkedList) {
                for (Stock continuStock : stock) {
                    tempContinueIncrePercent += continuStock.getInc_percent();
                }
                if (predicate.test(tempContinueIncrePercent)) {
                    ContinueStockDesc continueStockDesc = new ContinueStockDesc(tempContinueIncrePercent, stock.getFirst().getDate(), stock.getLast().getDate());
                    periodLists.add(continueStockDesc);
                }
                tempContinueIncrePercent = 0;
            }
            result.put(entry.getKey(), periodLists);
        }
        return result;
    }

    public class ContinueStockDesc {
        private double percent;
        private String startDate;
        private String endDate;


        private ContinueStockDesc(double percent, String startDate, String endDate) {
            this.percent = percent;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public double getPercent() {

            return percent;
        }

        public void setPercent(double percent) {
            this.percent = percent;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("percent", percent)
                    .add("startDate", startDate)
                    .add("endDate", endDate).toString();
        }
    }
}
