package com.collective.celos;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

public class HourlyScheduleTest {

    @Test
    public void hourlyScheduleEmpty() {
        Schedule sch = new HourlySchedule();
        ScheduledTime t = new ScheduledTime("2013-11-25T20:00Z");
        Set<ScheduledTime> hours = sch.getScheduledTimes(t, t);
        Assert.assertEquals(0, hours.size());
    }

    @Test
    public void hourlyScheduleOneHour() {
        Schedule sch = new HourlySchedule();
        ScheduledTime t1 = new ScheduledTime("2013-11-25T20:00Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T21:00Z");
        Set<ScheduledTime> hours = sch.getScheduledTimes(t1, t2);
        Assert.assertEquals(new TreeSet<ScheduledTime>(Arrays.asList(t1)), hours);
    }
    
    @Test
    public void hourlyScheduleOneHourWithMinutes() {
        Schedule sch = new HourlySchedule();
        ScheduledTime t1 = new ScheduledTime("2013-11-25T20:05Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T21:00Z");
        Set<ScheduledTime> hours = sch.getScheduledTimes(t1, t2);
        Assert.assertEquals(0, hours.size());
    }

    @Test
    public void hourlyScheduleSomeHours() {
        Schedule sch = new HourlySchedule();
        ScheduledTime t1 = new ScheduledTime("2013-11-25T20:05:12.00182Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T23:05:56.2182Z");
        Set<ScheduledTime> hours = sch.getScheduledTimes(t1, t2);
        List<ScheduledTime> expectedHours =
                Arrays.asList(new ScheduledTime("2013-11-25T21:00Z"),
                              new ScheduledTime("2013-11-25T22:00Z"),
                              new ScheduledTime("2013-11-25T23:00Z"));
        Assert.assertEquals(new TreeSet<ScheduledTime>(expectedHours), hours);
    }
    
    @Test
    public void hourlyScheduleSomeHoursWithStartingFullHour() {
        Schedule sch = new HourlySchedule();
        ScheduledTime t1 = new ScheduledTime("2013-11-25T20:00Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T23:05:56.2182Z");
        Set<ScheduledTime> hours = sch.getScheduledTimes(t1, t2);
        List<ScheduledTime> expectedHours =
                Arrays.asList(new ScheduledTime("2013-11-25T20:00Z"),
                              new ScheduledTime("2013-11-25T21:00Z"),
                              new ScheduledTime("2013-11-25T22:00Z"),
                              new ScheduledTime("2013-11-25T23:00Z"));
        Assert.assertEquals(new TreeSet<ScheduledTime>(expectedHours), hours);
    }
}
