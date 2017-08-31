package com.it;

import com.it.bean.StockBasicInfo;
import com.it.bean.StockInfo;
import com.it.collect.StockCollector;
import com.it.task.CollectHistory;
import org.assertj.core.util.Lists;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@SpringBootApplication
public class StockApplication implements CommandLineRunner {


    @Autowired
    private StockCollector stockCollector;
    @Autowired
    ApplicationContext ctx;

    public void run(String... strings) throws Exception {

        stockCollector.initHistoryStock();
//        stockCollector.initStockList(1,"sz");
//        stockCollector.initStockList(1,"sh");
//        startQuart();

    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(StockApplication.class, args);

    }

    private  void startQuart() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        //通道成功率预警
        JobDetail jobDetail = newJob(CollectHistory.class).withIdentity("checkChannelRate").build();
        //参过参数传递，避免重复创建ApplicationContext对象
        jobDetail.getJobDataMap().put(CollectHistory.CONTEXT, ctx);
        Trigger channelRateTrigger = newTrigger().withIdentity("checkChannelRateJob").startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0 */5 * * * ?")).build();//每20分钟执行一次
        scheduler.scheduleJob(jobDetail, channelRateTrigger);
    }
}
