package com.it.collect;

import com.it.bean.Daily;
import com.it.bean.Finance;
import com.it.bean.Stock;
import com.it.repository.StockMapper;
import com.it.util.Constant;
import com.it.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
                stock.getMarket() + stock.getCode() + "/nc.shtml";
        webDriver.get(url);
        String total = webDriver
                .findElement(By.xpath("//*[@id=\"hqDetails\"]/table/tbody/tr[3]/td[2]")).getText();
        String open = webDriver.findElement(By.xpath("//*[@id=\"hqDetails\"]/table/tbody/tr[1]/td[1]")).getText();
        String max = webDriver.findElement(By.xpath("//*[@id=\"hqDetails\"]/table/tbody/tr[2]/td[1]")).getText();
        String min = webDriver.findElement(By.xpath("//*[@id=\"hqDetails\"]/table/tbody/tr[3]/td[1]")).getText();
        String close = webDriver.findElement(By.xpath("//*[@id=\"hqDetails\"]/table/tbody/tr[4]/td[1]")).getText();
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
                daily.setTrxTotal((long) (Double.parseDouble(trxTotal.replaceAll("万手", "")) * 10000));
            } else
                daily.setTrxTotal((long) Double.parseDouble(trxTotal.replaceAll("手", "")));
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
        daily.setTrxAmt(trxAmt);
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
        Elements select = document.select("#ctl16_contentdiv > table > tbody >tr");
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

    public List<Stock> catchIndustryCode(String industry) {
        String url = "http://vip.stock.finance.sina.com.cn/mkt/#new_ysjs";
        final WebDriver webDriver = new ChromeDriver();
        webDriver.get(url);
//        WebDriverWait wait = new WebDriverWait(webDriver, 20);
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"tbl_wrap\"]/div/a[2]")));
        List<Stock> collectResult = new LinkedList<>();
        List<WebElement> elements = webDriver.findElements(By.xpath("//*[@id=\"tbl_wrap\"]/table/tbody/tr"));
        for (WebElement webelement : elements) {
            Stock stock = new Stock();
            stock.setIndustry(industry);
            stock.setRemark("分析样本");
            String name = webelement.findElement(By.xpath("th[1]/a")).getText();
            if (name.contains("sh")) {
                stock.setMarket("sh");
                name = name.replace("sh", "").trim();
            } else {
                stock.setMarket("sz");
                name = name.replace("sz", "").trim();
            }
            String code = webelement.findElement(By.xpath("th[2]/a")).getText();
            stock.setCode(code);
            stock.setName(name);
            stock.setDt(new Date());
//            Stock stock1 = stockMapper.getStock(stock.getCode(), null);
//            if (stock1 == null)
//                stockMapper.saveStock(stock);
        }
        return collectResult;
    }

}
