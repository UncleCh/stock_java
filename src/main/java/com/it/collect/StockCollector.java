package com.it.collect;

import com.it.bean.Daily;
import com.it.bean.Finance;
import com.it.bean.Stock;
import com.it.repository.StockMapper;
import com.it.util.BuyWeight;
import com.it.util.Constant;
import com.it.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 线程不安全
 */
@Component
public class StockCollector {

    private Logger logger = LoggerFactory.getLogger(StockCollector.class);

    private Map<String, WebDriver> browers = new ConcurrentHashMap<>();

    static {
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            System.setProperty("webdriver.chrome.driver",
                    "D:/work\\it\\stock_java\\src\\main\\resources/chromedriver.exe");
        } else {
            System.setProperty("webdriver.chrome.driver",
                    "/Users/chenxiujiang/Downloads/chromedriver");
        }

    }


    public Date getLastedDate(Stock stock, WebDriver webDriver) {
        String url = "http://finance.sina.com.cn/realstock/company/" +
                stock.getMarket() + stock.getCode() + "/nc.shtml";
        webDriver.get(url);
        String dateStr = webDriver.findElement(By.xpath("//*[@id=\"hqTime\"]")).getText();
        return DateUtils.parse("yyyy-MM-dd hh:mm:ss", dateStr);
    }

    public static void main(String[] args) {
        System.out.println(DateUtils.parse("yyyy-MM-dd", "2018-01-22 11:09:09"));
    }

    public Daily collectStockCode(Stock stock, WebDriver webDriver) throws IOException {
        String url = "http://finance.sina.com.cn/realstock/company/" +
                stock.getMarket().toLowerCase() + stock.getCode() + "/nc.shtml";
//        http://finance.sina.com.cn/realstock/company/sz000807/nc.shtml
        webDriver.get(url);
        String total = webDriver
                .findElement(By.xpath("//*[@id=\"hqDetails\"]/table/tbody/tr[3]/td[2]")).getText();
        String open = webDriver.findElement(By.xpath("//*[@id=\"hqDetails\"]/table/tbody/tr[1]/td[1]")).getText();
        String max = webDriver.findElement(By.xpath("//*[@id=\"hqDetails\"]/table/tbody/tr[2]/td[1]")).getText();
        String min = webDriver.findElement(By.xpath("//*[@id=\"hqDetails\"]/table/tbody/tr[3]/td[1]")).getText();
        String close = webDriver.findElement(By.xpath("//*[@id=\"price\"]")).getText();
        String amplitude = webDriver.findElement(By.xpath("//*[@id=\"hqDetails\"]/table/tbody/tr[1]/td[3]")).getText();
        String trxPer = webDriver.findElement(By.xpath("//*[@id=\"hqDetails\"]/table/tbody/tr[2]/td[3]")).getText();
        String pe = webDriver.findElement(By.xpath("//*[@id=\"hqDetails\"]/table/tbody/tr[4]/td[3]")).getText().trim();
        String trxAmt = webDriver.findElement(By.xpath("//*[@id=\"hqDetails\"]/table/tbody/tr[2]/td[2]")).getText();
        String style = webDriver.findElement(By.xpath("//*[@id=\"closed\"]")).getCssValue("style");
        String dateStr = webDriver.findElement(By.xpath("//*[@id=\"hqTime\"]")).getText();
        String chargeP = webDriver.findElement(By.xpath("//*[@id=\"changeP\"]")).getText();
        String trxTotal = webDriver.findElement(By.xpath("//*[@id=\"hqDetails\"]/table/tbody/tr[1]/td[2]")).getText();
        if (style.contains("display: block")) {
            logger.info("code {} 停牌", stock.getCode());
            return null;
        }
        Daily daily = new Daily();
        if (!trxTotal.contains("--")) {
            if (trxTotal.contains("万")) {
                daily.setTrxAmt( String.valueOf((int)(Double.parseDouble(trxTotal.replaceAll("万手", "")) * 10000)));
            } else
                daily.setTrxAmt( String.valueOf((int)Double.parseDouble(trxTotal.replaceAll("手", ""))));
        }
        if (!chargeP.contains("--") && StringUtils.isNotEmpty(chargeP))
            daily.setChangeP(Double.parseDouble(chargeP.replaceAll("%", "")));
        if (!total.contains("--"))
            daily.setTotal(Double.parseDouble(total.replace("亿", "")));
        if (!open.contains("--"))
            daily.setOpen(Double.parseDouble(open));
        if (!max.contains("--"))
            daily.setMax(Double.parseDouble(max));
        if (!min.contains("--"))
            daily.setMin(Double.parseDouble(min));
        if (!close.contains("--"))
            daily.setClose(Double.parseDouble(close));
        if (!amplitude.contains("--"))
            daily.setAmplitude(Double.parseDouble(amplitude.replace("%", "")));
        if (!trxPer.contains("--"))
            daily.setTrxPer(Double.parseDouble(trxPer.replace("%", "")));
        if (isNumeric(pe) && !trxPer.contains("--"))
            daily.setPeRatio(Double.parseDouble(pe));
        else
            logger.info("错误的数据 {} 市盈率 {}", url, pe);
        daily.setCode(stock.getCode());
        daily.setTrxTotal(trxAmt);
        Date dt = null;
        if (StringUtils.isNotEmpty(dateStr))
            dt = DateUtils.parse("yyyy-MM-dd", dateStr);
        else
            dt = DateUtils.getCurDate();
        daily.setDt(dt);
        return daily;
    }

    public List<Finance> collectFinance(Stock stock, WebDriver webDriver) {
        String url = "http://www.iwencai.com/data-robot/extract-new?" +
                "query=" + stock.getCode() + "&querytype=stock&qsData=pc_~soniu~others~homepage~box~history&dataSource=hp_history";
        webDriver.get(url);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String text = webDriver.findElement(By.xpath(
                "//*[@id=\"dp_tablemore_2\"]/div/div/div/div/table/tbody/tr[1]/td[1]/div")).getText();
        Date lastDate = DateUtils.parse("yyyyMMdd", text);
        Finance finance = new Finance();
        finance.setCode(stock.getCode());
        finance.setDt(lastDate);
        List<Finance> finances = new LinkedList<>();
        finances.add(finance);
        for (int i = 2; i <= 4; i++) {
            String dt = webDriver.findElement(By.xpath(
                    "//*[@id=\"dp_tablemore_2\"]/div/div/div/div/table/tbody/tr[" + i + "]/td[1]/div")).getText();
            Date temp = DateUtils.parse("yyyyMMdd", dt);
            finance = new Finance();
            finance.setDt(temp);
            finance.setCode(stock.getCode());
            String income = webDriver.findElement(By.xpath(
                    "//*[@id=\"dp_tablemore_2\"]/div/div/div/div/table/tbody/tr[" + i + "]/td[2]/div")).getText().replace(",", "");
            if (!income.contains("--")) {
                double tempIncome = 0;
                if (income.contains("亿")) {
                    tempIncome = Double.parseDouble(income.replace("亿", "")) * 10000;
                } else {
                    tempIncome = Double.parseDouble(income.replace("万", ""));
                }
                finance.setIncome(tempIncome);
            }
            String incomePer = webDriver.findElement(By.xpath(
                    "//*[@id=\"dp_tablemore_2\"]/div/div/div/div/table/tbody/tr[\"+i+\"]/td[3]/div/a")).getText().replace(",", "");
            if (!incomePer.contains("--")) {
                finance.setIncomePer(Double.parseDouble(incomePer));
            }

            String marketIncome = webDriver.findElement(By.xpath(
                    "//*[@id=\"dp_tablemore_2\"]/div/div/div/div/table/tbody/tr[\"+i+\"]/td[4]/div/a")).getText();
            if (!marketIncome.contains("--")) {
                double tempIncome = 0;
                marketIncome = marketIncome.replace(",", "").replace("亿", "");
                if (marketIncome.contains("亿")) {
                    tempIncome = Double.parseDouble(marketIncome) * 10000;
                } else {
                    tempIncome = Double.parseDouble(marketIncome);
                }
                finance.setMarketIncome(tempIncome);
            }
            String marketIncomePer = webDriver.findElement(By.xpath(
                    "//*[@id=\"dp_tablemore_2\"]/div/div/div/div/table/tbody/tr[\"+i+\"]/td[5]/div/a")).getText();
            if (!marketIncomePer.contains("--")) {
                finance.setMarketIncomePer(Double.parseDouble(marketIncomePer));
            }
            String perIncome = webDriver.findElement(By.xpath(
                    "//*[@id=\"dp_tablemore_2\"]/div/div/div/div/table/tbody/tr[\"+i+\"]/td[6]/div/a")).getText();
            if (!perIncome.contains("--")) {
                finance.setPerIncome(Double.parseDouble(perIncome));
            }
            String perAssets = webDriver.findElement(By.xpath(
                    "//*[@id=\"dp_tablemore_2\"]/div/div/div/div/table/tbody/tr[\"+i+\"]/td[7]/div/a")).getText();
            if (!perAssets.contains("--")) {
                finance.setPerAssets(Double.parseDouble(perAssets));
            }
            String perCacsh = webDriver.findElement(
                    By.xpath("//*[@id=\"dp_tablemore_2\"]/div/div/div/div/table/tbody/tr[" + i + "]/td[8]/div")).getText();
            if (!perCacsh.contains("--")) {
                finance.setPerCacsh(Double.parseDouble(perCacsh));
            }
            String selfAssetsPer = webDriver.findElement(By.xpath(
                    "//*[@id=\"dp_tablemore_2\"]/div/div/div/div/table/tbody/tr[\"+i+\"]/td[9]/div/a")).getText();
            if (!selfAssetsPer.contains("--")) {
                finance.setSelfAssetsPer(Double.parseDouble(selfAssetsPer));
            }
            String selfMarketPer = webDriver.findElement(By.xpath(
                    "//*[@id=\"dp_tablemore_2\"]/div/div/div/div/table/tbody/tr[\"+i+\"]/td[10]/div")).getText();
            if (!selfMarketPer.contains("--")) {
                finance.setSelfMarketPer(Double.parseDouble(selfMarketPer));
            }
            finances.add(finance);
        }
        return finances;
    }

    //方法三：
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9.]*");
        return pattern.matcher(str).matches();
    }

    public List<Daily> collectStockHistory(Stock stock) throws IOException {
        String url = "http://www.aigaogao.com/tools/history.html?s=" + stock.getCode();
        Document document = Jsoup.connect(url).get();
        Elements select = document.select("table > tbody >tr");
        List<Daily> dailyList = new LinkedList<>();
        for (int i = 1; i <= select.size() - 1; i++) {
            Elements td = select.get(i).select("td");
            Daily daily = new Daily();
            String dtStr = td.get(0).select("a").text();
            if (StringUtils.isEmpty(dtStr))
                continue;
            Date dt = DateUtils.parse("MM/dd/yyyy", dtStr);
            daily.setDt(dt);
            String open = td.get(1).text();
            if (StringUtils.isNotEmpty(open))
                daily.setOpen(Double.parseDouble(open.replace("</td>", "")));
            String max = td.get(2).text();
            if (StringUtils.isNotEmpty(max))
                daily.setMax(Double.parseDouble(max.replace("</td>", "")));
            String min = td.get(3).text();
            if (StringUtils.isNotEmpty(min))
                daily.setMin(Double.parseDouble(min.replace("</td>", "")));
            String close = td.get(4).text();
            if (StringUtils.isNotEmpty(close))
                daily.setClose(Double.parseDouble(close.replace("</td>", "")));
            String trxAmt = td.get(6).text();
            if (StringUtils.isNotEmpty(trxAmt)) {
                trxAmt = trxAmt.replace(",", "").replace("</td>", "");
                daily.setTrxAmt(trxAmt);
            }
            String amplitude = td.get(10).text();
            if (StringUtils.isNotEmpty(amplitude)) {
                amplitude = amplitude.replace("%", "").replace("</td>", "").replace("</span>", "").trim();
                daily.setAmplitude(Double.parseDouble(amplitude));
            }
            daily.setCode(stock.getCode());
            dailyList.add(daily);
            if (i > Constant.STOCK_SIZE)
                break;
        }
        return dailyList;
    }

    @Autowired
    StockMapper stockMapper;
    @Autowired
    ExcelCollector excelCollector;

    public List<Stock> catchIndustryCode(String industry) {
//        List<Stock> stocks = getAll();
//        for (Stock stock : stocks) {
////            if (oberverData.contains(stock.getCode()))
////                stockMapper.saveStock(stock);
//        }
//        return stocks;
        Stock query = new Stock();
        query.setIndustry(industry);
        return stockMapper.getStockList(query);
    }

    private List<Stock> getAll(){
        String industry ="房地产",observerIndustry = "周期性行业-代表公司";
        Stock stock = new Stock();
        stock.setCode("600340");
        stock.setName("华夏幸福");
        stock.setWeight(BuyWeight.BUY_SECOND.getWeiht());
        stock.setIndustry(industry);
        stock.setObserverIndustry(observerIndustry);
        stock.setPressurePosition(29.89);
        stock.setSupportPosition(26.71);
        stock.setDt(new Date());
        Stock stock1 = new Stock();
        stock1.setCode("600048");
        stock1.setName("保利地产");
        stock1.setWeight(BuyWeight.BUY_FIRST.getWeiht());
        stock1.setIndustry(industry);
        stock1.setObserverIndustry(observerIndustry);
        stock1.setPressurePosition(14.18);
        stock1.setSupportPosition(11.56);
        stock1.setDt(new Date());

        Stock stock2 = new Stock();
        stock2.setCode("601155");
        stock2.setName("新城控股");
        stock2.setWeight(BuyWeight.BUY_SECOND.getWeiht());
        stock2.setIndustry(industry);
        stock2.setObserverIndustry(observerIndustry);
        stock2.setPressurePosition(34.05);
        stock2.setSupportPosition(27.38);
        stock2.setDt(new Date());
        Stock stock3 = new Stock();
        stock3.setCode("600383");
        stock3.setName("金地集团");
        stock3.setIndustry(industry);
        stock3.setObserverIndustry(observerIndustry);
        stock3.setPressurePosition(11.22);
        stock3.setSupportPosition(10.45);
        stock3.setDt(new Date());
        Stock stock4 = new Stock();
        stock4.setCode("001979");
        stock4.setName("招商蛇口");
        stock4.setIndustry(industry);
        stock4.setObserverIndustry(observerIndustry);
        stock4.setPressurePosition(23.03);
        stock4.setSupportPosition(20.87);
        stock4.setDt(new Date());
        Stock stock5 = new Stock();
        stock5.setCode("000002");
        stock5.setName("万科A");
        stock5.setIndustry(industry);
        stock5.setObserverIndustry(observerIndustry);
        stock5.setPressurePosition(28.42);
        stock5.setSupportPosition(25.17);
        stock5.setDt(new Date());
        Stock stock6 = new Stock();
        stock6.setCode("000069");
        stock6.setName("华侨城A");
        stock6.setIndustry(industry);
        stock6.setObserverIndustry(observerIndustry);
        stock6.setPressurePosition(8.13);
        stock6.setSupportPosition(7.73);
        stock6.setDt(new Date());
        Stock stock7 = new Stock();
        stock7.setCode("000671");
        stock7.setName("阳光城");
        stock7.setIndustry(industry);
        stock7.setObserverIndustry(observerIndustry);
        stock7.setPressurePosition(7.05);
        stock7.setSupportPosition(6.1);
        stock7.setDt(new Date());
        Stock stock8 = new Stock();
        stock8.setCode("600622");
        stock8.setName("光大嘉宝");
        stock8.setIndustry(industry);
        stock8.setObserverIndustry(observerIndustry);
        stock8.setPressurePosition(9.53);
        stock8.setSupportPosition(7.13);
        stock8.setDt(new Date());
        List<Stock> stockList = new LinkedList<>();
        stockList.add(stock);
        stockList.add(stock1);
        stockList.add(stock2);
        stockList.add(stock3);
        stockList.add(stock4);
        stockList.add(stock5);
        stockList.add(stock6);
        stockList.add(stock7);
        stockList.add(stock8);
        return stockList;
    }

}
