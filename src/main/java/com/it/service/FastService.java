package com.it.service;

import com.it.bean.Stock;
import com.it.collect.StockCollector;
import com.it.repository.DailyMapper;
import com.it.repository.StockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class FastService {
    @Autowired
    StockService stockService;
    @Autowired
    StockCollector stockCollector;
    @Autowired
    AnalysisTrendService analysisTrendService;
    @Autowired
    DailyMapper dailyMapper;
    @Autowired
    StockMapper stockMapper;
    @Autowired
    AnalysisService analysisService;


    public void analysis() {
        String industry = "有色金属";
//        //增加样本，收集数据  002842
        List<Stock> stockList = stockCollector.catchIndustryCode(industry);
//        for (Stock temp : stockList) {
//            stockService.collectHistory(temp);
//        }
//        for (Stock temp : stockList) {
//            List<Daily> dailyList = dailyMapper.getDailyList(temp.getCode(), null, null);
//            analysisTrendService.analysisTrendAndSave(temp, dailyList);
//        }
        industry = "分析样本";
//        analysisTrendService.analysisIndustryTrend(industry);

//        clearAnalysisDate();
        analysisService.analysisTrend(industry);


    }

    public void clearAnalysisDate() {
        List<String> oberverData = Arrays.asList("000960", "600338", "601958", "600549", "000933", "000807",
                "600392", "600111", "600497", "601168", "601600");
        Stock queryParam = new Stock();
        queryParam.setObserverIndustry("分析样本");
        List<Stock> stockList = stockMapper.getStockList(queryParam);
        for (Stock temp : stockList) {
            if (!oberverData.contains(temp.getCode())) {
                temp.setDt(null);
                stockMapper.delete(temp);
            }
        }
    }
}
