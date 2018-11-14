package com.it.service;

import com.it.bean.Daily;
import com.it.bean.Stock;
import com.it.bean.StockProperties;
import com.it.bean.analysis.TrendOccurModel;
import com.it.collect.StockCollector;
import com.it.repository.DailyMapper;
import com.it.repository.StockMapper;
import com.it.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
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
    @Autowired
    TrendOccurModel trendOccurModel;


    public void analysis() {
        String industry = "房地产";
//        //增加样本，收集数据  002842
        List<Stock> stockList = stockCollector.catchIndustryCode(industry);
//        for (Stock temp : stockList) {
//            stockService.collectHistory(temp);
//        }
        stockService.collectStock(stockList);
        trendOccurModel.analysis(industry);
//        for (Stock temp : stockList) {
//            List<Daily> dailyList = dailyMapper.getDailyList(temp.getCode(), null, null);
//            analysisTrendService.analysisTrendAndSave(temp, dailyList);
//        }
//        industry = "分析样本";
//        analysisTrendService.analysisIndustryTrend(industry);

//        clearAnalysisDate();
//        analysisService.analysisTrend(industry);
//        finishAnalysis();


    }

    public void clearAnalysisDate() {
        List<String> oberverData = Arrays.asList("000960", "600338", "601958", "600549", "000933", "000807",
                "600392", "600111", "600497", "601600");
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

    List<String> oberverData = Arrays.asList("000960", "600338", "601958", "600549", "000933", "000807",
            "600392", "600111", "600497", "601600");
    List<String> buyStock = Arrays.asList("000933", "000807", "600549", "600392");

    public void finishAnalysis() {
        for (String code : buyStock) {
            Stock stock = stockMapper.getStock(code, null);
            stock.setObserverIndustry(Constant.OBERVER_INDUSTRY);
            stock.setRemark(StockProperties.BUY.name());
            stockMapper.updateStock(stock);
        }
        List<String> allObeverData = new LinkedList<>(oberverData);
        allObeverData.removeAll(buyStock);
        for (String temp :   allObeverData) {
            Stock stock = stockMapper.getStock(temp, null);
            stock.setObserverIndustry(Constant.OBERVER_INDUSTRY);
            stock.setRemark(StockProperties.OBSERVER.name());
            stockMapper.updateStock(stock);
        }
        //观察

    }
}
