package com.it.collect;

import com.it.bean.RealTimeDataPOJO;
import com.it.service.BaseStockTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;


public class RealTimeDataTest extends BaseStockTest {

    @Test
    public void testGetRealTimeDataObjects(){
        String[] codes = {"sh600129"};
        List<RealTimeDataPOJO> realTimeDataObjects = RealTimeData.getRealTimeDataObjects(codes);
        Assert.assertEquals(1,realTimeDataObjects.size());
    }

}