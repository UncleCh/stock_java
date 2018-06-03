package com.it.repository;

import com.it.bean.AnalysisTrend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnalysisTrendMapper {


    int saveAnalysisTrend(AnalysisTrend record);


    List<AnalysisTrend> getAnalysisTrendList(@Param("wave") double wave, @Param("trend") String trend, @Param("industry")String industry);

    AnalysisTrend getOne(@Param("sort") String sort,@Param("code") String code);

    AnalysisTrend getAnalysisTrend(String id);
}