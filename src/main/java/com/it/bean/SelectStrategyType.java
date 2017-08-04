package com.it.bean;


public enum SelectStrategyType {

    CONTINUE_GROWTH_MAX(1, "连续子序列和"), CONTINUE_FALL_MAX(1, "连续下跌子序列和"),CONTINUE_GROWTH(2, "连续增长子序列");

    private int type;
    private String desc;

    SelectStrategyType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
