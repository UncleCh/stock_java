package com.it.task;


import com.it.service.StockRealService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class CollectHistory implements Job {
    public static final String CONTEXT = "collect_history_context";
    private Logger logger = LoggerFactory.getLogger(CollectHistory.class);

    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        logger.info("------------执行收集今天行情数据-------------");
        ApplicationContext context = (ApplicationContext) jobContext.get(CONTEXT);
        StockRealService realService = context.getBean(StockRealService.class);
        realService.initRealData();
    }




}
