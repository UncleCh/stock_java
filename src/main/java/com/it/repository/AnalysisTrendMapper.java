package com.it.repository;

import com.it.bean.AnalysisTrend;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface AnalysisTrendMapper {



    int saveAnalysisTrend(AnalysisTrend record);


}