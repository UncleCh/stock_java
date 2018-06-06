package com.it.service;

import com.it.bean.Stock;
import com.it.collect.StockCollector;
import com.it.repository.StockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@Service
public class FastService {
    @Autowired
    StockService stockService;
    @Autowired
    StockCollector stockCollector;


    public void analysis() {
        //增加样本，收集数据  002842
        List<Stock> stockList = stockCollector.catchIndustryCode("有色金属");
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
        Stock temp = new Stock();
        temp.setCode("600111");
        temp.setRemark("大国博弈加剧，配置具备战略属性的稀土、钨板块投资机会");
        temp.setObserverIndustry("稀土板块");
        temp.setName("北方稀土");
        stockList.add(temp);
        stockService.collectStockBasic(stockList);
        for (Stock tempStock : stockList) {
            stockService.collectHistory(tempStock);
        }
    }
}
