package com.autotest.api.utils;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 时间格式化工具类
 *
 * @author liujinjie
 */
public class DateUtils {

    public static final String FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_1 = "MM/dd HH:mm";
    public static final String FORMAT_2 = "yyyy-MM-dd";
    public static final String FORMAT_3 = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_4 = "yyyy.MM.dd";
    public static final String FORMAT_5 = "yyyyMMddHHmmss";
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);
    public static DateFormat dateformate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static String getCurrentTimeStamp() {
        //获取系统当前时间
        //定义格式，不显示毫秒
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_5);
        return df.format(new Date());
    }

    /**
     * 当第一个参数Date为null时，把第二个参数当默认值返回.
     * 注意:不是格式化类型的意思.
     *
     * @param date
     * @param defaultValue 当date为null时返回默认值
     * @return
     * @author liujinjie
     */
    public static String formateDate(Date date, String defaultValue) {
        if (defaultValue == null) {
            defaultValue = "--";
        }
        return date == null ? defaultValue : sdf.format(date);
    }

    public static String formateDate(Date date) {
        return formateDate(date, null);
    }

    /**
     * 格式化当前时间.
     * 格式化后如:2014-04-09 18:00:00
     *
     * @return
     * @author liujinjie
     */
    public static String formateDate() {
        DateFormat df = new SimpleDateFormat(FORMAT_DATE);
        String dateresult = df.format(new Date());
        return dateresult;
    }

    public static String formateDateForToday() {
        DateFormat df = new SimpleDateFormat(FORMAT_2);
        String dateresult = df.format(new Date());
        return dateresult;
    }

    /**
     * 格式化当前时间.
     * 格式化后如:2014-04-09 18:00:00
     *
     * @return
     * @author raodehui
     */
    public static String formateDateToString(Date date) {
        DateFormat df = new SimpleDateFormat(FORMAT_DATE);
        String dateresult = df.format(date);
        return dateresult;
    }

    /**
     * 格式化当前时间
     *
     * @param formatType 格式化规则
     * @return
     * @author liujinjie
     */
    public static String formateDate(String formatType) {
        DateFormat df = new SimpleDateFormat(formatType);
        String dateresult = df.format(new Date());
        return dateresult;
    }

    /**
     * 获取昨天的日期
     * 格式为:yyyy-MM-dd
     *
     * @return
     * @auther haoxiaosha
     */
    public static String getYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat(FORMAT_2).format(cal.getTime());
        return yesterday;
    }

    public static String getDatefortable() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
        return yesterday;
    }

    /**
     * 格式化昨天开始时间
     *
     * @return
     */
    public static String formateYesterdayStart() {
        return formateDateByType(yesterdayStart(), FORMAT_DATE);
    }

    public static String formateYesterdayStart(String formatType) {
        return formateDateByType(yesterdayStart(), formatType);
    }

    /**
     * 格式化昨天结束时间
     *
     * @return
     */
    public static String formateYesterdayEnd() {
        return formateDateByType(yesterdayEnd(), FORMAT_DATE);
    }

    public static String formateYesterdayEnd(String formatType) {
        return formateDateByType(yesterdayEnd(), formatType);
    }

    /**
     * @param date
     * @return
     * @Title: formateDateMonth
     * @author: lengxuefei
     * @CreateDate: 2014年4月22日
     * @Description: 格式化到月（“yyyy-MM”）
     */
    public static String formateDateMonth(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM");
        return date == null ? "" : df.format(date);
    }

    /**
     * 按指定格式格式化日期
     *
     * @param date
     * @param formatType
     * @return
     * @author liujinjie
     */
    public static String formateDateByType(Date date, String formatType) {
        DateFormat df = new SimpleDateFormat(formatType);
        return df.format(date);
    }

    /**
     * 验证time是否超时.
     * 超过maxMin认为超时
     *
     * @param time   验证时间,毫秒
     * @param maxMin 单位分钟
     * @return 小于指定分钟返回true;否则返回false
     * @author liujinjie
     */
    public static boolean checkTimeOut(long time, int maxMin) {
        try {
            long now = System.currentTimeMillis();
            long nowSec = now - time;
            //  logger.info("nowTime={},valTime={},sec={}", now, time, nowSec);
            if (nowSec > 0 && nowSec < (maxMin * 60 * 1000)) {
                return true;
            }
        } catch (Exception e) {
            logger.error("验证是否超时出错.{}", e.getMessage());
        }
        return false;
    }

    /**
     * 验证Date是否超时
     *
     * @param date   验证时间
     * @param maxMin 单位分钟
     * @return true没有超时;false已经超时
     * @author liujinjie
     */
    public static boolean checkTimeOut(Date date, int maxMin) {
        return checkTimeOut(date.getTime(), maxMin);
    }

    /**
     * <pre>
     * 得到一个日期对象＋n月后的日期
     *
     * addMonths(2014-07-04, 1) = 2014-08-04
     * </pre>
     *
     * @param startDate
     * @param month
     * @return
     * @author liujinjie
     */
    public static Date addMonths(Date startDate, int month) {
        return new DateTime(startDate).plusMonths(month).toDate();
        //return org.apache.commons.lang3.time.DateUtils.addMonths(startDate, month);
    }

    /**
     * 得到一个日期对象 +n天后的日期
     *
     * @param startDate 开始日期
     * @param days      结束日期
     * @return Date
     * @author lichangjiang
     * @date 2015年11月30日 下午9:08:46
     */
    public static Date addDays(Date startDate, int days) {
        return new DateTime(startDate).plusDays(days).toDate();
    }

    /**
     * 将日期字符串转为日期
     * getDateByStr("2014-05-08", "yyyy-MM-dd") = 得到"2014-05-08"对应的日期
     *
     * @param strDate 日期字符串
     * @param format  日期字符串格式
     * @return
     * @throws ParseException
     * @author liujinjie
     */
    public static Date getDateByStr(String strDate, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(strDate);
        return date;
    }

    /**
     * <pre>
     * 计算2个日期的月差值
     *
     * getPlusMonth("2014-06-04", "2014-07-04", true ) = 1
     * getPlusMonth("2014-06-04", "2014-07-04", false) = 0
     *
     * getPlusMonth("2014-06-04", "2014-08-02", true ) = 1
     * getPlusMonth("2014-06-04", "2014-08-02", false) = 1
     *
     * getPlusMonth("2014-06-04", "2014-08-04", true ) = 2
     * getPlusMonth("2014-06-04", "2014-08-04", false) = 1
     *
     * getPlusMonth("2014-06-04", "2014-08-05", true ) = 2
     * getPlusMonth("2014-06-04", "2014-08-05", false) = 2
     *
     * </pre>
     *
     * @param startDate        开始日期
     * @param nowDate          结束日期
     * @param isIncludeSameDay true同一天,算一个月;false同一天,不算一个月
     * @return
     * @author liujinjie
     */
    public static Integer getPlusMonth(Date startDate, Date nowDate, Boolean isIncludeSameDay) {
        try {
            startDate = sdf.parse(sdf.format(startDate));
            nowDate = sdf.parse(sdf.format(nowDate));
        } catch (ParseException e) {
        }

        if (isIncludeSameDay == null) {
            isIncludeSameDay = true;
        }
        DateTime startTime = new DateTime(startDate);
        DateTime endTime = new DateTime(nowDate);
        if (startTime.isAfter(endTime)) {
            return 0;
        }

        int year = endTime.getYear() - startTime.getYear();
        int monty = endTime.getMonthOfYear() - startTime.getMonthOfYear();

        int m = year * 12 + monty;

        int dayStartTime = startTime.getDayOfMonth();
        int dayEndTime = endTime.getDayOfMonth();
        int monthStartTime = startTime.getMonthOfYear() - 1;
        int monthEndTime = endTime.getMonthOfYear() - 1;

        int subMonth = m;
        if (isIncludeSameDay) { // 6月4日到7月4日,算一个月
            if (dayEndTime < dayStartTime) {
                subMonth = subMonth - 1;
            }
        } else { // 6月4日到7月4日,不算一个月
            if (dayEndTime <= dayStartTime) {
                subMonth = subMonth - 1;
            }
        }
        return subMonth;
    }

    /**
     * <pre>
     * 整存宝计算期数,计算2个日期的月差值.
     * 如:购买12个月整存宝,计算到指定日期的期数.
     *
     * getPlusMonthForPlan("2014-06-04", "2014-07-04") = 1
     * getPlusMonthForPlan("2014-06-04", "2014-08-02") = 1
     * getPlusMonthForPlan("2014-06-04", "2014-08-04") = 2
     * getPlusMonthForPlan("2014-06-04", "2014-08-05") = 2
     *
     * </pre>
     *
     * @param startDate 开始日期
     * @param nowDate   结束日期
     * @return
     * @author liujinjie
     */
    public static Integer getPlusMonthForPlan(Date startDate, Date nowDate) {
        return getPlusMonth(startDate, nowDate, true);
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒  (较大时间-较小时间)
     *
     * @param maxTime 较大时间参数
     * @param minTime 较小时间参数
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceHour(Date maxTime, Date minTime) {
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            long time1 = maxTime.getTime();
            long time2 = minTime.getTime();
            long diff = time1 - time2;
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (Exception e) {
            logger.error("格式化日期异常", e.getMessage());
        }
        if (day == 0 && hour == 0 && min == 0) {
            return sec + "秒";
        } else if (day == 0 && hour == 0) {
            return min + "分";
        } else if (day == 0) {
            return hour + "小时";
        } else if (day != 0 && hour == 0) {
            return day + "天";
        } else {
            return day + "天" + hour + "小时";
        }
    }

    /**
     * 两个日期相差的天数
     *
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @return 两个日期相差天数
     */
    public static long getDateMargin(Date beginDate, Date endDate) {
        long margin = 0;

        margin = endDate.getTime() - beginDate.getTime();

        margin = margin / (1000 * 60 * 60 * 24);

        return margin;
    }

    /**
     * 如果天数相减为负数 就返回 -1
     * 两个日期相差的天数
     *
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @return 两个日期相差天数
     */
    public static long getDateMargin2Activity(Date beginDate, Date endDate) {
        long margin = 0;
        margin = endDate.getTime() - beginDate.getTime();
        if (margin < 0) {
            return -1;
        }
        margin = margin / (1000 * 60 * 60 * 24);
        return margin;
    }


    /**
     * 按指定格式格式化字符串返回日期
     *
     * @param date
     * @param formatType
     * @return
     * @author lengxuefei
     */
    public static Date parseDateByType(String date, String formatType) {
        DateFormat df = new SimpleDateFormat(formatType);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            //  logger.error("按指定格式{}格式化字符串返回日期{},{}", formatType, date, e);
        }
        return null;
    }

    /**
     * 将时间格式转换为年月日的long类型
     *
     * @param d
     * @return
     * @author wangshaofen
     */
    public static long chDateYmdToLong(Date d) {
        try {
            String timeStr = new DateTime(d).toString("yyyy-MM-dd");
            return new DateTime(timeStr).getMillis();
        } catch (Exception e) {
            logger.error("按指定格式格式化日期返回Long", e.getMessage());
        }
        return 0;

    }

    /**
     * 获取当月第一天
     *
     * @return
     * @author wangzhen
     */
    public static String getFirstMonthDay() {
        // 获取当月第一天
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        // 获取前月的第一天
        Calendar cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 0);
        cale.set(Calendar.DAY_OF_MONTH, 1);
        return format.format(cale.getTime());
    }

    /**
     * 获取当月最后一天
     *
     * @return
     * @author wangzhen
     */
    public static String getLastMonthDay() {
        // 获取当月最后一天
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        // 获取前月的最后一天
        Calendar cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, 0);
        return format1.format(cale.getTime());
    }


    /**
     * 取当天零点零分零秒
     *
     * @return
     * @author lengxuefei
     */
    public static Date getTodayStart(Date changeDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(changeDate);
        //如果没有这种设定的话回去系统的当期的时间
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = new Date(calendar.getTimeInMillis());
        return date;
    }

    /**
     * 取当天零点零分零秒
     *
     * @return
     * @author lengxuefei
     */
    public static Date getTodayStart() {
        return getTodayStart(new Date());
    }

    /**
     * 取当天23点59分59秒
     *
     * @return
     * @author lengxuefei
     */
    public static Date getTodayEnd(Date changeDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(changeDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
//        Date date = new Date(calendar.getTimeInMillis());
        Date date = calendar.getTime();
        return date;
    }

    /**
     * @return
     * @Description：取当天23点59分59秒
     * @author：Wangzhixuan
     */
    public static Date getTodayEnd() {
        return getTodayEnd(new Date());
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒  (较大时间-较小时间)
     *
     * @param maxTime 较大时间参数
     * @param minTime 较小时间参数
     * @return String 返回值为：xx天xx时xx分xx秒
     */
    public static String getDistanceTime(Date maxTime, Date minTime) {
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            long time1 = maxTime.getTime();
            long time2 = minTime.getTime();
            long diff = time1 - time2;
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (Exception e) {
            logger.error("格式化日期异常", e.getMessage());
        }
        if (day == 0 && hour == 0 && min == 0) {
            return sec + "秒";
        } else if (day == 0 && hour == 0) {
            return min + "分" + sec + "秒";
        } else if (day == 0) {
            return hour + "时" + min + "分" + sec + "秒";
        } else {
            return day + "天" + hour + "时" + min + "分" + sec + "秒";
        }
    }

    /**
     * @return
     * @Description：当前日期是否为偶数
     * @author：Wangzhixuan
     */
    public static boolean getNowDateIsEven() {
        DateTime dt = new DateTime();
        int day = dt.getDayOfMonth();
        return day % 2 == 0;
    }


    /**
     * @return
     * @Description：昨天的结束时间
     * @author：Wangzhixuan
     */
    public static Date yesterdayEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * @return
     * @Description：昨天的开始时间
     * @author：Wangzhixuan
     */
    public static Date yesterdayStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * @return
     * @Description：根据String类型的时间获取格式化后的时间
     * @author：lengxuefei
     */
    public static String getFormatDateByStringDate(String stringDate) {
        try {
            if (stringDate == null || "".equals(stringDate.trim())) {
                return "";
            } else {
                if (stringDate.contains("-")) {
                    return stringDate;
                } else {
                    Long stringDateTime = Long.parseLong(stringDate);
                    return new DateTime(stringDateTime).toString("yyyy-MM-dd");
                }
            }
        } catch (Exception e) {
            logger.error("根据String类型的时间获取格式化后的时间异常，{}", e);
            return "";
        }
    }

    /**
     * 比较2个日期大小
     *
     * @param date1
     * @param date2
     * @return 1第一个日期大 2第二个日期大  0相等
     * int
     * @author lichangjiang
     * @date 2015年11月2日 下午4:39:26
     */
    public static int compareDate(String date1, String date2) {
        DateFormat df = new SimpleDateFormat(FORMAT_DATE);
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return 2;
            } else {
                return 0;
            }
        } catch (Exception e) {
            logger.error("比较两个日期异常，{}", e);
        }
        return 0;
    }

    /**
     * 获取日期是几号
     *
     * @param date
     * @return int
     * @author lichangjiang
     * @date 2015年11月30日 下午9:22:43
     */
    public static int getDayByMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    /**
     * 判断一个日期是周几
     *
     * @param time 时间
     * @return
     * @throws Throwable int
     * @author lichangjiang
     * @date 2016年1月27日 下午4:41:29
     */
    public static int dayForWeek(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }

    /**
     * @return
     * @Description：时间戳生成流水号
     * @author：jiaxiaozhen
     */
    public static String convertTimeToString() {
        return new DateTime().toString("yyyyMMddHHmmss");
    }

    /**
     * 计算两个日期相差天数，（自然天数）
     *
     * @param startDay
     * @param endDay
     * @return 出现异常返回-1
     */
    public static long getDateMarginForDay(Date startDay, Date endDay) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startTimeStr = sdf.format(startDay);
        String endTimeStr = sdf.format(endDay);
        Date startDayAfter = null;
        Date endDayAfter = null;
        try {
            startDayAfter = sdf.parse(startTimeStr);
            endDayAfter = sdf.parse(endTimeStr);
        } catch (ParseException e) {
            logger.error("时间转换异常：{}", e);
            e.printStackTrace();
            return -1;
        }
        long margin = (endDayAfter.getTime() - startDayAfter.getTime()) / (1000 * 60 * 60 * 24);
        return margin;
    }

    /**
     * 获取上个月的最后一天
     *
     * @return
     */
    public static String getPreMonthLastDay() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int MaxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), MaxDay, 23, 59, 59);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
