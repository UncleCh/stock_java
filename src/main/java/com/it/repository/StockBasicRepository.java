package com.it.repository;

import com.it.bean.StockBasicInfo;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface StockBasicRepository extends MongoRepository<StockBasicInfo, Integer> {
}
