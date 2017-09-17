package com.it.collect;

import com.it.bean.StockBasicInfo;
import com.it.bean.StockInfo;
import com.it.util.Constant;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockResource {

    private final StockCollector stockCollector;
    private MongoOperations mongoTemplate;

    @Autowired
    public StockResource(MongoOperations mongoTemplate, StockCollector stockCollector) {
        this.mongoTemplate = mongoTemplate;
        this.stockCollector = stockCollector;
    }

    public List<StockInfo> getRealStockInfo(List<StockBasicInfo> stocks) {
        List<StockInfo> all = mongoTemplate.findAll(StockInfo.class);
        List<StockInfo> result = Lists.newArrayList();
        List<StockBasicInfo> needCatch = Lists.newArrayList(stocks);
        for (StockBasicInfo basicInfo : stocks) {
            for (StockInfo stockInfo : all) {
                if (stockInfo.getCode().equals(basicInfo.getCode())) {
                    result.add(stockInfo);
                    needCatch.remove(basicInfo);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(needCatch)) {
            List<StockInfo> stockInfoList = collectRealStockInfoByInternet(needCatch);
            result.addAll(stockInfoList);
            mongoTemplate.insertAll(stockInfoList);
        }
        return result;
    }

    //获取实时股票信息
    public List<StockInfo> collectRealStockInfoByInternet(List<StockBasicInfo> stockBasicInfoList) {
        List<StockInfo> result = Lists.newArrayList();
        int start = 0, end = 0;
        do {
            if (stockBasicInfoList.size() > end + Constant.API_LIMIT_COUNT)
                end += Constant.API_LIMIT_COUNT;
            else
                end = end + stockBasicInfoList.size() - start;
            String codes = stockBasicInfoList.subList(start, end).stream().
                    map(stockBasicInfo -> stockBasicInfo.getMarket() + stockBasicInfo.getCode()).collect(Collectors.joining(","));
            List<StockInfo> stockInfo = stockCollector.getStockInfo(codes);
            mongoTemplate.insertAll(Lists.newArrayList(stockInfo));
            result.addAll(stockInfo);
            start = end;
        }
        while (end < stockBasicInfoList.size());
        return result;
    }
}
