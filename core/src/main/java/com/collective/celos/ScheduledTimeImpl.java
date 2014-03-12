package com.collective.celos;

import com.collective.celos.exposed.ScheduledTime;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Scheduled time of a workflow run in UTC.
 */
public class ScheduledTimeImpl extends ValueObject implements ScheduledTime {
    
    protected final DateTime dateTime;

    public ScheduledTimeImpl(String formattedDate) {
        this(DateTime.parse(formattedDate));
    }

    public ScheduledTimeImpl(DateTime dateTime) {
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

}
