package com.it.service;


import com.google.common.collect.Lists;
import com.it.bean.AnalysisStock;
import com.it.bean.ContinueStockDesc;
import com.it.bean.SelectStrategyType;
import com.it.bean.Stock;
import com.it.repository.StockRepository;
import com.it.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StockSelectService {

    private StockRepository stockRepository;
    private StockAnalysis stockAnalysis;
    @Autowired
    private MongoTemplate mongoTemplate;
    private static Logger logger = LoggerFactory.getLogger(StockSelectService.class);

    @Autowired
    public StockSelectService(StockRepository stockRepository, StockAnalysis stockAnalysis) {
        this.stockRepository = stockRepository;
        this.stockAnalysis = stockAnalysis;
    }

    public AnalysisStock analysisiStockByPeriod(int period, int stockCode, List<ContinueStockDesc> curPeriod) {
        List<Stock> stocks = stockRepository.findByCodeOrderByDateAsc(stockCode +"");
        List<Stock> all = stockRepository.findAll();
        if (CollectionUtils.isEmpty(stocks))
            throw new RuntimeException("数据异常:" + stockCode);
        Map<Integer, LinkedList<ContinueStockDesc>> fallResult = stockAnalysis.getStockDataByContinuePercent(period, value -> value < -0.06,
                SelectStrategyType.CONTINUE_FALL_MAX, all);
        LinkedList<ContinueStockDesc> curPeriodStock = fallResult.get(fallResult.size());
        curPeriodStock.addAll(curPeriod);
        ArrayList<Double> prices = Lists.newArrayList();
        curPeriodStock.forEach(continueStockDesc -> prices.add(continueStockDesc.getAvgPrice()));
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(stockCode));
        Sort sort = new Sort(Sort.Direction.DESC, "date");
        query = query.with(sort).limit(1);
        Stock recentStock = mongoTemplate.findOne(query, Stock.class);
        prices.add(recentStock.getClose_price());
        prices.sort(Double::compare);
        double i = prices.indexOf(recentStock.getClose_price());
        Map<Integer, Stock> curPeriodMaxPrice = stockAnalysis.getMaxPrice(period, all);
        double days = 0;
        if (curPeriodStock.size() > 0) {
            ListIterator<ContinueStockDesc> listIterator = curPeriodStock.listIterator();
            ContinueStockDesc firstStock = null;
            ContinueStockDesc nextStock = curPeriodStock.getFirst();
            while (listIterator.hasNext()) {
                firstStock = nextStock;
                nextStock = listIterator.next();
                days += DateUtils.distanceDays(firstStock.getEndDate(), nextStock.getEndDate());
            }
        } else {
            logger.info("异常数据 代码：{} 连续振幅周期大小:{}", stockCode, curPeriodStock.size());
        }
        AnalysisStock analysisStock = new AnalysisStock(curPeriodStock.size(), i / prices.size(), curPeriodMaxPrice.get(curPeriodMaxPrice.size()).getMax_price());
        analysisStock.setFallMap(fallResult);
        analysisStock.setDays(days / analysisStock.getAmplitudeCount());
        for (Map.Entry<Integer, Stock> entry : curPeriodMaxPrice.entrySet()) {
            analysisStock.addPrice(entry.getValue());
        }
        Map<Integer, Stock> minPrice = stockAnalysis.getMinPrice(period, all);
        Stock minStock = minPrice.get(minPrice.size());
        Stock maxStock = curPeriodMaxPrice.get(curPeriodMaxPrice.size());
        double curPeriodAmplitude = (maxStock.getMax_price() - minStock.getMin_price()) / minStock.getMin_price();
        analysisStock.setCurPeriodAmplitude(curPeriodAmplitude);
        analysisStock.setAvgDayAmplitudeCount(((double) analysisStock.getAmplitudeCount()) / (all.size() % period));
        analysisStock.setCode(stockCode + "");
        analysisStock.setStartDate(all.get(0).getDate());
        analysisStock.curPeriodPecent = Math.abs(recentStock.getClose_price() - analysisStock.getCurPeriodMaxPrice()) / recentStock.getClose_price();
        analysisStock.curPeriodMinPecent = Math.abs(recentStock.getClose_price() - minStock.getClose_price()) / recentStock.getClose_price();
        analysisStock.recentPrice = recentStock.getClose_price();
        analysisStock.curPeriodMinPrice = minStock.getClose_price();
        return analysisStock;
    }
}
