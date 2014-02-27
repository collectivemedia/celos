package com.collective.celos;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

public class CronScheduleTest {

    @Test
    public void cronScheduleEmpty() {
        Schedule sch = makeCronSchedule("0 0 12 * * ?");
        ScheduledTime t = new ScheduledTime("2013-11-25T20:00Z");
        Set<ScheduledTime> times = sch.getScheduledTimes(t, t);
        Assert.assertEquals(0, times.size());
    }

    @Test
    public void cronScheduleOneHourEmpty() {
        Schedule sch = makeCronSchedule("0 0 12 * * ?");
        ScheduledTime t1 = new ScheduledTime("2013-11-25T20:00Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T21:00Z");
        Set<ScheduledTime> times = sch.getScheduledTimes(t1, t2);
        Assert.assertEquals(0, times.size());
    }

    @Test
    public void cronScheduleOneHourBorderWrongBorderVals() {
        Schedule sch = makeCronSchedule("0 0 12 * * ?");
        ScheduledTime t1 = new ScheduledTime("2013-11-25T14:00Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T11:00Z");
        Set<ScheduledTime> times = sch.getScheduledTimes(t1, t2);

        Assert.assertEquals(0, times.size());
    }


    @Test
    public void cronScheduleOneHourBorderIncluded() {
        Schedule sch = makeCronSchedule("0 0 12 * * ?");
        ScheduledTime t1 = new ScheduledTime("2013-11-25T12:00Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T13:00Z");
        Set<ScheduledTime> times = sch.getScheduledTimes(t1, t2);

        List<ScheduledTime> expectedMinutes = Arrays.asList(new ScheduledTime("2013-11-25T12:00Z"));
        Assert.assertEquals(new TreeSet<ScheduledTime>(expectedMinutes), times);
    }

    @Test
    public void cronScheduleComplexStuff() {
        //Fire every 5 minutes starting at 2:00 PM and ending at 2:55 PM, every day
        Schedule sch = makeCronSchedule("0 0/15 14 * * ?");
        ScheduledTime t1 = new ScheduledTime("2013-11-25T12:00Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T18:00Z");
        Set<ScheduledTime> times = sch.getScheduledTimes(t1, t2);

        List<ScheduledTime> expectedMinutes = Arrays.asList(
                new ScheduledTime("2013-11-25T14:00Z"),
                new ScheduledTime("2013-11-25T14:15Z"),
                new ScheduledTime("2013-11-25T14:30Z"),
                new ScheduledTime("2013-11-25T14:45Z")
        );
        Assert.assertEquals(new TreeSet<ScheduledTime>(expectedMinutes), times);
    }


    @Test
    public void cronScheduleOneHourBorderExcludedEmpty() {
        Schedule sch = makeCronSchedule("0 0 12 * * ?");
        ScheduledTime t1 = new ScheduledTime("2013-11-25T11:00Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T12:00Z");
        Set<ScheduledTime> hours = sch.getScheduledTimes(t1, t2);
        Assert.assertEquals(0, hours.size());
    }

    @Test
    public void cronScheduleInvalidConfig() {

        boolean failed = false;
        try {
            makeCronSchedule("0 0 12 * * blah");
        } catch (Exception exc) {
            failed = true;
        }
        Assert.assertTrue(failed);
    }


    @Test
    public void cronScheduleOneHour() {
        Schedule sch = makeCronSchedule("0 0 12 * * ?");
        ScheduledTime t1 = new ScheduledTime("2013-11-25T11:30Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T12:30Z");
        Set<ScheduledTime> hours = sch.getScheduledTimes(t1, t2);
        Assert.assertEquals(1, hours.size());
    }

    private CronSchedule makeCronSchedule(String config) {
        return new CronSchedule(Util.newObjectNode().put("cron_config", config));
    }
    
    @Test
    public void hourlyScheduleEmpty() {
        Schedule sch = makeHourlySchedule();
        ScheduledTime t = new ScheduledTime("2013-11-25T20:00Z");
        Set<ScheduledTime> hours = sch.getScheduledTimes(t, t);
        Assert.assertEquals(0, hours.size());
    }

