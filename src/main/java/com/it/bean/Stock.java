package com.it.bean;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "ali_stock")
public class Stock {

    @Id
    private ObjectId id;
    //最低价
    private double min_price;
    //市场，例如sh
    private String market;
    //交易手数
    private int trade_num;
    //交易金额元
    private double trade_money;
    //收盘价
    private double close_price;
    //开盘价
    private double open_price;
    //股票代码
    private double code;
    //最高价
    private double max_price;
    //日期，例如2015-09-02
    private String date;
    //增长百分比
    private double inc_percent;
    //振幅
    private double max_min_percent;

    public double getInc_percent() {
        return inc_percent;
    }

    public void setInc_percent(double inc_percent) {
        this.inc_percent = inc_percent;
    }

    public double getMax_min_percent() {
        return max_min_percent;
    }

    public void setMax_min_percent(double max_min_percent) {
        this.max_min_percent = max_min_percent;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public double getMin_price() {
        return min_price;
    }

    public void setMin_price(double min_price) {
        this.min_price = min_price;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public int getTrade_num() {
        return trade_num;
    }

    public void setTrade_num(int trade_num) {
        this.trade_num = trade_num;
    }

    public double getTrade_money() {
        return trade_money;
    }

    public void setTrade_money(double trade_money) {
        this.trade_money = trade_money;
    }

    public double getClose_price() {
        return close_price;
    }

    public void setClose_price(double close_price) {
        this.close_price = close_price;
    }

    public double getOpen_price() {
        return open_price;
    }

    public void setOpen_price(double open_price) {
        this.open_price = open_price;
    }

    public double getCode() {
        return code;
    }

    public void setCode(double code) {
        this.code = code;
    }

    public double getMax_price() {
        return max_price;
    }

    public void setMax_price(double max_price) {
        this.max_price = max_price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(date);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Stock)
            return ((Stock) obj).getDate().equals(getDate());
        return false;
    }
}
