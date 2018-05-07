package com.autotest.api.utils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DateTimeFormatter {

    public static String msecondsToDBFormat(long mseconds) {
        //1381371010000 to 2016-04-28 18:32:12
        return (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(new Date(mseconds));
    }

    public static String getCurrentSystime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(new Date());
    }

    public static void main(String[] args) {
        System.out.println(getCurrentSystime());
    }
}
