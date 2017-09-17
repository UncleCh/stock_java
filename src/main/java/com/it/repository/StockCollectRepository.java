package com.it.repository;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.it.bean.StockBasicInfo;
import com.it.bean.StockClassification;
import com.it.collect.StockCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class StockCollectRepository {

    private final StockCollector stockCollector;
    StockClassification stockClassification;
    private MongoTemplate mongoTemplate;
    private Logger logger = LoggerFactory.getLogger(StockCollectRepository.class);

    @Autowired
    public StockCollectRepository(StockCollector stockCollector, MongoTemplate mongoTemplate) {
        this.stockCollector = stockCollector;
        this.mongoTemplate = mongoTemplate;
//        stockClassification = stockCollector.stockClassification();
    }

    public List<StockBasicInfo> getNeedCatchStock() {
        return Lists.newArrayList(stockClassification.getNeedList());
    }

    public List<String> getCatchedStock() {
        GroupOperation code = Aggregation.group("code").count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(code);
        AggregationResults<Map> ali_stock = mongoTemplate.aggregate(aggregation, "ali_stock", Map.class);
        List<Map> mappedResults = ali_stock.getMappedResults();
        List<String> codes = org.assertj.core.util.Lists.newArrayList();
        mappedResults.forEach(map -> codes.add(map.get("_id").toString()));
        return codes;
    }

    public Set<StockBasicInfo> getCatchedStockBasicInfo() {
        List<String> catchedStock = getCatchedStock();
        Set<StockBasicInfo> result = Sets.newHashSet();
        Query query = new Query();
        for (String catchCode : catchedStock) {
            query.addCriteria(Criteria.where("code").is(catchCode));
            StockBasicInfo stockBasicInfo = mongoTemplate.findOne(query, StockBasicInfo.class);
            if(stockBasicInfo != null){
                result.add(stockBasicInfo);
            }else{
                logger.info("在股票列表中查询:{}，数据缺失",catchCode);
            }
        }
        return result;
    }

}


