package com.it.bean.analysis;


import com.it.util.DateUtils;

import java.util.Date;

public class TrendOccur {
    //开始时间
    private long startTime;
    //上涨幅度
    private double upTrend;
    //天数
    private int days;

    private String code;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public double getUpTrend() {
        return upTrend;
    }

    public void setUpTrend(double upTrend) {
        this.upTrend = upTrend;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "TrendOccur{" +
                "startTime=" + DateUtils.toSystemDate(new Date(startTime)) +
                ", upTrend=" + upTrend +
                ", days=" + days +
                ", code='" + code + '\'' +
                '}';
    }
}