    @Test
    public void hourlyScheduleOneHour() {
        Schedule sch = makeHourlySchedule();
        ScheduledTime t1 = new ScheduledTime("2013-11-25T20:00Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T21:00Z");
        Set<ScheduledTime> hours = sch.getScheduledTimes(t1, t2);
        Assert.assertEquals(new TreeSet<ScheduledTime>(Arrays.asList(t1)), hours);
    }
    
    @Test
    public void hourlyScheduleOneHourWithMinutes() {
        Schedule sch = makeHourlySchedule();
        ScheduledTime t1 = new ScheduledTime("2013-11-25T20:05Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T21:00Z");
        Set<ScheduledTime> hours = sch.getScheduledTimes(t1, t2);
        Assert.assertEquals(0, hours.size());
    }

    @Test
    public void hourlyScheduleSomeHours() {
        Schedule sch = makeHourlySchedule();
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
        Schedule sch = makeHourlySchedule();
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

    private Schedule makeHourlySchedule() {
        return makeCronSchedule("0 0 * * * ?");
    }
    
    @Test
    public void minutelyScheduleEmpty() {
        // Start time equals end time
        // -> empty schedule
        Schedule sch = makeMinutelySchedule();
        ScheduledTime t = new ScheduledTime("2013-11-25T20:00Z");
        Set<ScheduledTime> minutes = sch.getScheduledTimes(t, t);
        Assert.assertEquals(0, minutes.size());
    }

    @Test
    public void minutelyScheduleOneMinute() {
        // Start time is full minute, end time one minute later
        // -> start time is the single minute in schedule
        Schedule sch = makeMinutelySchedule();
        ScheduledTime t1 = new ScheduledTime("2013-11-25T20:00Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T20:01Z");
        Set<ScheduledTime> minutes = sch.getScheduledTimes(t1, t2);
        Assert.assertEquals(new TreeSet<ScheduledTime>(Arrays.asList(t1)), minutes);
    }
    
    @Test
    public void minutelyScheduleOneMinuteWithSeconds() {
        // Start time is non-full minute with some seconds,
        // end time one minute later
        // -> empty schedule
        Schedule sch = makeMinutelySchedule();
        ScheduledTime t1 = new ScheduledTime("2013-11-25T20:01:05Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T20:02:00Z");
        Set<ScheduledTime> minutes = sch.getScheduledTimes(t1, t2);
        Assert.assertEquals(0, minutes.size());
    }

    @Test
    public void minutelyScheduleSomeMinutes() {
        // Start time is non-full minute
        // End time is a couple of minutes later
        // -> schedule contains all full minutes after start and before end
        Schedule sch = makeMinutelySchedule();
        ScheduledTime t1 = new ScheduledTime("2013-11-25T20:05:12.00182Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T20:08:56.2182Z");
        Set<ScheduledTime> minutes = sch.getScheduledTimes(t1, t2);
        List<ScheduledTime> expectedMinutes =
                Arrays.asList(new ScheduledTime("2013-11-25T20:06Z"),
                              new ScheduledTime("2013-11-25T20:07Z"),
                              new ScheduledTime("2013-11-25T20:08Z"));
        Assert.assertEquals(new TreeSet<ScheduledTime>(expectedMinutes), minutes);
    }
    
    @Test
    public void minutelyScheduleSomeMinutesWithStartingFullMinute() {
        // Start time is full minuteconfig
        // End time is a couple of minutes later
        // -> schedule contains all full minutes after (and including) start and before end
        Schedule sch = makeMinutelySchedule();
        ScheduledTime t1 = new ScheduledTime("2013-11-25T20:00Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T20:03:56.2182Z");
        Set<ScheduledTime> minutes = sch.getScheduledTimes(t1, t2);
        List<ScheduledTime> expectedMinutes =
                Arrays.asList(new ScheduledTime("2013-11-25T20:00Z"),
                              new ScheduledTime("2013-11-25T20:01Z"),
                              new ScheduledTime("2013-11-25T20:02Z"),
                              new ScheduledTime("2013-11-25T20:03Z"));
        Assert.assertEquals(new TreeSet<ScheduledTime>(expectedMinutes), minutes);
    }

    private Schedule makeMinutelySchedule() {
        return makeCronSchedule("0 * * * * ?");
    }

}
