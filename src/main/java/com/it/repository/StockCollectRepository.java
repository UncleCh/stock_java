package com.it.repository;


import com.it.bean.StockBasicInfo;
import com.it.util.Constant;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class StockCollectRepository {

    private MongoTemplate mongoTemplate;

    @Autowired
    public StockCollectRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<StockBasicInfo> getNeedCatchStock() {
        Query query = new Query();
        query.addCriteria(Criteria.where("key").is(Constant.STOCK_NEED_CATCHED));
        List<Map> maps = mongoTemplate.find(query, Map.class, Constant.STOCK_CONFIG);
        if (CollectionUtils.isEmpty(maps))
            throw new RuntimeException("配置表数据异常,请计算需要抓取的股票列表");
        return (List<StockBasicInfo>) maps.get(0).get(Constant.STOCK_NEED_CATCHED);
    }

    public List<StockBasicInfo> getCatchedStock() {
        Query query = new Query();
        query.addCriteria(Criteria.where("key").is(Constant.STOCK_NEED_CATCHED));
        List<Map> maps = mongoTemplate.find(query, Map.class, Constant.STOCK_CONFIG);
        if (CollectionUtils.isEmpty(maps))
            throw new RuntimeException("配置表数据异常,请计算需要抓取的股票列表");
        return (List<StockBasicInfo>) maps.get(0).get(Constant.STOCK_CATCHED);
    }
}


