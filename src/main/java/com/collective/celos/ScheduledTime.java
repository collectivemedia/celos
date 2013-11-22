package com.collective.celos;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Scheduled time of a workflow run in UTC.
 */
public class ScheduledTime extends ValueObject {

    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormat
            .forPattern("yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormat
            .forPattern("MM");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormat
            .forPattern("dd");
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormat
            .forPattern("HH");

    protected final DateTime dateTime;

    public ScheduledTime(String formattedDate) {
        this(DateTime.parse(formattedDate));
    }

    public ScheduledTime(DateTime dateTime) {
        this.dateTime = Util.requireNonNull(dateTime);
        if (!dateTime.getZone().equals(DateTimeZone.UTC)) {
            throw new IllegalArgumentException(
                    "Scheduled time must be in UTC, but isn't: " + dateTime);
        }
    }

    public String getYear() {
        return dateTime.toString(YEAR_FORMATTER);
    }

    public String getMonth() {
        return dateTime.toString(MONTH_FORMATTER);
    }

    public String getDay() {
        return dateTime.toString(DAY_FORMATTER);
    }

    public String getHour() {
        return dateTime.toString(HOUR_FORMATTER);
    }

}
