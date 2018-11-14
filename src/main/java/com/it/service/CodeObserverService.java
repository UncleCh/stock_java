package com.it.service;

import com.it.bean.CodeObserver;
import com.it.bean.Daily;
import com.it.collect.StockCollector;
import com.it.repository.h2.CodeObserverMapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * 个股价格跟踪
 */
@Service
public class CodeObserverService {
    @Autowired
    private CodeObserverMapper codeObserverMapper;
    @Autowired
    private StockCollector stockCollector;
    Logger logger = LoggerFactory.getLogger(CodeObserverService.class);

    public boolean updateData() throws IOException {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        if (dayOfWeek.getValue() != 5) {
            logger.info("个股跟踪：时间不对:{}", dayOfWeek.getValue());
            return false;
        }
        CodeObserver queyParam = new CodeObserver();
        List<CodeObserver> codeObserverList = codeObserverMapper.getCodeObserverList(queyParam);
        final WebDriver webDriver = new ChromeDriver();
        for (CodeObserver codeObserver : codeObserverList) {
            Daily daily = stockCollector.collectStockCode(codeObserver.getCode(), codeObserver.getMarket(), webDriver);
            // 更新数据
            codeObserver.setLastClosePrice(codeObserver.getClosePrice());
            codeObserver.setPe(new BigDecimal(daily.getPeRatio()));
            codeObserver.setClosePrice(new BigDecimal(daily.getClose()));
            //重新计算
            codeObserver.setRecPeg(codeObserver.getPe().divide(codeObserver.getIncrPer(), 3, RoundingMode.HALF_UP));
            BigDecimal bigDecimal = new BigDecimal(codeObserver.getLastClosePrice().doubleValue() - codeObserver.getClosePrice().doubleValue());
            codeObserver.setTenDay(bigDecimal.divide(codeObserver.getLastClosePrice(), 3, BigDecimal.ROUND_HALF_UP));
            codeObserverMapper.updateCodeObserver(codeObserver);
        }
        webDriver.close();
        return true;
    }

    public static void main(String[] args) {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        System.out.println(dayOfWeek.getValue());
    }
}
