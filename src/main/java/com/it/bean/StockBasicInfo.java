package com.it.bean;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stock_list")
public class StockBasicInfo {
    @Id
    private ObjectId id;

    private String market;
    private String name;
    //流通股本，万股
    private String currcapital;
    //四季度净利润（亿元）
    private String profit_four;
    //上市日期
    private String listing_date;
    private String code;
    //总股本，万股
    private String totalcapital;
    //每股净资产（元）
    private String mgjzc;
    private String pinyin;

    private int period;
    //总市值
    private double totalPrice;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrcapital() {
        return currcapital;
    }

    public void setCurrcapital(String currcapital) {
        this.currcapital = currcapital;
    }

    public String getProfit_four() {
        return profit_four;
    }

    public void setProfit_four(String profit_four) {
        this.profit_four = profit_four;
    }

    public String getListing_date() {
        return listing_date;
    }

    public void setListing_date(String listing_date) {
        this.listing_date = listing_date;
    }

    public String getCode() {
        if (code.length() < 6)
            return String.format("%06s", code);
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTotalcapital() {
        return totalcapital;
    }

    public void setTotalcapital(String totalcapital) {
        this.totalcapital = totalcapital;
    }

    public String getMgjzc() {
        return mgjzc;
    }

    public void setMgjzc(String mgjzc) {
        this.mgjzc = mgjzc;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
