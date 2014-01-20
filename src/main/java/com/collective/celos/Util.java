package com.collective.celos;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;

public class Util {

    private static final Logger LOGGER = Logger.getLogger("celos");
    
    public static <T> T requireNonNull(T object) {
        if (object == null) throw new NullPointerException();
        else return object;
    }

    public static void logInfo(Object msg) {
        LOGGER.log(Level.INFO, msg.toString());
    }
    
    public static void logException(Exception e) {
        LOGGER.log(Level.SEVERE, "", e); // TODO: add message
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
