package com.it.bean.analysis;

import com.it.bean.AnalysisTrend;
import com.it.util.DateUtils;

import java.util.*;

/**
 * 趋势一致性
 */
public class OverlapTrend {
    private long startTime;
    private long endTime;
    private List<String> trendIds;
    private Set<String> codes = new HashSet<>();
    private Set<AnalysisTrend> trends = new HashSet<>();

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public List<String> getTrendIds() {
        return trendIds;
    }

    public void setTrendIds(List<String> trendIds) {
        this.trendIds = trendIds;
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
        trends.add(analysisTrend);
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
        return "OverlapTrend{" +
                "startTime=" + DateUtils.toSystemDate(new Date(startTime)) +
                ", endTime=" + DateUtils.toSystemDate(new Date(endTime))
                + "codes " + codes +
                '}';
    }
}
