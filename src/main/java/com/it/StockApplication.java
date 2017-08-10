package com.it;

import com.it.collect.StockCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockApplication implements CommandLineRunner {


    @Autowired
    private StockCollector stockCollector;

    public void run(String... strings) throws Exception {
//        stockCollector.initHistoryStock();
//        stockCollector.initStockList(1,"sz");

    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(StockApplication.class, args);
    }
}
