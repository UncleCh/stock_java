package com.it.bean;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * 股票分类
 */
public class StockClassification {
    private Set<StockBasicInfo> needList;
    private Set<StockBasicInfo> garbageList;
    private Set<StockBasicInfo> needUpdateList;

    public StockClassification() {
        this.needList = Sets.newHashSet();
        this.garbageList = Sets.newHashSet();
        this.needUpdateList = Sets.newHashSet();
    }

    public Set<StockBasicInfo> getNeedList() {
        return needList;
    }

    public void addNeedList(StockBasicInfo need) {
        this.needList.add(need);
    }

    public Set<StockBasicInfo> getGarbageList() {
        return garbageList;
    }

    public void addGarbageList(StockBasicInfo garbage) {
        this.garbageList.add(garbage);
    }

    public Set<StockBasicInfo> getNeedUpdateList() {
        return needUpdateList;
    }

    public void addNeedUpdateList(StockBasicInfo needUpdate) {
        this.needUpdateList.add(needUpdate);
    }
}
