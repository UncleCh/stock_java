package com.it.service;

import com.it.ReverseTrendException;
import com.it.bean.AnalysisTrend;
import com.it.bean.AnalysisTrendModel;
import com.it.bean.Daily;
import com.it.bean.Stock;
import com.it.bean.analysis.OverlapTrend;
import com.it.repository.AnalysisTrendMapper;
import com.it.repository.DailyMapper;
import com.it.repository.OverlapTrendMapper;
import com.it.util.Constant;
import com.it.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class AnalysisTrendService {
    @Autowired
    private AnalysisTrendMapper trendMapper;
    @Autowired
    private DailyMapper dailyMapper;
    @Autowired
    private OverlapTrendMapper overlapTrendMapper;
    private Logger logger = LoggerFactory.getLogger(AnalysisTrendService.class);

    public void analysisStock(Stock temp, String startDt, String endDt) {

        List<Daily> dailyList = dailyMapper.getDailyList(temp.getCode(), startDt, endDt);
        analysisTrend(temp, dailyList);
    }

    public void analysisTrendAndSave(Stock temp, List<Daily> dailyList) {
        List<AnalysisTrend> analysisTrends = analysisTrend(temp, dailyList);
        for (AnalysisTrend analysisTrend : analysisTrends) {
            logger.info("保存趋势:{}", analysisTrend);
            trendMapper.saveAnalysisTrend(analysisTrend);
        }
    }

    public List<AnalysisTrend> analysisTrend(Stock temp, List<Daily> dailyList) {
        AnalysisTrendModel sortList = new AnalysisTrendModel();
        List<AnalysisTrend> results = new LinkedList<>();
        AnalysisTrend curTrend = null;
        for (Daily daily : dailyList) {
            try {
                boolean trend = sortList.add(daily);
                if (trend) {
                    AnalysisTrend analysisTrend = getAnalysisTrend(temp, sortList);
                    if (curTrend != null) {
                        if (curTrend.getTrend().equals(analysisTrend.getTrend())) {
                            Date curEndDate = DateUtils.parse("yyyy-MM-dd", curTrend.getEndDt());
                            Date analysisStartDate = DateUtils.parse("yyyy-MM-dd", analysisTrend.getStartDt());
                            if ((analysisStartDate.getTime() - curEndDate.getTime()) <= (86400000 * 5)) {
                                curTrend = analysisTrend;
                                continue;
                            } else {
                                results.add(analysisTrend);
                                sortList.clear();
                            }
                        } else {
                            results.add(analysisTrend);
                            sortList.clear();
                        }
                    }
                    curTrend = analysisTrend;

                }
            } catch (Exception e) {
                if (e instanceof ReverseTrendException) {
                    AnalysisTrend analysisTrend = getAnalysisTrend(temp, sortList);
                    results.add(analysisTrend);
                    sortList.clear();
                    logger.info("趋势反转:{}", e.getMessage());
                }
            }
        }
        if (CollectionUtils.isEmpty(results) && curTrend != null)
            results.add(curTrend);

        return results;
    }

    public void analysisIndustryTrend(String industry) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<AnalysisTrend> trendList = trendMapper.getAnalysisTrendList(0.2, "UP", industry);
        Map<String, List<AnalysisTrend>> groupByYear = trendList.stream().collect(Collectors.groupingBy(o -> o.getStartDt().substring(2, 4)));
        for (Map.Entry<String, List<AnalysisTrend>> listEntry : groupByYear.entrySet()) {
            //牛市 最后分析  数据具有迷惑性
            if (listEntry.getKey().equalsIgnoreCase("15") || listEntry.getKey().equalsIgnoreCase("16"))
                continue;
            Map<String, List<AnalysisTrend>> groupByCode = listEntry.getValue().stream().collect(Collectors.groupingBy(AnalysisTrend::getCode));
            // 行情分析  1 求时间的交集  2  成交量确认
            if (MapUtils.isEmpty(groupByCode) || groupByCode.size() == 1) {
                logger.info("{} 年 {} 行业 趋势数据不满足分析条件 {}", listEntry.getKey(), industry, groupByCode);
                continue;
            }
            logger.info("{} 年 {} 行业 股票相同趋势 参与分析的股票 :{}", listEntry.getKey(), industry, groupByCode.keySet());

//            //求交集
            Set<OverlapTrend> result = new HashSet<>();
//            for (String code : groupByCode.keySet()) {
//                for (AnalysisTrend trend : groupByCode.get(code)) {
//                    alls.addAll(getOverlap(code, trend, groupByCode));
//                }
//            }
//            logger.info("{} 年 {} 行业 股票相同趋势 首次分析完成 :{}", listEntry.getKey(), industry, alls);
            //修改算法，求单只股票 最大的
            for (String code : groupByCode.keySet()) {
                for (AnalysisTrend trend : groupByCode.get(code)) {
                    result.addAll(getOverlap(code, trend, groupByCode));
                }
            }
//            Set<OverlapTrend> result = null;
//            while (CollectionUtils.isNotEmpty(alls)) {
//                if (CollectionUtils.isNotEmpty(alls))
//                    result = alls;
//                alls = getOverlapTrend(alls);
//            }
            if (result == null) {
                logger.info("{} 年 {} 行业 未出现一致性趋势", listEntry.getKey(), industry);
                continue;
            }
            for (OverlapTrend temp : result) {
                temp.setIndustry(industry);
                overlapTrendMapper.saveOverlapTrend(temp);
            }
            logger.info("{} 年 {} 行业 分析结果:{}", listEntry.getKey(), industry, result);
        }
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
                    overlapTrend.getTrends().addAll(temp.getTrends());
                    overlapTrend.getTrends().addAll(compare.getTrends());
                    if (overlapTrend.getEndTime() - overlapTrend.getStartTime() > Constant.DAY * 7 && (overlapTrend.getEndTime() - overlapTrend.getStartTime() < Constant.DAY * 80))
                        result.add(overlapTrend);
                }
            }
            removeFirst(allBack);
        }
        return result;
    }

    // 1. 找出该股票与该 analysisTrend 有重叠的部分，  2.找出最大的重叠交集
    private List<OverlapTrend> getOverlap(String code, AnalysisTrend analysisTrend, Map<String, List<AnalysisTrend>> groupByCode) {
        List<OverlapTrend> result = new LinkedList<>();
        List<AnalysisTrend> otherTrendList = getOtherTrend(code, groupByCode);
        if (analysisTrend.getEndDtTime() - analysisTrend.getStartDtTime() > Constant.DAY * 30 * 3
                && analysisTrend.getEndDtTime() - analysisTrend.getStartDtTime() < Constant.DAY * 7) {
            logger.info("忽略无效数据: {}", analysisTrend);
            return result;
        }
        for (AnalysisTrend trend : otherTrendList) {
            //移除趋势持续时间过长的数据 (可能有停牌)
            if (trend.getEndDtTime() - trend.getStartDtTime() > Constant.DAY * 30 * 3
                    && trend.getEndDtTime() - trend.getStartDtTime() < Constant.DAY * 7) {
                logger.info("忽略无效数据: {}", trend);
                continue;
            }
            //出现重叠的2种情况
            if ((analysisTrend.getStartDtTime() >= trend.getStartDtTime() && analysisTrend.getStartDtTime() <= trend.getEndDtTime())
                    || analysisTrend.getEndDtTime() >= trend.getStartDtTime() && analysisTrend.getEndDtTime() <= trend.getEndDtTime()) {
                long startTime = Math.max(analysisTrend.getStartDtTime(), trend.getStartDtTime());
                long endTime = Math.min(analysisTrend.getEndDtTime(), trend.getEndDtTime());
                OverlapTrend overlapTrend = new OverlapTrend();
                overlapTrend.setStartTime(startTime);
                overlapTrend.setEndTime(endTime);
                overlapTrend.addTrend(trend);
                overlapTrend.addTrend(analysisTrend);
                overlapTrend.addCode(trend.getCode());
                overlapTrend.addCode(analysisTrend.getCode());
                if (overlapTrend.getEndTime() - overlapTrend.getStartTime() > Constant.DAY * 7 && (overlapTrend.getEndTime() - overlapTrend.getStartTime() < Constant.DAY * 80))
                    result.add(overlapTrend);
            }
        }
        return result;
//        List<OverlapTrend> result = new LinkedList<>();
//        List<AnalysisTrend> otherTrendList = getOtherTrend(code, groupByCode);
//        if (analysisTrend.getEndDtTime() - analysisTrend.getStartDtTime() > Constant.DAY * 30 * 3) {
//            logger.info("忽略无效数据: {}", analysisTrend);
//            return result;
//        }
//        for (AnalysisTrend trend : otherTrendList) {
//            //移除趋势持续时间过长的数据 (可能有停牌)
//            if (trend.getEndDtTime() - trend.getStartDtTime() > Constant.DAY * 30 * 3) {
//                logger.info("忽略无效数据: {}", trend);
//                continue;
//            }
//            //出现重叠的2种情况
//            if ((analysisTrend.getStartDtTime() >= trend.getStartDtTime() && analysisTrend.getStartDtTime() <= trend.getEndDtTime())
//                    || analysisTrend.getEndDtTime() >= trend.getStartDtTime() && analysisTrend.getEndDtTime() <= trend.getEndDtTime()) {
//                long startTime = Math.max(analysisTrend.getStartDtTime(), trend.getStartDtTime());
//                long endTime = Math.min(analysisTrend.getEndDtTime(), trend.getEndDtTime());
//                OverlapTrend overlapTrend = new OverlapTrend();
//                overlapTrend.setStartTime(startTime);
//                overlapTrend.setEndTime(endTime);
//                overlapTrend.addTrend(trend);
//                overlapTrend.addTrend(analysisTrend);
//                overlapTrend.addCode(trend.getCode());
//                overlapTrend.addCode(analysisTrend.getCode());
//                if (overlapTrend.getEndTime() - overlapTrend.getStartTime() > Constant.DAY * 7 &&(overlapTrend.getEndTime() - overlapTrend.getStartTime() < Constant.DAY * 80 ))
//                    result.add(overlapTrend);
//            }
//        }
//        return result;
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

    private void removeFirst(Set<OverlapTrend> allBack) {
        Iterator<OverlapTrend> iterator = allBack.iterator();
        if (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }

    }

    private AnalysisTrend getAnalysisTrend(Stock temp, AnalysisTrendModel sortList) {
        AnalysisTrend analysisTrend = new AnalysisTrend();
        analysisTrend.setCode(temp.getCode());
        analysisTrend.setTrend(sortList.getCurTrend().toString());
        String start = DateUtils.sys.format(sortList.getStart().getDt());
        analysisTrend.setStartDt(start);
        String end = DateUtils.sys.format(sortList.getEnd().getDt());
        analysisTrend.setEndDt(end);
        analysisTrend.setWave(sortList.getWave());
        analysisTrend.setMax(sortList.getMax().getMax());
        analysisTrend.setMin(sortList.getMin().getMin());
        analysisTrend.setObserverIndustry(temp.getObserverIndustry());
        return analysisTrend;
    }

}
