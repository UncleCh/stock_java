package com.it.service;


import com.it.bean.Stock;
import com.it.bean.Trend;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WaveTrendAnalysis {
    private Stock start;
    private Stock end;
    private Stock mid;
    private Trend trendType;
    private Queue<Stock> container;
    private int size;
    //波动幅度
    private int wave = 6;

    public WaveTrendAnalysis() {
        this(30);
    }

    public WaveTrendAnalysis(int size) {
        this.size = size;
        container = new ConcurrentLinkedQueue<>();

    }

    public void addStock(Stock stock) {
        if (container.size() >= size) {
            container.remove();
        }
        if (container.isEmpty()) {
            start = stock;
        }
        container.add(stock);
    }

    public void addStocks(List<Stock> stocks) {
        for (Stock temp : stocks) {
            addStock(temp);
        }
    }
}
