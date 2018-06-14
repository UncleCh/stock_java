package com.it.bean.analysis;

import com.it.bean.Daily;
import com.it.bean.Stock;
import com.it.bean.StockProperties;
import com.it.repository.DailyMapper;
import com.it.repository.StockMapper;
import com.it.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 是否出现趋势
 * 1. 天数
 * 2. 上涨幅度
 * 3. 趋势开始时间(当天涨幅，正数)
 */
@Service
public class TrendOccurModel {
    @Autowired
    private DailyMapper dailyMapper;
    @Autowired
    private StockMapper stockMapper;
    private Logger logger = LoggerFactory.getLogger(TrendOccurModel.class);

    private Optional<TrendOccur> occurTrend(List<Daily> recDailyList) {
        Daily start = null;
        int upDays = 0;
        for (Daily daily : recDailyList) {
            if (start == null) {
                if (daily.getAmplitude() > 0)
                    start = daily;
            } else {
                if (daily.getAmplitude() > 0 && daily.getClose() > start.getClose()) {
                    upDays = upDays + 1;
                }
            }
        }
        if (start == null)
            return Optional.empty();
        Daily curDaily = recDailyList.get(recDailyList.size() - 1);
        double upTrend = (curDaily.getClose() - start.getClose()) / ((start.getOpen() + start.getClose()) / 2);
        if (upDays >= 2 && upTrend > 0.03) {
            TrendOccur trendOccur = new TrendOccur();
            trendOccur.setCode(curDaily.getCode());
            trendOccur.setDays(upDays);
            trendOccur.setStartTime(start.getDt().getTime());
            trendOccur.setUpTrend(upTrend);
            return Optional.of(trendOccur);
        }
        return Optional.empty();
    }

    private List<TrendOccur> occurTrend(TrendOccur trendOccur) {
        Stock stock = stockMapper.getStock(trendOccur.getCode(), null);
        Stock queryParam = new Stock();
        queryParam.setIndustry(stock.getIndustry());
        queryParam.setRemark(StockProperties.OBSERVER.name());
        List<Stock> stockList = stockMapper.getStockList(queryParam);
        List<TrendOccur> result = new LinkedList<>();
        for (Stock tempStock : stockList) {
            LocalDate startLocal = LocalDate.parse(DateUtils.toSystemDate(new Date(trendOccur.getStartTime())), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate minus = startLocal.minus(7, ChronoUnit.DAYS);
            List<Daily> dailyList = dailyMapper.getDailyList(tempStock.getCode(), minus.toString(), null);
            Optional<TrendOccur> trendOccur1 = occurTrend(dailyList);
            trendOccur1.ifPresent(result::add);
        }
        return result;
    }

    public void analysis(String industry) {
        Stock queryParam = new Stock();
        queryParam.setIndustry(industry);
        queryParam.setRemark(StockProperties.BUY.name());
        List<TrendOccur> happyList = new LinkedList<>();
        List<Stock> stockList = stockMapper.getStockList(queryParam);
        for (Stock stock : stockList) {
            List<Daily> recDailyList = dailyMapper.getRecDailyList(stock.getCode());
            recDailyList.sort(Comparator.comparingLong(o -> o.getDt().getTime()));
            Optional<TrendOccur> trendOccur = occurTrend(recDailyList);
            trendOccur.ifPresent(happyList::add);
        }
        if (happyList.isEmpty()) {
            logger.info("{} 未出现有效趋势", industry);
        } else {
            logger.info("{} 买入方出现趋势", happyList);
            for (TrendOccur trendOccur : happyList) {
                List<TrendOccur> trendOccurs = occurTrend(trendOccur);
                if (!trendOccurs.isEmpty()) {
                    logger.info("请查看 {},参考趋势:{} ", trendOccur.getCode(), trendOccurs);
                }
            }
        }
    }
}
