package com.lb.lbclock.utils;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {



    public static final String format1 = "yyyy-MM-dd HH:mm:ss";
    public static final String format2 = "yyyy-MM-dd";
    public static final String format3 = "yyyyMMddHHmm";
    public static final String format4 = "yyyyMMddHHmmss";

    // 根据日期根据将date类型的日期转化为string
    public static String formatDate(Date date, String format) {
        if (null == date){
            return null;
        }
        if (StringUtils.isBlank(format)){
            return null;
        }
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
    /**
     * @param date
     *  根据yyyy-MM-dd HH:mm:ss 将date类型转化为string
     *
     * */
    public static String dateToString1(Date date) {
        if (null == date){
            return null;
        }
        return formatDate(date, format1);
    }
    /**
     * 根据yyyy-MM-dd 将date类型转化为string
     * */
    public static String dateToString2(Date date) {
        if (null == date){
            return null;
        }
        return formatDate(date, format2);
    }
    /**
     * 根据yyyyMMddHHmm 将date类型转化为string
     * */
    public static String dateToString3(Date date) {
        if (null == date){
            return null;
        }
        return formatDate(date, format3);
    }
    /**
     * 根据yyyyMMddHHmmss 将date类型转化为string
     * */
    public static String dateToString4(Date date) {
        if (null == date){
            return null;
        }
        return formatDate(date, format4);
    }


    /**
     * 指定日期格式
     * @param dates
     * @param pattern
     * @return
     */
    public static Date parseDate(String dates,String pattern){
        if(StringUtils.isBlank(dates)){ return null;}
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return dateFormat.parse(dates);
        } catch (ParseException e) {
        }
        return null;
    }
    /**
     * 日期格式为yyyy-MM-dd HH:mm:ss
     * @param dates
     * @return
     */
    public static Date stringToDate1(String dates){
        if(StringUtils.isBlank(dates)){ return null;}
        return parseDate(dates, format1);
    }
}
