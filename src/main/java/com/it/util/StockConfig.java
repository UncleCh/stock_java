package com.it.util;


import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

@Config.Sources({"classpath:StockConfig.properties"})
public interface StockConfig extends Config {

    @Key("ali_stock_appcode")
    String appCode();

    @Key("ali_stock_host")
    String stockHost();

    @Key("stock_list")
    String stockList();

    @Key("history_stock_path")
    String historyStockUrl();


    @Key("stock_code_list")
    String stockCodeList();

    @Key("use_agent")
    String useAgent();

    @Key("default_percent")
    String defaultPercent();

    static StockConfig getConfig(){
        return ConfigFactory.create(StockConfig.class);
    }
}
