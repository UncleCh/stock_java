package com.it.collect;

import com.it.bean.Stock;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class ExcelCollector {

    /**
     * 读取沪深 300
     */
    public List<Stock> readExcel(String fileName) {
        Workbook wb = getWorkbook(fileName);
        List<Stock> indexLists = new LinkedList<>();
        Sheet sheetAt = wb.getSheetAt(0);
        for (Row row : sheetAt) {
            if (row.getRowNum() != 0) {
                String code = row.getCell(4).getStringCellValue();
                String market = row.getCell(7).getStringCellValue();
                Stock temp = new Stock();
                if ("SHH".equalsIgnoreCase(market)) {
                    temp.setMarket("sh");
                } else {
                    temp.setMarket("sz");
                }
                String dt = row.getCell(0).getStringCellValue();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    temp.setDt(sdf.parse(dt));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                temp.setIndexCode(row.getCell(1).getStringCellValue());
                temp.setCode(code);
                String name = row.getCell(5).getStringCellValue();
                temp.setName(name);
                indexLists.add(temp);
            }
        }
        return indexLists;
    }

    public List<Stock> readCodeExcel(String industry) {
        Workbook wb = getWorkbook("code.xlsx");
        List<Stock> indexLists = new LinkedList<>();
        Sheet sheetAt = wb.getSheetAt(0);
        for (Row row : sheetAt) {
            if (row.getRowNum() != 0) {
                Stock stock = new Stock();
                String[] code = row.getCell(0).getStringCellValue().split("\\.");
                stock.setCode(code[0]);
                stock.setMarket(code[1]);
                String name = row.getCell(1).getStringCellValue();
                stock.setName(name);
                stock.setIndustry(industry);
                stock.setObserverIndustry("分析样本");
                stock.setDt(new Date());
                indexLists.add(stock);
            }
        }
        return indexLists;
    }


    private Workbook getWorkbook(String fileName) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
        Workbook wb = null;
        try {
            if (fileName.endsWith(".xls")) {
                wb = new HSSFWorkbook(inputStream);
            } else {
                wb = new XSSFWorkbook(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wb;
    }

    /**
     * 读取上证100
     */
    public List<Stock> readExcel100(String fileName) throws ParseException {
        Workbook wb = getWorkbook(fileName);
        List<Stock> indexLists = new LinkedList<>();
        Sheet sheetAt = wb.getSheetAt(0);
        for (Row row : sheetAt) {
            if (row.getRowNum() != 0) {
                String stockCode = row.getCell(4).getStringCellValue();
                String dt = row.getCell(0).getStringCellValue();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Stock temp = new Stock();
                temp.setDt(sdf.parse(dt));
                temp.setCode(stockCode);
                temp.setIndexCode(row.getCell(1).getStringCellValue());
                indexLists.add(temp);
            }
        }
        return indexLists;

    }


}
