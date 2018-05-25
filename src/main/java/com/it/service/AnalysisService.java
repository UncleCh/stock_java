package com.it.service;

import com.alibaba.fastjson.JSONObject;
import com.it.bean.AnalysisTrend;
import com.it.bean.Stock;
import com.it.repository.AnalysisTrendMapper;
import com.it.repository.StockMapper;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    public void analysisByCode(String code) {
        Stock stock = stockMapper.getStock(code, null);
        stockService.analysisStock(stock);
    }

    public void analysisIndustryTrend() {

        List<AnalysisTrend> trendList = trendMapper.getAnalysisTrendList(0.2, "UP");
        Map<String, List<AnalysisTrend>> groupByCode = trendList.stream().collect(Collectors.groupingBy(AnalysisTrend::getCode));
        // 行情分析  1 求时间的交集  2  成交量确认
        if (MapUtils.isEmpty(groupByCode) || groupByCode.size() == 1) {
            logger.info("趋势数据不满足分析条件 {}", groupByCode);
        }
        trendIntersection(groupByCode);

    }

    /**
     * 获取除指定代码外，其他所有的趋势
     *
     * @param code
     * @param groupByCode
     * @return
     */
    private List<AnalysisTrend> getOtherTrend(String code, Map<String, List<AnalysisTrend>> groupByCode) {
        List<AnalysisTrend> result = new LinkedList<>();
        groupByCode.forEach(new BiConsumer<String, List<AnalysisTrend>>() {
            @Override
            public void accept(String s, List<AnalysisTrend> analysisTrends) {
                if (!s.equals(code)) {
                    result.addAll(analysisTrends);
                }
            }
        });
        return result;
    }

    //求交集  https://blog.csdn.net/leegoowang/article/details/72084667
    private void trendIntersection(Map<String, List<AnalysisTrend>> groupByCode) {
        for (String code : groupByCode.keySet()) {
            for (AnalysisTrend trend : groupByCode.get(code)) {
                List<AnalysisTrend> otherTrend = getOtherTrend(code, groupByCode);

            }
        }

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
