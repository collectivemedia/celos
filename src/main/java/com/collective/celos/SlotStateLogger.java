package com.collective.celos;

import org.apache.log4j.Logger;

/**
 * The SlotStateLoger helps us log SlotState information and exceptions in a
 * consistent way throughout the application.
 */
class SlotStateLogger {
    private final Logger logger;

    public SlotStateLogger(Logger logger) {
        this.logger = logger;
    }

    public void info(SlotState state, String string) {
        logger.info(decorate(state) + string);
    }

    public void logException(SlotState state, Exception e) {
        logger.error(decorate(state) + e);
        for (StackTraceElement ste : e.getStackTrace()) {
            logger.error(decorate(state) + "\t" + ste);
        }
    }

    String decorate(SlotState state) {
        String text = state == null ? "none" : state.getSlotID().toString();
        return "[" + text + "] ";
    }

}