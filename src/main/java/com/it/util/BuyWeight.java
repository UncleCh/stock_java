package com.it.util;

public enum BuyWeight {

    BUY_FIRST(1), BUY_SECOND(2), BUY_THREE(3);
    private int weiht;

    BuyWeight(int weiht) {
        this.weiht = weiht;
    }

    public int getWeiht() {
        return weiht;
    }
}
