package com.softb.savefy.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
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
        return date.toInstant().atZone(ZoneId.of("America/Sao_Paulo")).toLocalDate();
    }

    public static Date getMonthDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 01);
        return Date.from(getLocalDateFor(cal.getTime()).atStartOfDay(ZoneId.of("America/Sao_Paulo")).toInstant());
    }

    public static Date addBussinessDays(Date date, Integer days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        while (days > 0){
            cal.add(Calendar.DAY_OF_MONTH, 1);

            if ( !(cal.get(Calendar.DAY_OF_WEEK) == 0 || cal.get(Calendar.DAY_OF_WEEK) == 0 )){
                days--;
            }
        }

        return cal.getTime();
    }
}
