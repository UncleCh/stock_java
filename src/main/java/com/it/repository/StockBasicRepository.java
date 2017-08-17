package com.it.repository;

import com.it.bean.Stock;
import com.it.bean.StockBasicInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface StockBasicRepository extends MongoRepository<StockBasicInfo, Integer> {
    StockBasicInfo findByCode(double stockCode);
}
