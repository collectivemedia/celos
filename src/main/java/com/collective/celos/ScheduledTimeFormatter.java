package com.collective.celos;

public class ScheduledTimeFormatter {

    public String replaceTimeTokens(String string, ScheduledTime t) {
        return string
            .replace("${year}", formatYear(t))
            .replace("${month}", formatMonth(t))
            .replace("${day}", formatDay(t))
            .replace("${hour}", formatHour(t))
            .replace("${minute}", formatMinute(t))
            .replace("${second}", formatSecond(t))
            .replace("${millisecond}", formatMillisecond(t));
    }

    public String formatMillisecond(ScheduledTime t) {
        return String.format("%03d", t.getMillisecond());
    }

    public String formatSecond(ScheduledTime t) {
        return String.format("%02d", t.getSecond());
    }

    public String formatMinute(ScheduledTime t) {
        return String.format("%02d", t.getMinute());
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
