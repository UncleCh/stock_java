package com.it.bean;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stock_info")
public class StockInfo {
    @Id
    private ObjectId id;

    private String todayMax;
    private String buy5_n;
    private String remark;
    private String buy2_n;
    private String buy5_m;
    private String buy2_m;
    private String currcapital;
    private String buy4_m;
    private String buy3_m;
    private String buy4_n;
    private String buy3_n;
    private String all_value;
    private String time;
    private String turnover;
    private String downLimit;
    private String swing;
    private String totalcapital;
    private String appointRate;
    private String diff_rate;
    private String appointDiff;
    private String sell1_n;
    private String yestodayClosePrice;
    private String sell1_m;
    private String todayMin;
    private String max52;
    private String diff_money;
    private String code;
    private String sell2_m;
    private String sell2_n;
    private String min52;
    private String buy1_m;
    private String pe;
    private String market;
    private String buy1_n;
    private String pb;
    private String highLimit;
    private String tradeNum;
    private int state;
    private String sell3_m;
    private String openPrice;
    private String circulation_value;
    private String sell5_n;
    private String date;
    private String sell5_m;
    private String closePrice;
    private String name;
    private String sell3_n;
    private String sell4_n;
    private String tradeAmount;
    private String sell4_m;
    private String nowPrice;

    public String getTodayMax() {
        return todayMax;
    }

    public void setTodayMax(String todayMax) {
        this.todayMax = todayMax;
    }

    public String getBuy5_n() {
        return buy5_n;
    }

    public void setBuy5_n(String buy5_n) {
        this.buy5_n = buy5_n;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBuy2_n() {
        return buy2_n;
    }

    public void setBuy2_n(String buy2_n) {
        this.buy2_n = buy2_n;
    }

    public String getBuy5_m() {
        return buy5_m;
    }

    public void setBuy5_m(String buy5_m) {
        this.buy5_m = buy5_m;
    }

    public String getBuy2_m() {
        return buy2_m;
    }

    public void setBuy2_m(String buy2_m) {
        this.buy2_m = buy2_m;
    }

    public String getCurrcapital() {
        return currcapital;
    }

    public void setCurrcapital(String currcapital) {
        this.currcapital = currcapital;
    }

    public String getBuy4_m() {
        return buy4_m;
    }

    public void setBuy4_m(String buy4_m) {
        this.buy4_m = buy4_m;
    }

    public String getBuy3_m() {
        return buy3_m;
    }

    public void setBuy3_m(String buy3_m) {
        this.buy3_m = buy3_m;
    }

    public String getBuy4_n() {
        return buy4_n;
    }

    public void setBuy4_n(String buy4_n) {
        this.buy4_n = buy4_n;
    }

    public String getBuy3_n() {
        return buy3_n;
    }

    public void setBuy3_n(String buy3_n) {
        this.buy3_n = buy3_n;
    }

    public String getAll_value() {
        return all_value;
    }

    public void setAll_value(String all_value) {
        this.all_value = all_value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTurnover() {
        return turnover;
    }

    public void setTurnover(String turnover) {
        this.turnover = turnover;
    }

    public String getDownLimit() {
        return downLimit;
    }

    public void setDownLimit(String downLimit) {
        this.downLimit = downLimit;
    }

    public String getSwing() {
        return swing;
    }

    public void setSwing(String swing) {
        this.swing = swing;
    }

    public String getTotalcapital() {
        return totalcapital;
    }

    public void setTotalcapital(String totalcapital) {
        this.totalcapital = totalcapital;
    }

    public String getAppointRate() {
        return appointRate;
    }

    public void setAppointRate(String appointRate) {
        this.appointRate = appointRate;
    }

    public String getDiff_rate() {
        return diff_rate;
    }

    public void setDiff_rate(String diff_rate) {
        this.diff_rate = diff_rate;
    }

    public String getAppointDiff() {
        return appointDiff;
    }

    public void setAppointDiff(String appointDiff) {
        this.appointDiff = appointDiff;
    }

    public String getSell1_n() {
        return sell1_n;
    }

    public void setSell1_n(String sell1_n) {
        this.sell1_n = sell1_n;
    }

    public String getYestodayClosePrice() {
        return yestodayClosePrice;
    }

    public void setYestodayClosePrice(String yestodayClosePrice) {
        this.yestodayClosePrice = yestodayClosePrice;
    }

    public String getSell1_m() {
        return sell1_m;
    }

    public void setSell1_m(String sell1_m) {
        this.sell1_m = sell1_m;
    }

    public String getTodayMin() {
        return todayMin;
    }

    public void setTodayMin(String todayMin) {
        this.todayMin = todayMin;
    }

    public String getMax52() {
        return max52;
    }

    public void setMax52(String max52) {
        this.max52 = max52;
    }

    public String getDiff_money() {
        return diff_money;
    }

    public void setDiff_money(String diff_money) {
        this.diff_money = diff_money;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSell2_m() {
        return sell2_m;
    }

    public void setSell2_m(String sell2_m) {
        this.sell2_m = sell2_m;
    }

    public String getSell2_n() {
        return sell2_n;
    }

    public void setSell2_n(String sell2_n) {
        this.sell2_n = sell2_n;
    }

    public String getMin52() {
        return min52;
    }

    public void setMin52(String min52) {
        this.min52 = min52;
    }

    public String getBuy1_m() {
        return buy1_m;
    }

    public void setBuy1_m(String buy1_m) {
        this.buy1_m = buy1_m;
    }

    public String getPe() {
        return pe;
    }

    public void setPe(String pe) {
        this.pe = pe;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getBuy1_n() {
        return buy1_n;
    }

    public void setBuy1_n(String buy1_n) {
        this.buy1_n = buy1_n;
    }

    public String getPb() {
        return pb;
    }

    public void setPb(String pb) {
        this.pb = pb;
    }

    public String getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(String highLimit) {
        this.highLimit = highLimit;
    }

    public String getTradeNum() {
        return tradeNum;
    }

    public void setTradeNum(String tradeNum) {
        this.tradeNum = tradeNum;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getSell3_m() {
        return sell3_m;
    }

    public void setSell3_m(String sell3_m) {
        this.sell3_m = sell3_m;
    }

    public String getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(String openPrice) {
        this.openPrice = openPrice;
    }

    public String getCirculation_value() {
        return circulation_value;
    }

    public void setCirculation_value(String circulation_value) {
        this.circulation_value = circulation_value;
    }

    public String getSell5_n() {
        return sell5_n;
    }

    public void setSell5_n(String sell5_n) {
        this.sell5_n = sell5_n;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSell5_m() {
        return sell5_m;
    }

    public void setSell5_m(String sell5_m) {
        this.sell5_m = sell5_m;
    }

    public String getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(String closePrice) {
        this.closePrice = closePrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSell3_n() {
        return sell3_n;
    }

    public void setSell3_n(String sell3_n) {
        this.sell3_n = sell3_n;
    }

    public String getSell4_n() {
        return sell4_n;
    }

    public void setSell4_n(String sell4_n) {
        this.sell4_n = sell4_n;
    }

    public String getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(String tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getSell4_m() {
        return sell4_m;
    }

    public void setSell4_m(String sell4_m) {
        this.sell4_m = sell4_m;
    }

    public String getNowPrice() {
        return nowPrice;
    }

    public void setNowPrice(String nowPrice) {
        this.nowPrice = nowPrice;
    }
}
