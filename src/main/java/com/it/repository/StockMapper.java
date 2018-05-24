package com.it.repository;

import com.it.bean.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface StockMapper {

    public void saveStock(Stock stock);

    public boolean exits(Stock stock);

    public Stock getStock(@Param("code") String code,@Param("dt") Date dt);

    public void updateStock(Stock stock);

    public void delete(Stock stock);
}
