package com.softb.savefy.utils;

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
}
