package com.it.service;


import com.it.bean.Stock;
import com.it.repository.StockRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StockAnalysisTest {

    @Autowired
    StockAnalysis stockAnalysis;
    @Autowired
    StockRepository stockRepository;

    @Test
    public void testGetStockDataByContinuePercent() {
        int period = 412;
        Sort sort = new Sort(Sort.Direction.ASC,"date");
        List<Stock> stocks = stockRepository.findAll(sort);
        Map<Integer, List<StockAnalysis.ContinueStockDesc>> result = stockAnalysis.getStockDataByContinuePercent(period, 0.1, stocks);
        Assert.assertTrue(result.size() > 0);
        System.out.println(result);
    }

}