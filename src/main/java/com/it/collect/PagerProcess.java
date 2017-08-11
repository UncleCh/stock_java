package com.it.collect;

import com.google.common.collect.Lists;
import com.it.bean.Stock;
import com.it.util.Constant;
import com.it.util.DateUtils;
import com.it.util.StockConfig;
import org.aeonbits.owner.ConfigFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class PagerProcess implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    private static Logger logger = LoggerFactory.getLogger(PagerProcess.class);
    private StockCollector stockCollector;
    private MongoTemplate mongoTemplate;
    private static AtomicBoolean isAdd = new AtomicBoolean(false);

    public PagerProcess(StockCollector stockCollector, MongoTemplate mongoTemplate) {
        this.stockCollector = Objects.requireNonNull(stockCollector);
        this.mongoTemplate = Objects.requireNonNull(mongoTemplate);
    }

    public void process(Page page) {
        if (!isAdd.get()) {
            synchronized (isAdd) {
                if (!isAdd.get()) {
                    Set<String> stockList = stockCollector.getUnCatchStockCode();
                    for (String stockCode : stockList) {
                        if (stockCode.length() < 6)
                            stockCode =  String.format("%06s", stockCode);
                        page.addTargetRequest(StockConfig.getConfig().historyStockUrl().concat(stockCode));
                    }
                }
            }
        }
        Document doc = page.getHtml().getDocument();
        String stockCode = page.getRequest().getUrl().split("=")[1];
        List<Stock> results = Lists.newArrayList();
        getStockDataBySpider(stockCode, doc, results);
        page.putField("data", results);
    }

    private void getStockDataBySpider(String stockCode, Document doc, final List<Stock> results) {
        Element table = doc.select("table.data").get(1);
        int size = table.select("tbody tr").size();
        logger.info("catch page all tr size: {}", size);
        table.select("tbody tr")
                .forEach(tr -> {
                    Elements td = tr.select("td");
                    if (td.hasClass("hdr"))
                        return;
                    String date = DateUtils.toSystemDate(td.get(0).text());
                    Stock stock = new Stock();
                    stock.setDate(date);
                    stock.setCode(Double.parseDouble(stockCode));
                    stock.setOpen_price(Double.parseDouble(td.get(1).text()));
                    stock.setMax_price(Double.parseDouble(td.get(2).text()));
                    stock.setMin_price(Double.parseDouble(td.get(3).text()));
                    stock.setClose_price(Double.parseDouble(td.get(4).text()));
                    stock.setTrade_num(Integer.parseInt(td.get(5).text().replaceAll(",", "")));
                    stock.setTrade_money(Double.parseDouble(td.get(6).text().replaceAll(",", "")));
                    double v = (stock.getClose_price() - stock.getOpen_price()) / stock.getOpen_price();
                    DecimalFormat df = new DecimalFormat("0.0000");
                    stock.setInc_percent(Double.parseDouble(df.format(v)));
                    v = (stock.getMax_price() - stock.getMin_price()) / stock.getMin_price();
                    stock.setMax_min_percent(Double.parseDouble(df.format(v)));
                    results.add(stock);
                });

        logger.info("catch stock data finish size:{}", results.size());
    }


    public Site getSite() {
        StockConfig stockConfig = ConfigFactory.create(StockConfig.class);
        return site.addHeader("User-Agent", stockConfig.useAgent());
    }


}