//		  System.out.println(sdf.format(cal.getTime()));
        return sdf.format(cal.getTime());
    }

    /**
     * 获取上个月的第一天
     *
     * @return
     */
    public static String getPreMonthFirstDay() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), firstDay, 00, 00, 00);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
//		  System.out.println(sdf.format(cal.getTime()));
        return sdf.format(cal.getTime());
    }

    /**
     * 返回当月最后一天的日期
     */
    public static String getLastDayOfMonth(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        // 获取Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), lastDay, 00, 00, 00);
        return format.format(calendar.getTime());
    }

    /**
     * 判断当前时间是否处在周年庆期间资产新增活动某一个阶段
     * 5.7--5.12 第一阶段 返回标志1
     * 5.13--5.19 第二阶段 返回标志2
     * 5.20--5.25 第三阶段 返回标志3
     * 其他时间返回0
     *
     * @return
     */
    public static int getZNQDateFlag() {
        int flag = 0;
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        if (month == 4) {// 月份 (从0开始, 实际要加一)
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (day >= 7 && day <= 12) {
                flag = 1;
            } else if (day >= 13 && day <= 19) {
                flag = 2;
            } else if (day >= 20 && day <= 25) {
                flag = 3;
            }
        }
        return flag;
    }

    /**
     * 判断当前时间是否处在周年庆期
     * 5.6  返回标志1
     * 5.7--5.25  返回标志2
     * 其他时间返回0
     *
     * @return
     */
    public static int isFirstZNQFirstDay() {
        int flag = 0;
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        if (month == 4) {// 月份 (从0开始, 实际要加一)
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (day == 6) {
                flag = 1;
            } else if (day >= 7 && day <= 25) {
                flag = 2;
            }
        }
        return flag;
    }

    /**
     * 判断周年庆期间用户进阶vip sivp时间
     * 5.7  返回标志1
     * 5.8--5.26  返回标志2
     * 其他时间返回0
     *
     * @return
     */
    public static int sendZNQVIPRedbagDate() {
        int flag = 0;
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        if (month == 4) {// 月份 (从0开始, 实际要加一)
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (day == 7) {
                flag = 1;
            } else if (day >= 8 && day <= 26) {
                flag = 2;
            }
        }
        return flag;
    }

    public static boolean isSameDay(Date firstDay, Date secondDay) {
        String firstDayStr = DateUtils.formateDateByType(firstDay, "yyyy-MM-dd");
        String secondDayStr = DateUtils.formateDateByType(secondDay, "yyyy-MM-dd");
        return firstDayStr.equals(secondDayStr);
    }

    public static void main(String[] args) throws Exception {
        Date now = new Date();
        Date stopDate = DateUtils.getDateByStr("2016-09-01 00:00:00", DateUtils.FORMAT_DATE);

        System.out.println(now.after(stopDate));
    }


}
