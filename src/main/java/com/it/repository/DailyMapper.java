package com.it.repository;

import com.it.bean.Daily;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DailyMapper {

    void saveDaily(Daily daily);

    void batchSaveDaily(@Param("dailyList") List<Daily> dailyList);

    boolean exits(Daily daily);

    int countDaily(Daily dt);

    List<Daily> getDailyList(@Param("code") String code, @Param("dt") String startDt,
                             @Param("endDt") String endDt);

    List<Daily> getRecDailyList(@Param("code") String code);
}
