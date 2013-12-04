package com.collective.celos;

import org.apache.log4j.Logger;

/**
 * The SlotStateLoger helps us log SlotState information and exceptions in a
 * consistent way throughout the application.
 */
public class SlotStateLogger {
    private final Logger logger = Logger.getLogger(getClass());

    public void logMessage(WorkflowID workflowId, String msg) {
        logger.info(formatString(workflowId, msg));
    }

    public void logMessage(SlotID slotId, String msg) {
        logger.info(formatString(slotId, msg));
    }

    public void logException(WorkflowID workflowId, Throwable t) {
        logger.error(formatString(workflowId, t.toString()));
        logThrowable(workflowId, t);
    }

    public void logException(SlotID slotId, Throwable t) {
        logger.error(formatString(slotId, t.toString()));
        logThrowable(slotId, t);
    }

    String formatString(Object obj, String msg) {
        return format(obj, msg, " ");
    }

    String format(Object context, Object msg, String separator) {
        String contextString = context == null ? "none" : context.toString();
        String msgString = msg == null ? "none" : msg.toString();
        return String.format("[%s]%s%s", contextString, separator, msgString);
    }

    void logThrowable(Object obj, Throwable t) {
        for (StackTraceElement ste : t.getStackTrace()) {
            logger.error(format(obj, ste, "\t"));
        }
    }

}