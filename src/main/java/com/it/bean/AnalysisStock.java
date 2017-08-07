package com.it.bean;

import com.google.common.base.MoreObjects;

import java.util.LinkedList;
import java.util.List;

/**
 * 分析模型
 */
public class AnalysisStock {
    // 振幅次数
    private int amplitudeCount;
    // 当前价格，百分比
    private double curPricePercent;

    private double curPeriodMaxPrice;

    private List<Double> maxPriceList = new LinkedList<>();


    public AnalysisStock(int amplitudeCount, double curPricePercent, double curPeriodMaxPrice) {
        this.amplitudeCount = amplitudeCount;
        this.curPricePercent = curPricePercent;
        this.curPeriodMaxPrice = curPeriodMaxPrice;
    }

    public double getCurPeriodMaxPrice() {
        return curPeriodMaxPrice;
    }

    public List<Double> getMaxPriceList() {
        return maxPriceList;
    }

    public void addPrice(double maxPrice) {
        maxPriceList.add(maxPrice);
    }

    public int getAmplitudeCount() {
        return amplitudeCount;
    }

    public double getCurPricePercent() {
        return curPricePercent;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("amplitudeCount", amplitudeCount)
                .add("curPricePercent", curPricePercent)
                .toString();
    }
}
