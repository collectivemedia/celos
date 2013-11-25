package com.collective.celos;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

public class HourlySchedule implements Schedule {

    @Override
    public Set<ScheduledTime> getScheduledTimes(ScheduledTime start, ScheduledTime end) {
        DateTime startDT = start.getDateTime();
        DateTime endDT = end.getDateTime();
        DateTime hour = startDT.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0);
        Set<ScheduledTime> hours = new HashSet<ScheduledTime>();
        while(hour.isBefore(endDT)) {
            // FIXME: ugly but couldn't find out a much better way
            // to handle the case when the start is a full hour
            // and should therefore be included
            if (hour.equals(startDT) || hour.isAfter(startDT)) {
                hours.add(new ScheduledTime(hour));
            }
            hour = hour.plusHours(1);
        }
        return hours;
    }

}
