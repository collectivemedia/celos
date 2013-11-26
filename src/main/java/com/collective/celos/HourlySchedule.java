package com.collective.celos;

import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.DateTime;

public class HourlySchedule implements Schedule {

    @Override
    public SortedSet<ScheduledTime> getScheduledTimes(ScheduledTime start, ScheduledTime end) {
        DateTime startDT = start.getDateTime();
        DateTime endDT = end.getDateTime();
        DateTime hour;
        // If start time of window is full hour, use it as first hour
        // otherwise use next full hour as first one.
        if (isFullHour(startDT)) {
            hour = startDT;
        } else {
            hour = startDT.plusHours(1).withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0);
        }
        SortedSet<ScheduledTime> hours = new TreeSet<ScheduledTime>();
        while(hour.isBefore(endDT)) {
            hours.add(new ScheduledTime(hour));
            hour = hour.plusHours(1);
        }
        return hours;
    }

    private boolean isFullHour(DateTime t) {
        return t.getMillisOfSecond() == 0
            && t.getSecondOfMinute() == 0
            && t.getMinuteOfHour() == 0;
    }

}
