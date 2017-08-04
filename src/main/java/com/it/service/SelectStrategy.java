package com.it.service;


import com.it.bean.Stock;

import java.util.LinkedList;
import java.util.List;

/**
 *  查找策略
 */
public interface SelectStrategy {

    List<LinkedList<Stock>>  calContinueGrowth(int curPeriodIndex, List<Stock> stocks);

}
