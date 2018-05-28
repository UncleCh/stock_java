package com.it;

import com.it.bean.Stock;
import com.it.service.AnalysisService;
import com.it.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
public class StockApplication implements CommandLineRunner {

    @Autowired
    StockService stockService;
    static ConfigurableApplicationContext run;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AnalysisService analysisService;

    private Logger logger = LoggerFactory.getLogger(StockApplication.class);


    public void run(String... strings) throws Exception {
//        String trendSql = "select count(*) as count ,code  from analysis_trend where start_dt > '2017-03-01 13:00:00' " +
//                " group by code order by count desc";
//        List<Map<String, Object>> maps = jdbcTemplate.queryForList(trendSql);
//        for (Map<String, Object> temp :  maps) {
//            String finaSql = "select self_assets_per,per_cacsh from finance where code = '"+temp.get("code")+"'";
//            List<Map<String, Object>> finaList = jdbcTemplate.queryForList(finaSql);
//            int count = Integer.parseInt(temp.get("count").toString());
//            if(count < 12)
//                continue;
//            double selfAssetsPer = 0,perCacsh = 0;
//            for (Map<String, Object> tempMap :   finaList) {
//                selfAssetsPer = selfAssetsPer + Double.parseDouble(tempMap.get("self_assets_per").toString());
//                perCacsh = perCacsh + Double.parseDouble(tempMap.get("per_cacsh").toString());
//            }
//            String lastSql = "select close from daily where code = '"+temp.get("code")+"' and dt > '2017-03-01' order by dt desc limit 1";
//            String lostSql = "select close from daily where code = '"+temp.get("code")+"' and dt > '2017-03-01' order by dt asc limit 1";
//            //最近的 股价
//            List<Map<String, Object>> lastMap = jdbcTemplate.queryForList(lastSql);
//            //最远的 股价
//            List<Map<String, Object>> lostMap = jdbcTemplate.queryForList(lostSql);
//            double lastClose = Double.parseDouble(lastMap.get(0).get("close").toString());
//            double lostClose = Double.parseDouble(lostMap.get(0).get("close").toString());
//            if((lastClose / lostClose) > 1.6 || selfAssetsPer < 15 || perCacsh < 0 ){
////                logger.error("代码 {} 振幅次数 {} 资产收益率 {} 每股现金流 {} 涨幅超过60% 最近 {} 最远 {}",
////                        temp.get("code"),temp.get("count"),selfAssetsPer,perCacsh,lastClose,lostClose);
//            }else{
//                logger.info("代码 {} 振幅次数 {} 资产收益率 {} 每股现金流 {}",
//                        temp.get("code"),temp.get("count"),selfAssetsPer,perCacsh);
//            }
//
//
//        }
        analysisService.analysisIndustryTrend();
//        stockService.multiCollectStock();
//        addAnalysis();
    }

    public static void main(String[] args) throws Exception {
        run = SpringApplication.run(StockApplication.class, args);
    }


    public void addAnalysis() {
        List<Stock> stockList = new LinkedList<>();
        Stock stock = new Stock();
        stock.setCode("600549");
        stock.setRemark("大国博弈加剧，配置具备战略属性的稀土、钨板块投资机会");
        stock.setObserverIndustry("稀土板块");
        stock.setName("厦门钨业");
        stockList.add(stock);
        Stock temp = new Stock();
        temp.setCode("600111");
        temp.setRemark("大国博弈加剧，配置具备战略属性的稀土、钨板块投资机会");
        temp.setObserverIndustry("稀土板块");
        temp.setName("北方稀土");
        stockList.add(temp);
        stockService.collectStockBasic(stockList);
        for (Stock tempStock : stockList) {
            stockService.collectHistory(tempStock);
        }
    }


}
