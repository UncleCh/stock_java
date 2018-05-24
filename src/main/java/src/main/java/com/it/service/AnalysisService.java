package com.it.service;

import com.alibaba.fastjson.JSONObject;
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
                    if(in.get("industry").toString().equals(industry)) {
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
