package com.it.bean;

import java.math.BigDecimal;

/**
 * 个股跟踪
 */
public class CodeObserver {
    private String stockName;
    private BigDecimal closePrice;
    //复合增长率
    private BigDecimal incrPer;
    //滚动PE
    private BigDecimal pe;
    //滚动PEG
    private BigDecimal recPeg;
    private BigDecimal tenDay;
    private String industry;
    private String detailIndustry;
    private BigDecimal lastClosePrice;
    private String code;
    private String market;

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public BigDecimal getIncrPer() {
        return incrPer;
    }

    public void setIncrPer(BigDecimal incrPer) {
        this.incrPer = incrPer;
    }

    public BigDecimal getPe() {
        return pe;
    }

    public void setPe(BigDecimal pe) {
        this.pe = pe;
    }

    public BigDecimal getRecPeg() {
        return recPeg;
    }

    public void setRecPeg(BigDecimal recPeg) {
        this.recPeg = recPeg;
    }

    public BigDecimal getTenDay() {
        return tenDay;
    }

    public void setTenDay(BigDecimal tenDay) {
        this.tenDay = tenDay;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getDetailIndustry() {
        return detailIndustry;
    }

    public void setDetailIndustry(String detailIndustry) {
        this.detailIndustry = detailIndustry;
    }

    public BigDecimal getLastClosePrice() {
        return lastClosePrice;
    }

    public void setLastClosePrice(BigDecimal lastClosePrice) {
        this.lastClosePrice = lastClosePrice;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }
}
