package com.collective.celos;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

public class Util {

    public static <T> T requireNonNull(T object) {
        if (object == null) throw new NullPointerException();
        else return object;
    }

    static DateTime toFullHour(DateTime dt) {
        return dt.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0);
    }

    static boolean isFullHour(DateTime dt) {
        return dt.getMillisOfSecond() == 0
            && dt.getSecondOfMinute() == 0
            && dt.getMinuteOfHour() == 0;
    }
    
}
