package com.it.repository;

import com.it.bean.Finance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface FinanceMapper {

    public void saveFinance(Finance finance);

    public boolean exits(Finance tempF);

    public int countFinanceByCode(@Param("code") String code,@Param("dt") Date dt);
}
