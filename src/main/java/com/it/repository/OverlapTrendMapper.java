package com.it.repository;

import com.it.bean.analysis.OverlapTrend;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OverlapTrendMapper {

    void saveOverlapTrend(OverlapTrend overlapTrend);

    List<OverlapTrend> getOverlapTrend(OverlapTrend overlapTrend);
}
