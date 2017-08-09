package com.it.bean;

import com.google.common.base.MoreObjects;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;

/**
 * 分析模型
 */
@Document(collection = "analysis_stock")
public class AnalysisStock {

    @Id
    private ObjectId id;
    // 振幅次数
    private int amplitudeCount;
    // 当前价格，百分比
    private double curPricePercent;

    private double curPeriodMaxPrice;

    private List<Double> maxPriceList = new LinkedList<>();

    private String startDate;


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

    public double getCurPeriodMaxPrice() {
        return curPeriodMaxPrice;
    }

    public List<Double> getMaxPriceList() {
        return maxPriceList;
    }

    public void addPrice(double maxPrice) {
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("amplitudeCount", amplitudeCount)
                .add("curPricePercent", curPricePercent)
                .toString();
    }
}
