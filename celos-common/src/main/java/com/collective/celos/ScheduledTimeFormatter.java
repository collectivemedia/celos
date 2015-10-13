package com.collective.celos;

import java.time.ZonedDateTime;

public class ScheduledTimeFormatter {

    public String replaceTimeTokens(String text0, ZonedDateTime t) {
        String text = Util.requireNonNull(text0);
        return text
            .replace("${year}", formatYear(t))
            .replace("${month}", formatMonth(t))
            .replace("${day}", formatDay(t))
            .replace("${hour}", formatHour(t))
            .replace("${minute}", formatMinute(t))
            .replace("${second}", formatSecond(t))
            .replace("${millisecond}", formatMillisecond(t));
    }

    public String formatTimestamp(ZonedDateTime t) {
        return formatHour(t) + ":" + formatMinute(t) + ":" + formatSecond(t) + "." + formatMillisecond(t) + "Z";
    }

    protected String formatMillisecond(ZonedDateTime t) {
        return String.format("%03d", t.getNano() / 1000 / 1000);
    }

    protected String formatSecond(ZonedDateTime t) {
        return String.format("%02d", t.getSecond());
    }

    protected String formatMinute(ZonedDateTime t) {
        return String.format("%02d", t.getMinute());
    }

    protected String formatHour(ZonedDateTime t) {
        return String.format("%02d", t.getHour());
    }

    protected String formatDay(ZonedDateTime t) {
        return String.format("%02d", t.getDayOfMonth());
    }

    protected String formatMonth(ZonedDateTime t) {
        return String.format("%02d", t.getMonthValue());
    }

    protected String formatYear(ZonedDateTime t) {
        return String.format("%04d", t.getYear());
    }

    protected String formatPretty(ZonedDateTime t) {
        return t.toString();
    }

}
