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
    // 当前价格，百分比
    private double curPricePercent;

    private double curPeriodMaxPrice;
    private String code;
    //当前周期最大价格、最小价格相差百分比
    private double curPeriodAmplitude;

    private List<Stock> maxPriceList = new LinkedList<>();

    private String startDate;
    private Map<Integer, List<ContinueStockDesc>> growthMap;
    private Map<Integer, List<ContinueStockDesc>> fallMap;


    public AnalysisStock(int amplitudeCount, double curPricePercent, double curPeriodMaxPrice) {
        this.amplitudeCount = amplitudeCount;
        this.curPricePercent = curPricePercent;
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

    public double getCurPricePercent() {
        return curPricePercent;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Map<Integer, List<ContinueStockDesc>> getGrowthMap() {
        return growthMap;
    }

    public void setGrowthMap(Map<Integer, List<ContinueStockDesc>> growthMap) {
        this.growthMap = growthMap;
    }

    public Map<Integer, List<ContinueStockDesc>> getFallMap() {
        return fallMap;
    }

    public void setFallMap(Map<Integer, List<ContinueStockDesc>> fallMap) {
        this.fallMap = fallMap;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("amplitudeCount", amplitudeCount)
                .add("curPricePercent", curPricePercent)
                .toString();
    }
}
