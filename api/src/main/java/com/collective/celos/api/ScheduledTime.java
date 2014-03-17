package com.collective.celos.api;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Scheduled time of a workflow run in UTC.
 */
public class ScheduledTime extends ValueObject implements Comparable<ScheduledTime> {

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

    public DateTime getDateTime() {
        return dateTime;
    }

    @Override
    public int compareTo(ScheduledTime t) {
        return CompareToBuilder.reflectionCompare(this, t);
    }

    public int getYear() {
        return dateTime.getYear();
    }

    public int getMonth() {
        return dateTime.getMonthOfYear();
    }

    public int getDay() {
        return dateTime.getDayOfMonth();
    }

    public int getHour() {
        return dateTime.getHourOfDay();
    }

    public int getMinute() {
        return dateTime.getMinuteOfHour();
    }

    public int getSecond() {
        return dateTime.getSecondOfMinute();
    }

    public int getMillisecond() {
        return dateTime.getMillisOfSecond();
    }

    public String toString() {
        return dateTime.toString();
    }

    public static ScheduledTime now() {
        return new ScheduledTime(DateTime.now(DateTimeZone.UTC));
    }
    
}
