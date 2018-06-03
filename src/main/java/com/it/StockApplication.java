package com.it;

import com.it.bean.Stock;
import com.it.service.AnalysisService;
import com.it.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
public class StockApplication implements CommandLineRunner {

    @Autowired
    StockService stockService;
    static ConfigurableApplicationContext run;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AnalysisService analysisService;

    private Logger logger = LoggerFactory.getLogger(StockApplication.class);


    public void run(String... strings) throws Exception {
//        analysisService.analysis("稀土板块");
        analysisService.analysisTrend("稀土板块");
//        stockService.multiCollectStock();
//        addAnalysis();
    }

    public static void main(String[] args) throws Exception {
        run = SpringApplication.run(StockApplication.class, args);
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
