package com.wwt.managemail.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeUtils {
    public static Date getCurrentYeadFirstDay() {
        Date now = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.set(Calendar.DAY_OF_YEAR, 1);
        return c.getTime();
    }

    /**
     * @param minDate 最小时间  2015-01
     * @param maxDate 最大时间 2015-10
     * @return 日期集合 格式为 年-月
     * @throws Exception
     */
    public static List<String> getMonthBetween(Date minDate, Date maxDate) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月
        ArrayList<String> result = new ArrayList<>();
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(minDate);
        max.setTime(maxDate);

        Calendar curr = min;
        while (curr.before(max) || curr.equals(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }
        return result;
    }


    /**
     * @param minDate 最小时间  2015-01
     * @param maxDate 最大时间 2015-10
     * @return 日期集合 格式为 年-月
     * @throws Exception
     */
    public static List<String> getMonthBetween(Date minDate, Date maxDate, SimpleDateFormat sdf) throws Exception {
        return getMonthBetween(minDate, maxDate, sdf, 1);
    }

    /**
     * @param minDate 最小时间  2015-01
     * @param maxDate 最大时间 2015-10
     *                间隔时间
     * @return 日期集合 格式为 年-月
     * @throws Exception
     */
    public static List<String> getMonthBetween(Date minDate, Date maxDate, SimpleDateFormat sdf, int between) throws Exception {
        ArrayList<String> result = new ArrayList<>();
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(minDate);
        max.setTime(maxDate);

        Calendar curr = min;
        curr.add(Calendar.MONTH, between);
        while (curr.before(max) || curr.equals(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, between);
        }

        return result;
    }

    /**
     * 获取当前月第一天
     *
     * @param month
     * @return
     */
    public static Date getFirstDayOfMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        // 设置月份
        calendar.set(Calendar.MONTH, month - 1);
        // 获取某月最小天数
        int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        // 设置日历中月份的最小天数
        calendar.set(Calendar.DAY_OF_MONTH, firstDay);
        // 格式化日期
        return calendar.getTime();
    }

    public static Date getLastDayOfMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        // 设置月份
        calendar.set(Calendar.MONTH, month - 1);
        // 获取某月最大天数
        int lastDay = 0;
        //2月的平年瑞年天数
        if (month == 2) {
            lastDay = calendar.getLeastMaximum(Calendar.DAY_OF_MONTH);
        } else {
            lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        // 设置日历中月份的最大天数
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        // 格式化日期
        return calendar.getTime();
    }

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        System.out.println(sdf.format(getFirstDayOfMonth(2)));
        System.out.println(sdf.format(getLastDayOfMonth(2)));
    }
}
