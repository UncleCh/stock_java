package com.it.service;

import com.it.bean.RealTimeDataPOJO;
import com.it.bean.Stock;
import com.it.bean.StockBasicInfo;
import com.it.collect.RealTimeData;
import com.it.repository.StockCollectRepository;
import com.it.repository.StockRepository;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StockRealService {

    private StockCollectRepository collectRepository;
    private MongoTemplate mongoTemplate;
    private StockRepository stockRepository;

    @Autowired
    public StockRealService(StockCollectRepository collectRepository, MongoTemplate mongoTemplate, StockRepository stockRepository) {
        this.collectRepository = collectRepository;
        this.mongoTemplate = mongoTemplate;
        this.stockRepository = stockRepository;
    }

    public void initRealData(){
        List<Stock> yesterdayStockInfo = getYesterdayStockInfo(collectRepository, stockRepository);
        mongoTemplate.insertAll(yesterdayStockInfo);
    }

    private List<Stock> getYesterdayStockInfo(StockCollectRepository collectRepository, StockRepository stockRepository) {
        List<RealTimeDataPOJO> realTimeData = getCatchedCodeRealInfo(collectRepository);
        List<Stock> stocks = Lists.newArrayList();
        for (RealTimeDataPOJO realPojo : realTimeData) {
            //计算今天的信息
            Stock stock = new Stock();
            stock.setMin_price(realPojo.getLow());
            stock.setMarket(realPojo.getFullCode().substring(0, 2));
            stock.setTrade_num((int) realPojo.getVolume());
            stock.setTrade_money(realPojo.getVolumePrice());
            stock.setClose_price(realPojo.getClose());
            stock.setOpen_price(realPojo.getOpen());
            stock.setCode(realPojo.getFullCode().substring(2));
            stock.setMax_price(realPojo.getHigh());
            stock.setDate(realPojo.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            Stock yesterdayStock = stockRepository.findByCodeOrderByDateDesc(stock.getCode());
            double changePercent = (yesterdayStock.getClose_price() - stock.getClose_price()) / stock.getClose_price();
            stock.setInc_percent(changePercent);
            double maxMinPer = (realPojo.getHigh() - realPojo.getLow()) / stock.getClose_price();
            stock.setMax_min_percent(maxMinPer);
            stocks.add(stock);
        }
        return stocks;
    }

    private List<RealTimeDataPOJO> getCatchedCodeRealInfo(StockCollectRepository collectRepository) {
        List<StockBasicInfo> catchedStock = collectRepository.getCatchedStock();
        List<String> codeList = Lists.newArrayList();
        for (StockBasicInfo stockInfo : catchedStock) {
            codeList.add(stockInfo.getCode());
        }
        String[] codes = (String[]) codeList.toArray();
        return RealTimeData.getRealTimeDataObjects(codes);
    }
}
