package com.it;

import com.it.bean.StockBasicInfo;
import com.it.bean.StockInfo;
import com.it.collect.StockCollector;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootApplication
public class StockApplication implements CommandLineRunner {


    @Autowired
    private StockCollector stockCollector;

    public void run(String... strings) throws Exception {

        stockCollector.initHistoryStock();
//        stockCollector.initStockList(1,"sz");
//        stockCollector.initStockList(1,"sh");

    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(StockApplication.class, args);
    }
}
