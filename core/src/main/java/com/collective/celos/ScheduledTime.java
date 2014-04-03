package com.collective.celos;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Scheduled time of a workflow run in UTC.
 */
public class ScheduledTime extends com.collective.celos.ValueObject implements Comparable<ScheduledTime> {

    public static final ScheduledTimeFormatter FORMATTER = new ScheduledTimeFormatter();
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

    public ScheduledTime minusYears(int i) {
        return new ScheduledTime(getDateTime().minusYears(i));
    }

    public ScheduledTime plusYears(int i) {
        return new ScheduledTime(getDateTime().plusYears(i));
    }

    public ScheduledTime minusMonths(int i) {
        return new ScheduledTime(getDateTime().minusMonths(i));
    }

    public ScheduledTime plusMonths(int i) {
        return new ScheduledTime(getDateTime().plusMonths(i));
    }

    public ScheduledTime minusDays(int i) {
        return new ScheduledTime(getDateTime().minusDays(i));
    }

    public ScheduledTime plusDays(int i) {
        return new ScheduledTime(getDateTime().plusDays(i));
    }

    public ScheduledTime minusHours(int i) {
        return new ScheduledTime(getDateTime().minusHours(i));
    }

    public ScheduledTime plusHours(int i) {
        return new ScheduledTime(getDateTime().plusHours(i));
    }

    public ScheduledTime minusMinutes(int i) {
        return new ScheduledTime(getDateTime().minusMinutes(i));
    }

    public ScheduledTime plusMinutes(int i) {
        return new ScheduledTime(getDateTime().plusMinutes(i));
    }


    public ScheduledTime minusSeconds(int i) {
        return new ScheduledTime(getDateTime().minusSeconds(i));
    }

    public ScheduledTime plusSeconds(int i) {
        return new ScheduledTime(getDateTime().plusSeconds(i));
    }

    public String year() {
        return FORMATTER.formatYear(this);
    }

    public String month() {
        return FORMATTER.formatMonth(this);
    }

    public String day() {
        return FORMATTER.formatDay(this);
    }

    public String hour() {
        return FORMATTER.formatHour(this);
    }

    public String minute() {
        return FORMATTER.formatMinute(this);
    }

    public String second() {
        return FORMATTER.formatSecond(this);
    }

    public static ScheduledTime now() {
        return new ScheduledTime(DateTime.now(DateTimeZone.UTC));
    }

}
