package com.it.bean;


public enum Trend {
    UP("上升"), DOWN("下降"), WAVE("波动");

    Trend(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

}
