package com.collective.celos;

import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.DateTime;

public class HourlySchedule implements Schedule {

    public HourlySchedule() {
    }
    
    @Override
    public SortedSet<ScheduledTime> getScheduledTimes(ScheduledTime start, ScheduledTime end) {
        DateTime startDT = start.getDateTime();
        DateTime endDT = end.getDateTime();
        DateTime hour;
        // If start time of window is full hour, use it as first hour
        // otherwise use next full hour as first one.
        if (Util.isFullHour(startDT)) {
            hour = startDT;
        } else {
            hour = Util.toFullHour(startDT.plusHours(1));
        }
        SortedSet<ScheduledTime> hours = new TreeSet<ScheduledTime>();
        while(hour.isBefore(endDT)) {
            hours.add(new ScheduledTime(hour));
            hour = hour.plusHours(1);
        }
        return hours;
    }

}
