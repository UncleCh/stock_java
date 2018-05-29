package com.it.service;

import com.alibaba.fastjson.JSONObject;
import com.it.bean.AnalysisTrend;
import com.it.bean.Stock;
import com.it.bean.analysis.OverlapTrend;
import com.it.repository.AnalysisTrendMapper;
import com.it.repository.StockMapper;
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



    public void analysisIndustryTrend(String industry) {

        List<AnalysisTrend> trendList = trendMapper.getAnalysisTrendList(0.2, "UP", industry);
        Map<String, List<AnalysisTrend>> groupByCode = trendList.stream().collect(Collectors.groupingBy(AnalysisTrend::getCode));
        // 行情分析  1 求时间的交集  2  成交量确认
        if (MapUtils.isEmpty(groupByCode) || groupByCode.size() == 1) {
            logger.info("趋势数据不满足分析条件 {}", groupByCode);
        }
        //求交集
        Set<OverlapTrend> alls = new HashSet<>();
        for (String code : groupByCode.keySet()) {
            for (AnalysisTrend trend : groupByCode.get(code)) {
                alls.addAll(getOverlap(code, trend, groupByCode));
            }
        }
        logger.info("分析完成 :{}", alls);
        while (CollectionUtils.isNotEmpty(alls)) {
            alls = getOverlapTrend(alls);
            logger.info("分析:{}", alls);
        }
        logger.info("分析结果:{}", alls);

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
        groupByCode.forEach((s, analysisTrends) -> {
            if (!s.equals(code)) {
                result.addAll(analysisTrends);
            }
        });
        return result;
    }


    private Set<OverlapTrend> getOverlapTrend(Set<OverlapTrend> alls) {
        Set<OverlapTrend> allBack = new HashSet<>(alls);
        removeFirst(allBack);
        Set<OverlapTrend> result = new HashSet<>();
        for (OverlapTrend temp : alls) {
            for (OverlapTrend compare : allBack) {

                //相同的股票出现趋势
                if (temp.getCodes().equals(compare.getCodes())) {
                    if ((temp.getStartTime() >= compare.getStartTime() && temp.getStartTime() <= compare.getEndTime())
                            || temp.getEndTime() >= compare.getStartTime() && temp.getEndTime() <= compare.getEndTime()) {
                        if ((temp.getEndTime() - temp.getStartTime()) > (compare.getEndTime() - compare.getStartTime())) {
                            logger.info("股票出现趋势 : 忽略 {} 选择 {}", compare, temp);
                            result.add(temp);
                        } else {
                            result.add(compare);
                            logger.info("股票出现趋势 : 忽略 {} 选择 {}", temp, compare);
                        }
                        continue;
                    }
                }

                if ((temp.getStartTime() >= compare.getStartTime() && temp.getStartTime() <= compare.getEndTime())
                        || temp.getEndTime() >= compare.getStartTime() && temp.getEndTime() <= compare.getEndTime()) {
                    long startTime = Math.max(temp.getStartTime(), compare.getStartTime());
                    long endTime = Math.min(temp.getEndTime(), temp.getEndTime());
                    OverlapTrend overlapTrend = new OverlapTrend();
                    overlapTrend.setStartTime(startTime);
                    overlapTrend.setEndTime(endTime);
                    overlapTrend.getCodes().addAll(temp.getCodes());
                    overlapTrend.getCodes().addAll(compare.getCodes());

                    result.add(overlapTrend);
                }
            }
            removeFirst(allBack);
        }
        return result;
    }

    private void removeFirst(Set<OverlapTrend> allBack) {
        Iterator<OverlapTrend> iterator = allBack.iterator();
        if (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }

    }

    private List<OverlapTrend> getOverlap(String code, AnalysisTrend analysisTrend, Map<String, List<AnalysisTrend>> groupByCode) {
        List<OverlapTrend> result = new LinkedList<>();
        List<AnalysisTrend> otherTrend = getOtherTrend(code, groupByCode);

        for (AnalysisTrend trend : otherTrend) {
            //出现重叠的2种情况
            if ((analysisTrend.getStartDtTime() >= trend.getStartDtTime() && analysisTrend.getStartDtTime() <= trend.getEndDtTime())
                    || analysisTrend.getEndDtTime() >= trend.getStartDtTime() && analysisTrend.getEndDtTime() <= trend.getEndDtTime()) {
                long startTime = Math.max(analysisTrend.getStartDtTime(), trend.getStartDtTime());
                long endTime = Math.min(analysisTrend.getEndDtTime(), trend.getEndDtTime());
                OverlapTrend overlapTrend = new OverlapTrend();
                overlapTrend.setStartTime(startTime);
                overlapTrend.setEndTime(endTime);
                overlapTrend.setTrendIds(Arrays.asList(analysisTrend.getId(), trend.getId()));
                overlapTrend.setLeft(trend);
                overlapTrend.setRight(analysisTrend);
                overlapTrend.addCode(trend.getCode());
                overlapTrend.addCode(analysisTrend.getCode());
                result.add(overlapTrend);
            }
        }
        return result;
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
