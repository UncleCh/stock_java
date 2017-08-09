package com.it.repository;

import com.it.bean.AnalysisStock;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnalysisRepository extends MongoRepository<AnalysisStock, Integer> {

}
