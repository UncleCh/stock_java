package com.it.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.it.bean.AnalysisTrend;
import com.it.bean.Daily;
import com.it.bean.Stock;
import com.it.bean.analysis.OverlapTrend;
import com.it.repository.AnalysisTrendMapper;
import com.it.repository.DailyMapper;
import com.it.repository.OverlapTrendMapper;
import com.it.repository.StockMapper;
import com.it.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private AnalysisTrendMapper trendMapper;
    @Autowired
    private OverlapTrendMapper overlapTrendMapper;
    @Autowired
    private DailyMapper dailyMapper;
    @Autowired
    private AnalysisTrendService analysisTrendService;

    @Autowired
    StockService stockService;
    private Logger logger = LoggerFactory.getLogger(AnalysisService.class);

    public void analysis() {
        //统计股票表 行业 - 行业数量
        String sql = "SELECT count(1)  as industryCount,industry from stock group by industry";
        String dt = "2018-04-27", amplitude = "3";
        String dailySql = "select s.industry,d.* from daily d,stock s " +
                "where  d.dt = '" + dt + "' and d.`code` = s.`code` and d.change_p > " + amplitude;
        //行业数据 -- 300
        List<Map<String, Object>> industryMap = jdbcTemplate.queryForList(sql);
        //查询当天符合条件的行业
        List<Map<String, Object>> dailyMap = jdbcTemplate.queryForList(dailySql);

        Map<String, List<Map<String, Object>>> industry1 = dailyMap.stream().collect(Collectors.groupingBy(new Function<Map<String, Object>, String>() {
            @Override
            public String apply(Map<String, Object> o) {
                return o.get("industry").toString();
            }
        }));
        //计算 当天符合条件行业 百分比
        List<DailyIndustry> result = new LinkedList<>();
        industry1.forEach(new BiConsumer<String, List<Map<String, Object>>>() {
            @Override
            public void accept(String industry, List<Map<String, Object>> maps) {
                for (Map<String, Object> in : industryMap) {
                    if (in.get("industry").toString().equals(industry)) {
                        int total = Integer.parseInt(in.get("industryCount").toString());
                        double v = maps.size() / (double) total;
                        DailyIndustry dailyIndustry = new DailyIndustry(industry, v, total + "");
                        List<String> code = maps.stream().map(dailyMap -> {
                            return dailyMap.get("code").toString();
                        }).collect(Collectors.toList());
                        dailyIndustry.codes = code;
                        result.add(dailyIndustry);
                    }
                }

            }
        });

        result.sort((o1, o2) -> -Double.compare(o1.per, o2.per));
        for (DailyIndustry dailyIndustry : result) {

            System.out.println(dailyIndustry);
        }
    }

    //根据观察的行业分类
    public void analysis(String industry) {
        Stock queryParam = new Stock();
        queryParam.setObserverIndustry(industry.trim());
        List<Stock> stockList = stockMapper.getStockList(queryParam);
        for (Stock temp : stockList) {
            stockService.analysisStock(temp);
        }
    }




    public void analysisTrend(String industry) {
        OverlapTrend queryParam = new OverlapTrend();
        queryParam.setIndustry(industry);
        List<OverlapTrend> overlapTrend = overlapTrendMapper.getOverlapTrend(queryParam);
        for (OverlapTrend temp : overlapTrend) {
            List<AnalysisTrend> trendIds = JSONArray.parseArray(temp.getTrendIds(), String.class).stream()
                    .map(s -> trendMapper.getAnalysisTrend(s)).collect(Collectors.toList());
            AnalysisTrend min = null, secondMin = null;
            Iterator<AnalysisTrend> iterator = trendIds.iterator();
            while (iterator.hasNext()) {
                AnalysisTrend analysisTrend = iterator.next();
                if (min == null) {
                    min = analysisTrend;
                    secondMin = analysisTrend;
                }
                if (analysisTrend.getStartDtTime() < min.getStartDtTime()) {
                    secondMin = min;
                    min = analysisTrend;
                }
                if (analysisTrend.getStartDtTime() < secondMin.getStartDtTime() &&
                        !min.equals(analysisTrend)) {
                    secondMin = analysisTrend;
                } else if (analysisTrend.getStartDtTime() == secondMin.getStartDtTime()
                        && min.equals(secondMin)) {
                    secondMin = analysisTrend;
                }


            }

            //最早 趋势同步时间
            long max = Math.max(min.getStartDtTime(), secondMin.getStartDtTime());
            //个股放量时间
            List<Daily> dailyList = dailyMapper.getDailyList(min.getCode(), min.getStartDt(), min.getEndDt());
            Daily biggerDaily = getTrendAmtBigger(dailyList);
            if (biggerDaily != null) {
                Stock stock = new Stock();
                stock.setCode(min.getCode());
                stock.setObserverIndustry(min.getObserverIndustry());
                List<AnalysisTrend> analysisTrends = analysisTrendService.analysisTrend(stock,
                        dailyMapper.getDailyList(min.getCode(), DateUtils.toSystemDate(biggerDaily.getDt()), min.getEndDt()));
                logger.info("最早 趋势同步时间 {} code {} 开始时间 {} 结束时间 {} 放量时间 {}",
                        DateUtils.toSystemDate(new Date(max)), biggerDaily.getCode(), min.getStartDt(), min.getEndDt(),
                        DateUtils.toSystemDate(biggerDaily.getDt()));
                logger.info("总涨幅:{}放量后涨幅:{}", min.getWave(), analysisTrends.get(0).getWave());
            } else
                logger.info(" code {} 开始时间 {} 结束时间 {} 放量时间 {} 总涨幅:{} 没有符合条件的放量",
                         min.getCode(),min.getStartDt(), min.getEndDt(),min.getWave());

            List<Daily> secondDailyList = dailyMapper.getDailyList(secondMin.getCode(), secondMin.getStartDt(), secondMin.getEndDt());
            Daily secondBiggerDaily = getTrendAmtBigger(secondDailyList);
            if (secondBiggerDaily != null) {
                Stock stock = new Stock();
                stock.setCode(secondMin.getCode());
                stock.setObserverIndustry(secondMin.getObserverIndustry());
                logger.info("code {} 开始时间 {} 结束时间 {} 放量时间 {}", secondBiggerDaily.getCode(),
                        secondMin.getStartDt(), secondMin.getEndDt(),
                        DateUtils.toSystemDate(secondBiggerDaily.getDt()));
                List<AnalysisTrend> analysisTrends = analysisTrendService.analysisTrend(stock,
                        dailyMapper.getDailyList(secondMin.getCode(), DateUtils.toSystemDate(secondBiggerDaily.getDt()), secondMin.getEndDt()));
                logger.info("总涨幅:{}放量后涨幅:{}", secondMin.getWave(), analysisTrends.get(0).getWave());
            } else
                logger.info("code {} 开始时间 {} 结束时间 {} 总涨幅:{}没有符合条件的放量",
                        secondMin.getCode()
                        ,secondMin.getStartDt(),secondMin.getEndDt(),secondMin.getWave());

        }
        System.out.println(1);

    }

    private Daily getTrendAmtBigger(List<Daily> dailyList) {
        Daily daily = null;
        int countDays = 0, appendDays = 0;
        for (Daily temp : dailyList) {
            if (daily == null)
                daily = temp;
            else {
                long trxAmtDouble = Long.parseLong(temp.getTrxAmt()) / Long.parseLong(daily.getTrxAmt());
                countDays = countDays + 1;
                if (trxAmtDouble >= 1.5) {
                    appendDays = appendDays + 1;
                }

                if (countDays == 5) {
                    if (appendDays >= 3)
                        return temp;
                    else {
                        countDays = 0;
                        appendDays = 0;
                    }
                }
            }

        }
        return null;
    }

    static class DailyIndustry {
        public String industry;
        public double per;
        public String total;
        public List<String> codes = new LinkedList<>();

        public DailyIndustry(String industry, double per, String total) {
            this.industry = industry;
            this.per = per;
            this.total = total;
        }

        public void add(String code) {
            codes.add(code);
        }

        @Override
        public String toString() {
            return "DailyIndustry{" +
                    "industry='" + industry + '\'' +
                    ", per=" + per +
                    ", total='" + total + '\'' +
                    ", codes=" + JSONObject.toJSONString(codes) +
                    '}';
        }
    }
}
