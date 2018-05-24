package com.it.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    public static SimpleDateFormat sys = new SimpleDateFormat("yyyy-MM-dd");


    public static String toSystemDate(String sourceDate) {
        try {
            Date date = sdf.parse(sourceDate);
            return sys.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static int distanceDays(String startDate, String endDate) {
        LocalDate startLocal = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endLocal = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Period between = Period.between(startLocal, endLocal);
        return between.getDays();
    }

    public static Date getCurDate(String dateFormat, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String dayStr = sdf.format(date);
        try {
            return sdf.parse(dayStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getCurDate() {
        return getCurDate("yyyy-MM-dd", new Date());
    }

    public static Date parse(String dateFormat, String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
