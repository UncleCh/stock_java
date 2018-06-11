package com.it.bean;


import java.util.Date;

/**
 * 每天数据
 */
public class Daily {
    //股票代码名称
    private String code;
    //最高价
    private double max;
    //最低价
    private double min;
    //开盘价
    private double open;
    //收盘价
    private double close;
    //成交额
    private String trxTotal;
    //成交量
    private String trxAmt;
    //振幅
    private double amplitude;
    //换手率
    private double trxPer;
    //市盈率
    private double peRatio;
    //总市值
    private double total;
    //涨幅
    private double changeP;

    private Date dt;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public String getTrxTotal() {
        return trxTotal;
    }

    public void setTrxTotal(String trxTotal) {
        this.trxTotal = trxTotal;
    }

    public String getTrxAmt() {
        return trxAmt;
    }

    public void setTrxAmt(String trxAmt) {
        this.trxAmt = trxAmt;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public double getTrxPer() {
        return trxPer;
    }

    public void setTrxPer(double trxPer) {
        this.trxPer = trxPer;
    }

    public double getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(double peRatio) {
        this.peRatio = peRatio;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Date getDt() {
        return dt;
    }

    public void setDt(Date dt) {
        this.dt = dt;
    }

    public double getChangeP() {
        return changeP;
    }

    public void setChangeP(double changeP) {
        this.changeP = changeP;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Daily{" +
                "code='" + code + '\'' +
                ", max=" + max +
                ", min=" + min +
                ", open=" + open +
                ", close=" + close +
                ", trxTotal=" + trxTotal +
                ", trxAmt='" + trxAmt + '\'' +
                ", amplitude=" + amplitude +
                ", trxPer=" + trxPer +
                ", peRatio=" + peRatio +
                ", total=" + total +
                ", dt=" + dt +
                '}';
    }
}
