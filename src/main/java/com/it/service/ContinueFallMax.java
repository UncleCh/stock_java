package com.it.service;

import com.google.common.collect.Lists;
import com.it.bean.Stock;
import com.it.util.StockConfig;
import org.aeonbits.owner.ConfigFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ContinueFallMax implements  SelectStrategy {
    @Override
    public List<LinkedList<Stock>> calContinueGrowth(int curPeriodIndex, List<Stock> stocks) {
        List<LinkedList<Stock>> periodResult = Lists.newArrayList();
        LinkedList<Stock> continueGrowth = null;
//        StockConfig stockConfig = ConfigFactory.create(StockConfig.class);
        for (Stock stock : stocks) {
            if (stock.getInc_percent() < 0) {
                if (continueGrowth == null)
                    continueGrowth = Lists.newLinkedList();
                continueGrowth.add(stock);
            } else {
                if (continueGrowth != null && (continueGrowth.size() > 1 || continueGrowth.getFirst().getInc_percent() < -0.02)) {
                    periodResult.add(continueGrowth);
                }
                continueGrowth = null;
            }
        }

        return periodResult;
    }
}
