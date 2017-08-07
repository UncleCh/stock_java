package com.it.bean;

import com.google.common.base.MoreObjects;

/**
 * 连续变化模型
 */
public class ContinueStockDesc {
    private double percent;
    private String startDate;
    private String endDate;
    private double totalPrice;
    private double avgPrice;

    public double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public ContinueStockDesc(double percent, String startDate, String endDate) {
        this.percent = percent;
        this.startDate = startDate;
        this.endDate = endDate;

    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
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