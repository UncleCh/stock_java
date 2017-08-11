package com.it.repository;

import com.it.bean.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StockRepository extends MongoRepository<Stock, Integer> {

    List<Stock> findByCodeOrderByDateAsc(double stockCode);


}
