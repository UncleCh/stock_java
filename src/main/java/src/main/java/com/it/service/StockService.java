package com.it.service;

import com.alibaba.fastjson.JSONObject;
import com.it.ReverseTrendException;
import com.it.bean.*;
import com.it.collect.ExcelCollector;
import com.it.collect.StockCollector;
import com.it.repository.AnalysisTrendMapper;
import com.it.repository.DailyMapper;
import com.it.repository.FinanceMapper;
import com.it.repository.StockMapper;
import com.it.util.Constant;
import com.it.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class StockService {

    @Autowired
    private ExcelCollector excelCollector;
    @Autowired
    private StockMapper indexListMapper;
    @Autowired
    private StockCollector stockCollector;
    @Autowired
    private DailyMapper dailyMapper;
    @Autowired
    private FinanceMapper financeMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private AnalysisTrendMapper trendMapper;
    private Logger logger = LoggerFactory.getLogger(StockService.class);

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    public void multiCollectStock() {
        List<Stock> indexLists = excelCollector.readExcel("index/000300cons.xls");
//        collectStockBasic(indexLists);
        collectStock(indexLists);
    }

    /**
     * 自动读取 沪深300
     */
    public void collectStock(List<Stock> stocks) {

        final WebDriver webDriver = new ChromeDriver();
        for (Stock temp : stocks) {
            logger.info("开始分析 股票代码 {}", temp.getCode());
            try {
//                collectHistory(temp);
            } catch (Exception e) {
                logger.info("--", e);
                continue;
            }
//            if (!indexListMapper.exits(temp))
//                indexListMapper.saveStock(temp);
//            collectFinace(temp, webDriver);
//
            collectDaily(webDriver, temp);
        }
        webDriver.close();
    }


    public void analysisStock(Stock temp) {
        List<Daily> dailyList = dailyMapper.getDailyList(temp.getCode(), "2017-06-01");
        AnalysisTrendModel sortList = new AnalysisTrendModel();
        AnalysisTrend curTrend = null;
        for (Daily daily : dailyList) {
            try {
                boolean trend = sortList.add(daily);
                if (trend) {
                    AnalysisTrend analysisTrend = getAnalysisTrend(temp, sortList);
                    if (curTrend != null) {
                        if (curTrend.getTrend().equals(analysisTrend.getTrend())) {
                            Date curEndDate = DateUtils.parse("yyyy-MM-dd", curTrend.getEndDt());
                            Date analysisStartDate = DateUtils.parse("yyyy-MM-dd", analysisTrend.getStartDt());
                            if ((analysisStartDate.getTime() - curEndDate.getTime()) <= (86400000 * 5)) {
                                curTrend = analysisTrend;
                                continue;
                            } else {
                                trendMapper.saveAnalysisTrend(analysisTrend);
                                sortList.clear();
                            }
                        } else {
                            trendMapper.saveAnalysisTrend(analysisTrend);
                            sortList.clear();
                        }
                    }
                    curTrend = analysisTrend;

                }
            } catch (Exception e) {
                if (e instanceof ReverseTrendException) {
                    AnalysisTrend analysisTrend = getAnalysisTrend(temp, sortList);
                    trendMapper.saveAnalysisTrend(analysisTrend);
                    sortList.clear();
                    logger.info("趋势反转:{}" ,e.getMessage());
                }

            }

        }
    }

    private AnalysisTrend getAnalysisTrend(Stock temp, AnalysisTrendModel sortList) {
        AnalysisTrend analysisTrend = new AnalysisTrend();
        analysisTrend.setCode(temp.getCode());
        analysisTrend.setTrend(sortList.getCurTrend().toString());
        String start = DateUtils.sys.format(sortList.getStart().getDt());
        analysisTrend.setStartDt(start);
        String end = DateUtils.sys.format(sortList.getEnd().getDt());
        analysisTrend.setEndDt(end);
        analysisTrend.setWave(sortList.getWave());
        analysisTrend.setMax(sortList.getMax().getMax());
        analysisTrend.setMin(sortList.getMin().getMin());
        return analysisTrend;
    }

    private void collectDaily(WebDriver webDriver, Stock temp) {
        Daily daily = new Daily();
        daily.setCode(temp.getCode());
        daily.setDt(DateUtils.getCurDate());
        if (dailyMapper.exits(daily))
            return;
        Future<Daily> dailyFuture = executorService.submit(() -> stockCollector.collectStockCode(temp, webDriver));
        try {
            daily = null;
            daily = dailyFuture.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            return;
        }
        if (daily != null && !dailyMapper.exits(daily))
            dailyMapper.saveDaily(daily);
    }

    private void collectFinace(Stock temp, WebDriver webDriver) {
        int count = financeMapper.countFinanceByCode(temp.getCode(), null);
        if (count < 3) {
            List<Finance> finances = stockCollector.collectFinance(temp, webDriver);
            for (Finance tempF : finances) {
                if (!financeMapper.exits(tempF))
                    financeMapper.saveFinance(tempF);
            }
        }
    }

    public void collectHistory(Stock temp) {
        Daily daily = new Daily();
        daily.setCode(temp.getCode());
        int count = dailyMapper.countDaily(daily);
        if (count > Constant.STOCK_SIZE) {
//            analysisStock(temp);
            return;
        }
        try {
            List<Daily> dailyList = stockCollector.collectStockHistory(temp);
            logger.info("抓取股票历史数据 {} 大小{}", temp.getCode(), dailyList.size());
            for (Daily tempD : dailyList) {
                if (!dailyMapper.exits(tempD)) {
                    dailyMapper.saveDaily(tempD);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void collectStockBasic(List<Stock> stocks) {
        final WebDriver webDriver = new ChromeDriver();
        for (Stock temp : stocks) {
            Stock stock = stockMapper.getStock(temp.getCode(), null);
            if (stock != null) {
                stockMapper.updateStock(temp);
                continue;
            }
            String url = "https://www.iwencai.com/data-robot/extract-new?query=" + temp.getCode() +
                    "&querytype=stock&qsData=pc_~soniu~others~homepage~box~history&dataSource=hp_history";
            if (!StringUtils.isEmpty(temp.getIndustry()))
                continue;
            try {
                webDriver.get(url);
                WebDriverWait wait = new WebDriverWait(webDriver, 20);
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"dp_block_0\"]/div/div/table/tbody/tr/td[3]/div/a")));
                WebElement search = webDriver.findElement(By.xpath("//*[@id=\"dp_block_0\"]/div/div/table/tbody/tr/td[3]/div/a"));
                temp.setIndustry(search.getText());
                List<WebElement> elements = webDriver.findElements(By.xpath("//*[@id=\"dp_block_0\"]/div/div/table/tbody/tr/td[6]/div/span"));
                List<String> concepts = new LinkedList<>();
                for (WebElement element : elements) {
                    String a = element.getText().replaceAll(";", "").trim();
                    concepts.add(a);
                }
                temp.setConcept(JSONObject.toJSONString(concepts));
                stockMapper.updateStock(temp);
            } catch (Exception e) {
                logger.info(url, e);
            }

        }
        webDriver.close();
    }


}