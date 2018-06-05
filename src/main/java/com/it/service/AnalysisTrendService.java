package com.it.service;

import com.it.ReverseTrendException;
import com.it.bean.AnalysisTrend;
import com.it.bean.AnalysisTrendModel;
import com.it.bean.Daily;
import com.it.bean.Stock;
import com.it.repository.AnalysisTrendMapper;
import com.it.repository.DailyMapper;
import com.it.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class AnalysisTrendService {
    @Autowired
    private AnalysisTrendMapper trendMapper;
    @Autowired
    private DailyMapper dailyMapper;
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
        AnalysisTrendModel sortList = new AnalysisTrendModel(90,0.01);
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
        if(CollectionUtils.isEmpty(results) && curTrend != null)
            results.add(curTrend);

        return results;
    }

    public AnalysisTrend getAnalysisTrend(Stock temp, AnalysisTrendModel sortList) {
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
