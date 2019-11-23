package com.wwt.managemail.utils;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
    public static Date getCurrentYeadFirstDay() {
        Date now = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.set(Calendar.DAY_OF_YEAR, 1);
        return c.getTime();
    }
}
