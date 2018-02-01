package com.softb.savefy.utils;

import org.apache.commons.lang.time.DateUtils;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by eriklacerda on 3/31/16.
 */
public final class AppDate {
    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static LocalDate getLocalDateFor(Date date){
        return date.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
    }

    public static Date getMonthDate(Date date){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 01);
        return DateUtils.truncate(Date.from(getLocalDateFor(cal.getTime()).atStartOfDay(ZoneOffset.UTC).toInstant()), java.util.Calendar.DAY_OF_MONTH);
    }

    public static Date today(){
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        return DateUtils.truncate(Date.from(today.atStartOfDay(ZoneOffset.UTC).toInstant()), java.util.Calendar.DAY_OF_MONTH);
    }

    public static Date month(){
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        return getMonthDate(DateUtils.truncate(today, java.util.Calendar.DAY_OF_MONTH));
    }

    public static Date addBusinessDays(Date date, Integer days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        while (days > 0){
            cal.add(Calendar.DAY_OF_MONTH, 1);

            if ( !(cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7 )){
                days--;
            }
        }

        return cal.getTime();
    }
}
