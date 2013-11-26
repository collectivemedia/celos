package com.collective.celos;

public class ScheduledTimeFormatter {

    public String replaceTimeTokens(String string, ScheduledTime t) {
        return string
            .replace("${year}", formatYear(t))
            .replace("${month}", formatMonth(t))
            .replace("${day}", formatDay(t))
            .replace("${hour}", formatHour(t));
    }

    public String formatHour(ScheduledTime t) {
        return String.format("%02d", t.getHour());
    }

    public String formatDay(ScheduledTime t) {
        return String.format("%02d", t.getDay());
    }

    public String formatMonth(ScheduledTime t) {
        return String.format("%02d", t.getMonth());
    }

    public String formatYear(ScheduledTime t) {
        return String.format("%04d", t.getYear());
    }
    
}
