package com.it;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockApplication implements CommandLineRunner {




    public void run(String... strings) throws Exception {


    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(StockApplication.class, args);
    }
}
