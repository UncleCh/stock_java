package com.it;

import com.it.bean.CodeObserver;
import com.it.collect.ExcelCollector;
import com.it.repository.StockMapper;
import com.it.repository.h2.CodeObserverMapper;
import com.it.service.AnalysisService;
import com.it.service.CodeObserverService;
import com.it.service.FastService;
import com.it.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.util.List;

@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
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
    @Autowired
    ExcelCollector excelCollector;
    @Autowired
    StockMapper stockMapper;
    @Autowired
    CodeObserverMapper codeObserverMapper;
    @Autowired
    CodeObserverService codeObserverService;

    private Logger logger = LoggerFactory.getLogger(StockApplication.class);


    public void run(String... strings) throws Exception {
//        codeObserverService.updateData();
//        fastService.analysis();
    }

    public static void main(String[] args) throws Exception {
        run = SpringApplication.run(StockApplication.class, args);
    }


}
