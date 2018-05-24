package com.it.bean;


import com.it.ReverseTrendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AnalysisTrendModel {
    private Daily max;
    private Daily min;
    private Queue<Daily> container = new ConcurrentLinkedQueue<>();
    private int days = 30;
    private double wave = 0.1;
    private Trend curTrend;
    private double curWave;
    //反向波动
    private double reverseWave = 0.1;
    private Logger logger = LoggerFactory.getLogger(AnalysisTrendModel.class);

    public AnalysisTrendModel() {
    }

    public AnalysisTrendModel(int days, double wave) {
        this.days = days;
        this.wave = wave;
    }

    /**
     * @param daily
     * @return true 出现符合条件的k线
     */
    public boolean add(Daily daily) {
        if (daily.getClose() == 0 && daily.getMax() == 0) {
            logger.info("数据错误 代码 {} 日期 {}", daily.getCode(), daily.getDt());
            return false;
        }

        if (container.size() >= days) {
            Daily remove = container.remove();
            System.out.println(remove);
        }
        container.add(daily);
        if (max == null) {
            max = daily;
            min = daily;
        } else {
            if (curTrend != null) {//出现相反方向运动
                if (Trend.UP == curTrend) {
                    if ((max.getMax() - daily.getClose()) / daily.getClose() >= reverseWave) {
                        compare(daily);
                        throw new ReverseTrendException("当前趋势:" + curTrend + "- max" + max + "daily" + daily);
                    }
                }else if(curTrend == Trend.DOWN){
                    if ((min.getMax() - daily.getClose()) / daily.getClose() >= reverseWave) {
                        compare(daily);
                        throw new ReverseTrendException("当前趋势:" + curTrend + "- min" + min + "daily" + daily);
                    }
                }
            }
            compare(daily);

            double wave = max.getMax() - min.getMin(), wavePer;
            if (max.getDt().compareTo(min.getDt()) < 0)
                wavePer = wave / max.getClose();
            else
                wavePer = wave / min.getClose();

            boolean isTrend = wavePer >= this.wave;
            if (isTrend) {
                if (curTrend != null) {
                    if (curTrend == getCurTrend() && curWave == wavePer)
                        isTrend = false;
                } else {
                    curTrend = getCurTrend();
                }
                curWave = wavePer;

            }
            return isTrend;
        }
        return false;
    }

    private void compare(Daily daily) {
        if (max.getMax() < daily.getClose()) {
            max = daily;
            if (curTrend != null)
                days = days + 5;
        }
        if (min.getMin() > daily.getClose()) {
            min = daily;
            if (curTrend != null)
                days = days + 5;
        }
    }

    public Trend getCurTrend() {
        double wave = max.getMax() - min.getMin(), wavePer;
        if (max.getDt().compareTo(min.getDt()) < 0)
            wavePer = wave / max.getClose();
        else
            wavePer = wave / min.getClose();
        if (wavePer < this.wave) {
            return Trend.WAVE;
        }
        if (max.getDt().compareTo(min.getDt()) < 0) {
            return Trend.DOWN;
        } else {
            return Trend.UP;
        }
    }

    public Daily getStart() {
        if (max.getDt().compareTo(min.getDt()) < 0)
            return max;
        else
            return min;
    }

    public Daily getEnd() {
        if (max.getDt().compareTo(min.getDt()) > 0)
            return max;
        else
            return min;
    }

    public double getWave() {
        double wave = max.getMax() - min.getMin(), wavePer;
        if (max.getDt().compareTo(min.getDt()) < 0)
            wavePer = wave / max.getClose();
        else
            wavePer = wave / min.getClose();
        return wavePer;
    }

    public Daily getMax(){
        return max;
    }

    public Daily getMin(){
        return min;
    }

    public void clear() {
        Iterator<Daily> iterator = container.iterator();
        Daily start = getStart();
        Daily end = getEnd();
        while (iterator.hasNext()) {
            Daily daily = iterator.next();
            if (daily.getDt().compareTo(start.getDt()) >= 0 ||
                    daily.getDt().compareTo(end.getDt()) <= 0) {
                iterator.remove();
            }
        }
        if (container.isEmpty()) {
            max = null;
            min = null;
        } else {
            max = container.peek();
            min = container.peek();
        }
        while (iterator.hasNext()) {
            Daily next = iterator.next();
            if (max.getMax() < next.getMax())
                max = next;
            if (max.getMin() > next.getMin())
                min = next;
        }


    }
}
