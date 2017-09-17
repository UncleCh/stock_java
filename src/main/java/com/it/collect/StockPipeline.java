package com.it.collect;

import com.it.bean.Stock;
import com.it.repository.StockRepository;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

/**
 * stock mongdb 持久化
 * Created by Administrator on 2017/4/26.
 */

public class StockPipeline implements Pipeline {

    private StockRepository stockRepository;
    private Logger logger = LoggerFactory.getLogger(StockPipeline.class);

    public StockPipeline(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }


    @Override
    public void process(ResultItems resultItems, Task task) {
        List<Stock> stocks = resultItems.get("data");
        if (CollectionUtils.isNotEmpty(stocks)) {
            List<Stock> insert = stockRepository.insert(stocks);
            logger.info("catch stock data finish size:{} insert size:{}", stocks.size(), insert.size());
        }
    }
}