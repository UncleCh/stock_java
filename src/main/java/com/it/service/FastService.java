package com.it.service;

import com.it.bean.Daily;
import com.it.bean.Stock;
import com.it.collect.StockCollector;
import com.it.repository.DailyMapper;
import com.it.repository.StockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    public void analysis() {
        String industry = "有色金属";
        //增加样本，收集数据  002842
//        List<Stock> stockList = stockCollector.catchIndustryCode("有色金属");
        Stock queryParam = new Stock();
        queryParam.setIndustry(industry);
        List<Stock> stockList = stockMapper.getStockList(queryParam);
        for (Stock temp : stockList) {
//            stockService.collectHistory(temp);
//            List<Daily> dailyList = dailyMapper.getDailyList(temp.getCode(), null, null);
//            analysisTrendService.analysisTrendAndSave(temp,dailyList);
            industry = "稀土板块";
            analysisTrendService.analysisIndustryTrend(industry);
        }
//        List<Daily> dailyList = dailyMapper.getDailyList(null, null, null, industry);
//        addAnalysis();
        //趋势分析

    }

    public void addAnalysis() {
        List<Stock> stockList = new LinkedList<>();
        Stock stock = new Stock();
        stock.setCode("600549");
        stock.setRemark("大国博弈加剧，配置具备战略属性的稀土、钨板块投资机会");
        stock.setObserverIndustry("稀土板块");
        stock.setName("厦门钨业");
        stockList.add(stock);
        stockService.collectStockBasic(stockList);
        for (Stock tempStock : stockList) {
            stockService.collectHistory(tempStock);
        }
    }
}
