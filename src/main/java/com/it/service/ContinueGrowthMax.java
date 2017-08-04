package com.it.service;

import com.google.common.collect.Lists;
import com.it.bean.Stock;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
class ContinueGrowthMax implements SelectStrategy {

    @Override
    public List<LinkedList<Stock>> calContinueGrowth(int curPeriodIndex, List<Stock> stocks) {
        double maxSum = 0, thisSum = 0;
        List<LinkedList<Stock>> periodResult = Lists.newArrayList();
        LinkedList<Stock> continueGrowth = Lists.newLinkedList();
        for (int i = 0, j = 0; j < stocks.size(); j++) {
            thisSum += stocks.get(j).getInc_percent();
            if (thisSum > maxSum) {
                maxSum = thisSum;
                continueGrowth.add(stocks.get(j));
            } else if (thisSum < 0) {
                i = j + 1;
                thisSum = 0;
                continueGrowth.clear();
            }
        }
        periodResult.add(continueGrowth);
        return periodResult;
    }

}
