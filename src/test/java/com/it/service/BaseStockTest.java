package com.it.service;

import com.it.bean.Stock;
import com.it.repository.StockRepository;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class BaseStockTest {

    @Autowired
    protected StockRepository stockRepository;
    protected List<Stock> stocks;
    protected int period = 412;
    @Before
    public void setUp() throws Exception {

    }
}
