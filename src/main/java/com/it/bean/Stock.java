package com.it.bean;


import java.util.Date;

/**
 * 基本信息
 */
public class Stock {

    //股票代码
    private String code;
    //公司名称
    private String name;
    //日期，例如2015-09-02
    private Date dt;
    //权重
    private double weight;
    //指数代码
    private String indexCode;
    //市场
    private String market;
    //行业
    private String industry;
    //概念
    private String concept;
    private String remark;
    private String observerIndustry;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDt() {
        return dt;
    }

    public void setDt(Date dt) {
        this.dt = dt;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getIndexCode() {
        return indexCode;
    }

    public void setIndexCode(String indexCode) {
        this.indexCode = indexCode;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getObserverIndustry() {
        return observerIndustry;
    }

    public void setObserverIndustry(String observerIndustry) {
        this.observerIndustry = observerIndustry;
    }
}
