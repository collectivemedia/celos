package com.collective.celos.exposed;

import org.joda.time.DateTime;

/**
 * Scheduled time of a workflow run in UTC.
 */
public interface ScheduledTime extends Comparable<ScheduledTime> {

    public DateTime getDateTime();

    public int getYear();

    public int getMonth();

    public int getDay();

    public int getHour();

    public int getMinute();

    public int getSecond();

    public int getMillisecond();

    public String toString();

}
