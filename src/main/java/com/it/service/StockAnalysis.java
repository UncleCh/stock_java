package com.it.service;


import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.it.bean.Stock;
import com.it.util.StockConfig;
import org.aeonbits.owner.ConfigFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StockAnalysis {
    // 412
    private Map<Integer, List<LinkedList<Stock>>> calContinueGrowthByPeriod(int period, List<Stock> stocks) {
        StockConfig stockConfig = ConfigFactory.create(StockConfig.class);
        Map<Integer, List<LinkedList<Stock>>> result = Maps.newHashMap();
        int endPosition, curPeriodIndex = 1;
        if (stocks.size() < period)
            endPosition = stocks.size();
        else
            endPosition = period;
        while (endPosition <= stocks.size()) {
            calContinueGrowth(curPeriodIndex, stocks, stockConfig, result);
            curPeriodIndex++;
            if (stocks.size() < endPosition + period)
                endPosition += stocks.size();
            else
                endPosition += period;
        }
        return result;
    }

    private void calContinueGrowth(int curPeriodIndex, List<Stock> stocks, StockConfig stockConfig, Map<Integer, List<LinkedList<Stock>>> result) {
        List<LinkedList<Stock>> periodResult = Lists.newArrayList();
        LinkedList<Stock> continueGrowth = null;
        for (Stock stock : stocks) {
            if (stock.getInc_percent() > 0) {
                if (continueGrowth == null)
                    continueGrowth = Lists.newLinkedList();
                continueGrowth.add(stock);
            } else {
                if (continueGrowth != null && (continueGrowth.size() > 1 || continueGrowth.getFirst().getInc_percent() > Integer.parseInt(stockConfig.defaultPercent()))) {
                    periodResult.add(continueGrowth);
                } else {
                    continueGrowth = null;
                }
            }
        }
        result.put(curPeriodIndex, periodResult);
    }

    public Map<Integer, List<ContinueStockDesc>> getStockDataByContinuePercent(int period, double percent, List<Stock> stocks) {
        Map<Integer, List<LinkedList<Stock>>> periodResult = calContinueGrowthByPeriod(period, stocks);
        if (periodResult.size() == 0)
            return Maps.newHashMap();
        Map<Integer, List<ContinueStockDesc>> result = Maps.newHashMap();
        for (Map.Entry<Integer, List<LinkedList<Stock>>> entry : periodResult.entrySet()) {
            int tempContinueIncrePercent = 0;
            List<LinkedList<Stock>> curLinkedList = entry.getValue();
            List<ContinueStockDesc> periodLists = Lists.newArrayList();
            for (LinkedList<Stock> stock : curLinkedList) {
                for (Stock continuStock : stock) {
                    tempContinueIncrePercent += continuStock.getInc_percent();
                }
                if (tempContinueIncrePercent > percent) {
                    ContinueStockDesc continueStockDesc = new ContinueStockDesc(tempContinueIncrePercent, stock.getFirst().getDate(), stock.getLast().getDate());
                    periodLists.add(continueStockDesc);
                }

            }
            result.put(entry.getKey(), periodLists);
        }
        return result;
    }

    public class ContinueStockDesc {
        private double percent;
        private String startDate;
        private String endDate;


        public ContinueStockDesc(double percent, String startDate, String endDate) {
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
