package com.it.bean;

import com.google.common.base.MoreObjects;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 分析模型
 */
@Document(collection = "analysis_stock")
public class AnalysisStock {

    @Id
    private ObjectId id;
    //  当前周期 振幅次数
    private int amplitudeCount;
    //平均每天的振幅周期
    private double avgDayAmplitudeCount;
    //振幅 间隔时间 过滤为0的数据
    private double days;
    //当前价与最小值相差百分比
    public double curPeriodMinPecent;

    // 当前价格占整个周期百分比
    private double curPrice;
    //当前价与最大值差距百分比
    public double curPeriodPecent;

    public double curPeriodMinPrice;
    public double recentPrice;
    // position /  size  偏小 良好
    private double curPeriodMaxPrice;
    private String code;
    //当前周期最大价格、最小价格相差百分比
    private double curPeriodAmplitude;

    private List<Stock> maxPriceList = new LinkedList<>();

    private String startDate;

    private Map<Integer, LinkedList<ContinueStockDesc>> growthMap;
    private Map<Integer, LinkedList<ContinueStockDesc>> fallMap;

    public AnalysisStock() {
    }

    public AnalysisStock(int amplitudeCount, double curPricePercent, double curPeriodMaxPrice) {
        this.amplitudeCount = amplitudeCount;
        this.curPrice = curPricePercent;
        this.curPeriodMaxPrice = curPeriodMaxPrice;
    }



    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public double getCurPeriodAmplitude() {
        return curPeriodAmplitude;
    }

    public void setCurPeriodAmplitude(double curPeriodAmplitude) {
        this.curPeriodAmplitude = curPeriodAmplitude;
    }

    public double getCurPeriodMaxPrice() {
        return curPeriodMaxPrice;
    }

    public List<Stock> getMaxPriceList() {
        return maxPriceList;
    }

    public void addPrice(Stock maxPrice) {
        maxPriceList.add(maxPrice);
    }

    public int getAmplitudeCount() {
        return amplitudeCount;
    }

    public double getCurPrice() {
        return curPrice;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Map<Integer, LinkedList<ContinueStockDesc>> getGrowthMap() {
        return growthMap;
    }

    public void setGrowthMap(Map<Integer, LinkedList<ContinueStockDesc>> growthMap) {
        this.growthMap = growthMap;
    }

    public Map<Integer, LinkedList<ContinueStockDesc>> getFallMap() {
        return fallMap;
    }

    public void setFallMap(Map<Integer, LinkedList<ContinueStockDesc>> fallMap) {
        this.fallMap = fallMap;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getDays() {
        return days;
    }

    public void setDays(double days) {
        this.days = days;
    }

    public double getCurPeriodPecent() {
        return curPeriodPecent;
    }

    public void setCurPeriodPecent(double curPeriodPecent) {
        this.curPeriodPecent = curPeriodPecent;
    }

    public double getCurPeriodMinPecent() {
        return curPeriodMinPecent;
    }

    public void setCurPeriodMinPecent(double curPeriodMinPecent) {
        this.curPeriodMinPecent = curPeriodMinPecent;
    }

    public double getCurPeriodMinPrice() {
        return curPeriodMinPrice;
    }

    public void setCurPeriodMinPrice(double curPeriodMinPrice) {
        this.curPeriodMinPrice = curPeriodMinPrice;
    }

    public double getRecentPrice() {
        return recentPrice;
    }

    public void setRecentPrice(double recentPrice) {
        this.recentPrice = recentPrice;
    }
    //    private int calGoodMarket(){
////        com.google.common.collect.ImmutableSet.of("2005年6月6日","2008年10月28日","2012年12月4日","2013年6月25日")
//    }


    public double getAvgDayAmplitudeCount() {
        return avgDayAmplitudeCount;
    }

    public void setAvgDayAmplitudeCount(double avgDayAmplitudeCount) {
        this.avgDayAmplitudeCount = avgDayAmplitudeCount;
    }

    @Override
    public String toString() {
        return "AnalysisStock{" +
                " amplitudeCount=" + amplitudeCount +
                ", avgDayAmplitudeCount=" + avgDayAmplitudeCount +
                ", days=" + days +
                ", curPrice=" + curPrice +
                ", curPeriodPecent=" + curPeriodPecent +
                ", curPeriodMinPecent=" + curPeriodMinPecent +
                ", curPeriodMinPrice=" + curPeriodMinPrice +
                ", recentPrice=" + recentPrice +
                ", curPeriodMaxPrice=" + curPeriodMaxPrice +
                ", code='" + code + '\'' +
                ", curPeriodAmplitude=" + curPeriodAmplitude +
                ", startDate='" + startDate + '\'' +
                '}';
    }
}
