package com.it.bean;


import java.util.Date;

/**
 * 财务分析
 */
public class Finance {
    //股票代码
    private String code;
    //yyyy-MM-dd
    private Date dt;
    //净收入
    private double income;
    //净收入百分比
    private double incomePer;
    //营业收入
    private double marketIncome;
    //营业收入百分比
    private double marketIncomePer;
    //每股收入
    private double perIncome;
    //每股净资产
    private double perAssets;
    //每股现金流
    private double perCacsh;
    //净资产收益率
    private double selfAssetsPer;
    //毛利率
    private double selfMarketPer;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDt() {
        return dt;
    }

    public void setDt(Date dt) {
        this.dt = dt;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getIncomePer() {
        return incomePer;
    }

    public void setIncomePer(double incomePer) {
        this.incomePer = incomePer;
    }

    public double getMarketIncome() {
        return marketIncome;
    }

    public void setMarketIncome(double marketIncome) {
        this.marketIncome = marketIncome;
    }

    public double getMarketIncomePer() {
        return marketIncomePer;
    }

    public void setMarketIncomePer(double marketIncomePer) {
        this.marketIncomePer = marketIncomePer;
    }

    public double getPerIncome() {
        return perIncome;
    }

    public void setPerIncome(double perIncome) {
        this.perIncome = perIncome;
    }

    public double getPerAssets() {
        return perAssets;
    }

    public void setPerAssets(double perAssets) {
        this.perAssets = perAssets;
    }

    public double getPerCacsh() {
        return perCacsh;
    }

    public void setPerCacsh(double perCacsh) {
        this.perCacsh = perCacsh;
    }

    public double getSelfAssetsPer() {
        return selfAssetsPer;
    }

    public void setSelfAssetsPer(double selfAssetsPer) {
        this.selfAssetsPer = selfAssetsPer;
    }

    public double getSelfMarketPer() {
        return selfMarketPer;
    }

    public void setSelfMarketPer(double selfMarketPer) {
        this.selfMarketPer = selfMarketPer;
    }
}
