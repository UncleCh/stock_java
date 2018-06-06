package com.it;

import com.it.bean.Stock;
import com.it.service.AnalysisService;
import com.it.service.FastService;
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
    @Autowired
    private FastService fastService;

    private Logger logger = LoggerFactory.getLogger(StockApplication.class);


    public void run(String... strings) throws Exception {
//        analysisService.analysis("稀土板块");
//        analysisService.analysisIndustryTrend("稀土板块");
        fastService.analysis();
    }

    public static void main(String[] args) throws Exception {
        run = SpringApplication.run(StockApplication.class, args);
    }





}
