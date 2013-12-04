package com.collective.celos;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

public class Util {

    public static <T> T requireNonNull(T object) {
        if (object == null) throw new NullPointerException();
        else return object;
    }

    public static void logException(Exception e) {
        e.printStackTrace(System.err);
    }
    
    private static SlotStateLogger stateLogger = new SlotStateLogger(Logger.getLogger(Util.class));
    public static void logMessage(WorkflowID workflowId, String msg) {
        stateLogger.logMessage(workflowId, msg);
    }
    
    public static void logMessage(SlotID slotId, String msg) {
        stateLogger.logMessage(slotId, msg);
    }
    
    public static void logException(WorkflowID workflowId, Throwable t) {
        stateLogger.logException(workflowId, t);
    }
    
    public static void logException(SlotID slotId, Throwable t) {
        stateLogger.logException(slotId, t);
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
