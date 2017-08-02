package com.it.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    private static SimpleDateFormat sys = new SimpleDateFormat("yyyy-MM-dd");


    public static  String toSystemDate(String sourceDate){
        try {
            Date date = sdf.parse(sourceDate);
            return sys.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
