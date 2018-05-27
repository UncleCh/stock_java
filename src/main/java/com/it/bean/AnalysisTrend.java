package com.it.bean;

import com.it.util.DateUtils;

public class AnalysisTrend {
    private String id;

    private String code;

    private String trend;

    private String startDt;

    private String endDt;

    private double wave;

    private double max;

    private double min;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend == null ? null : trend.trim();
    }

    public String getStartDt() {
        return startDt;
    }

    public void setStartDt(String startDt) {
        this.startDt = startDt == null ? null : startDt.trim();
    }

    public String getEndDt() {
        return endDt;
    }

    public void setEndDt(String endDt) {
        this.endDt = endDt == null ? null : endDt.trim();
    }

    public Double getWave() {
        return wave;
    }

    public void setWave(Double wave) {
        this.wave = wave;
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

    public long getStartDtTime() {
        return DateUtils.parse(getStartDt()).getTime();
    }
    public long getEndDtTime() {
        return DateUtils.parse(getEndDt()).getTime();
    }

    @Override
    public String toString() {
        return "AnalysisTrend{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", trend='" + trend + '\'' +
                ", startDt='" + startDt + '\'' +
                ", endDt='" + endDt + '\'' +
                ", wave=" + wave +
                ", max=" + max +
                ", min=" + min +
                '}';
    }
}