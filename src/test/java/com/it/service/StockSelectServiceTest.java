package com.it.service;

import com.it.bean.AnalysisStock;
import com.it.bean.ContinueStockDesc;
import com.it.bean.SelectStrategyType;
import com.it.bean.Stock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StockSelectServiceTest extends BaseStockTest{

    @Autowired
    private StockSelectService selectService;
    @Autowired
    private StockAnalysis stockAnalysis;

    @Test
    public void selectStockByPeriod() throws Exception {
        Map<Integer, List<ContinueStockDesc>> result = stockAnalysis.getStockDataByContinuePercent(period, value -> value > 0.1, SelectStrategyType.CONTINUE_GROWTH, stocks);
        List<ContinueStockDesc> continueStockDescs = result.get(result.size());
        AnalysisStock analysisStock = selectService.selectStockByPeriod(continueStockDescs);
        System.out.println(analysisStock);
    }

}