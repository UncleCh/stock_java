package com.it.bean.analysis;

import com.alibaba.fastjson.JSONObject;
import com.it.bean.AnalysisTrend;
import com.it.util.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 趋势一致性
 */
public class OverlapTrend {
    private long startTime;
    private long endTime;
    private String trendIds;
    private String industry;
    private Date startDt;
    private Date endDt;

    private Set<String> codes = new HashSet<>();
    private Set<AnalysisTrend> trends = new HashSet<>();

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        startDt = new Date(startTime);
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        endDt = new Date(endTime);
        this.endTime = endTime;
    }


    public Set<String> getCodes() {
        return codes;
    }

    public void setCodes(Set<String> codes) {
        this.codes = codes;
    }

    public void addCode(String code) {
        this.codes.add(code);
    }

    public void addTrend(AnalysisTrend analysisTrend) {
        industry = analysisTrend.getObserverIndustry();
        trends.add(analysisTrend);
    }

    public Set<AnalysisTrend> getTrends() {
        return trends;
    }

    public void setTrends(Set<AnalysisTrend> trends) {
        this.trends = trends;
    }

    public String getTrendIds() {
        return StringUtils.isNotEmpty(trendIds) ?
                trendIds : JSONObject.toJSONString(trends.stream().map(AnalysisTrend::getId).collect(Collectors.toList()));
    }

    public void setTrendIds(String trendIds) {
        this.trendIds = trendIds;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public Date getStartDt() {
        return startDt;
    }

    public void setStartDt(Date startDt) {
        this.startDt = startDt;
    }

    public Date getEndDt() {
        return endDt;
    }

    public void setEndDt(Date endDt) {
        this.endDt = endDt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OverlapTrend that = (OverlapTrend) o;
        return startTime == that.startTime &&
                endTime == that.endTime &&
                codes.equals(that.getCodes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, codes);
    }

    @Override
    public String toString() {
        return "\n OverlapTrend{" +
                "startTime=" + DateUtils.toSystemDate(new Date(startTime)) +
                ", endTime=" + DateUtils.toSystemDate(new Date(endTime))
                + "codes " + trends +
                '}';
    }
}
