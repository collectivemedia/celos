package com.collective.celos;

import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class MinutelySchedule implements Schedule {

    public MinutelySchedule(ObjectNode ignored) {
    }
    
    @Override
    public SortedSet<ScheduledTime> getScheduledTimes(ScheduledTime start, ScheduledTime end) {
        DateTime startDT = start.getDateTime();
        DateTime endDT = end.getDateTime();
        DateTime minute;
        // If start time of window is full minute, use it as first minute
        // otherwise use next full minute as first one.
        if (Util.isFullMinute(startDT)) {
            minute = startDT;
        } else {
            minute = Util.toFullMinute(startDT.plusMinutes(1));
        }
        SortedSet<ScheduledTime> minutes = new TreeSet<ScheduledTime>();
        while(minute.isBefore(endDT)) {
            minutes.add(new ScheduledTime(minute));
            minute = minute.plusMinutes(1);
        }
        return minutes;
    }

}
