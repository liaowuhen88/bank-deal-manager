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
     * @param minDate 最小时间  2015-01
     * @param maxDate 最大时间 2015-10
     *                间隔时间
     * @return 日期集合 格式为 年-月
     * @throws Exception
     */
    public static List<String> getMonthBetween(Date minDate, Date maxDate, Date base, SimpleDateFormat sdf) throws Exception {
        return getMonthBetween(minDate, maxDate, base, sdf, 1);
    }


    /**
     * @param minDate 最小时间  2015-01
     * @param maxDate 最大时间 2015-10
     *                间隔时间
     * @return 日期集合 格式为 年-月
     * @throws Exception
     */
    public static List<String> getMonthBetween(Date minDate, Date maxDate, Date base, SimpleDateFormat sdf, int between) throws Exception {
        ArrayList<String> result = new ArrayList<>();
        Calendar min = Calendar.getInstance();
        Calendar middle = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        min.setTime(minDate);
        middle.setTime(base);
        max.setTime(maxDate);
        // 1.先计算开始到base时间
        Calendar curr = middle;
        // 当前时间往前倒推
        result.add(0, sdf.format(curr.getTime()));
        curr.add(Calendar.MONTH, -between);
        while (true) {
            //curr.before(max) || curr.equals(max)
            if (curr.after(min)) {
                result.add(0, sdf.format(curr.getTime()));
                curr.add(Calendar.MONTH, -between);
            } else {
                //result.add(0,sdf.format(min.getTime()));
                break;
            }
            //System.out.println(result);
        }
        //System.out.println(result);

        // 在计算base到结束时间
        middle.setTime(base);
        curr = middle;
        curr.add(Calendar.MONTH, between);
        while (true) {
            //curr.before(max) || curr.equals(max)
            if (curr.before(max)) {
                result.add(sdf.format(curr.getTime()));
                curr.add(Calendar.MONTH, between);
            } else {
                result.add(sdf.format(max.getTime()));
                break;
            }
        }
        //System.out.println(result);
        return result;
    }

    /**
     * 获取当前月第一天
     *
     * @param month
     * @return
     */
    public static Date getFirstDayOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        // 设置月份
        calendar.set(Calendar.MONTH, month - 1);
        // 获取某月最小天数
        int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        // 设置日历中月份的最小天数
        calendar.set(Calendar.DAY_OF_MONTH, firstDay);
        // 格式化日期
        return calendar.getTime();
    }

    public static Date getLastDayOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
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

    public static int daysBetween(Date date1, Date date2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date2);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    public static void main(String[] args) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        //System.out.println(sdf.format(getFirstDayOfMonth(2019, 2)));
        //System.out.println(sdf.format(getLastDayOfMonth(2019, 2)));
        // 买入时间
        Date start = sdf.parse("2020-01-10");
        // 收利日期
        Date base = sdf.parse("2020-03-30");
        //到期时间
        Date end = sdf.parse("2020-12-10");
        // 按月计息
        List<String> list = getMonthBetween(start, end, base, sdf, 1);
        System.out.println(list);
    }
}
