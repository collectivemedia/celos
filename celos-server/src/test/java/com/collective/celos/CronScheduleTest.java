/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos;

import org.junit.Assert;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CronScheduleTest {

    @Test
    public void cronScheduleEmpty() {
        Schedule sch = makeCronSchedule("0 0 12 * * ?");
        ZonedDateTime t = ZonedDateTime.parse("2013-11-25T20:00Z");
        Set<ZonedDateTime> times = sch.getScheduledTimes(null, t, t);
        Assert.assertEquals(0, times.size());
    }

    @Test
    public void cronScheduleOneHourEmpty() {
        Schedule sch = makeCronSchedule("0 0 12 * * ?");
        ZonedDateTime t1 = ZonedDateTime.parse("2013-11-25T20:00Z");
        ZonedDateTime t2 = ZonedDateTime.parse("2013-11-25T21:00Z");
        Set<ZonedDateTime> times = sch.getScheduledTimes(null, t1, t2);
        Assert.assertEquals(0, times.size());
    }

    @Test
    public void cronScheduleOneHourBorderWrongBorderVals() {
        Schedule sch = makeCronSchedule("0 0 12 * * ?");
        ZonedDateTime t1 = ZonedDateTime.parse("2013-11-25T14:00Z");
        ZonedDateTime t2 = ZonedDateTime.parse("2013-11-25T11:00Z");
        Set<ZonedDateTime> times = sch.getScheduledTimes(null, t1, t2);

        Assert.assertEquals(0, times.size());
    }


    @Test
    public void cronScheduleOneHourBorderIncluded() {
        Schedule sch = makeCronSchedule("0 0 12 * * ?");
        ZonedDateTime t1 = ZonedDateTime.parse("2013-11-25T12:00Z");
        ZonedDateTime t2 = ZonedDateTime.parse("2013-11-25T13:00Z");
        Set<ZonedDateTime> times = sch.getScheduledTimes(null, t1, t2);

        List<ZonedDateTime> expectedMinutes = Arrays.asList(ZonedDateTime.parse("2013-11-25T12:00Z"));
        Assert.assertEquals(new TreeSet<ZonedDateTime>(expectedMinutes), times);
    }

    @Test
    public void cronScheduleComplexStuff() {
        //Fire every 5 minutes starting at 2:00 PM and ending at 2:55 PM, every day
        Schedule sch = makeCronSchedule("0 0/15 14 * * ?");
        ZonedDateTime t1 = ZonedDateTime.parse("2013-11-25T12:00Z");
        ZonedDateTime t2 = ZonedDateTime.parse("2013-11-25T18:00Z");
        Set<ZonedDateTime> times = sch.getScheduledTimes(null, t1, t2);

        List<ZonedDateTime> expectedMinutes = Arrays.asList(
                ZonedDateTime.parse("2013-11-25T14:00Z"),
                ZonedDateTime.parse("2013-11-25T14:15Z"),
                ZonedDateTime.parse("2013-11-25T14:30Z"),
                ZonedDateTime.parse("2013-11-25T14:45Z")
        );
        Assert.assertEquals(new TreeSet<ZonedDateTime>(expectedMinutes), times);
    }


    @Test
    public void cronScheduleOneHourBorderExcludedEmpty() {
        Schedule sch = makeCronSchedule("0 0 12 * * ?");
        ZonedDateTime t1 = ZonedDateTime.parse("2013-11-25T11:00Z");
        ZonedDateTime t2 = ZonedDateTime.parse("2013-11-25T12:00Z");
        Set<ZonedDateTime> hours = sch.getScheduledTimes(null, t1, t2);
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
        ZonedDateTime t1 = ZonedDateTime.parse("2013-11-25T11:30Z");
        ZonedDateTime t2 = ZonedDateTime.parse("2013-11-25T12:30Z");
        Set<ZonedDateTime> hours = sch.getScheduledTimes(null, t1, t2);
        Assert.assertEquals(1, hours.size());
    }

    private CronSchedule makeCronSchedule(String config) {
        return new CronSchedule(config);
    }
    
    @Test
    public void hourlyScheduleEmpty() {
        Schedule sch = makeHourlySchedule();
        ZonedDateTime t = ZonedDateTime.parse("2013-11-25T20:00Z");
        Set<ZonedDateTime> hours = sch.getScheduledTimes(null, t, t);
        Assert.assertEquals(0, hours.size());
    }

    @Test
    public void hourlyScheduleOneHour() {
        Schedule sch = makeHourlySchedule();
        ZonedDateTime t1 = ZonedDateTime.parse("2013-11-25T20:00Z");
        ZonedDateTime t2 = ZonedDateTime.parse("2013-11-25T21:00Z");
        Set<ZonedDateTime> hours = sch.getScheduledTimes(null, t1, t2);
        Assert.assertEquals(new TreeSet<ZonedDateTime>(Arrays.asList(t1)), hours);
    }
    
    @Test
    public void hourlyScheduleOneHourWithMinutes() {
        Schedule sch = makeHourlySchedule();
        ZonedDateTime t1 = ZonedDateTime.parse("2013-11-25T20:05Z");
        ZonedDateTime t2 = ZonedDateTime.parse("2013-11-25T21:00Z");
        Set<ZonedDateTime> hours = sch.getScheduledTimes(null, t1, t2);
        Assert.assertEquals(0, hours.size());
    }

    @Test
    public void hourlyScheduleSomeHours() {
        Schedule sch = makeHourlySchedule();
        ZonedDateTime t1 = ZonedDateTime.parse("2013-11-25T20:05:12.00182Z");
        ZonedDateTime t2 = ZonedDateTime.parse("2013-11-25T23:05:56.2182Z");
        Set<ZonedDateTime> hours = sch.getScheduledTimes(null, t1, t2);
        List<ZonedDateTime> expectedHours =
                Arrays.asList(ZonedDateTime.parse("2013-11-25T21:00Z"),
                              ZonedDateTime.parse("2013-11-25T22:00Z"),
                              ZonedDateTime.parse("2013-11-25T23:00Z"));
        Assert.assertEquals(new TreeSet<ZonedDateTime>(expectedHours), hours);
    }
    
    @Test
    public void hourlyScheduleSomeHoursWithStartingFullHour() {
        Schedule sch = makeHourlySchedule();
        ZonedDateTime t1 = ZonedDateTime.parse("2013-11-25T20:00Z");
        ZonedDateTime t2 = ZonedDateTime.parse("2013-11-25T23:05:56.2182Z");
        Set<ZonedDateTime> hours = sch.getScheduledTimes(null, t1, t2);
        List<ZonedDateTime> expectedHours =
                Arrays.asList(ZonedDateTime.parse("2013-11-25T20:00Z"),
                              ZonedDateTime.parse("2013-11-25T21:00Z"),
                              ZonedDateTime.parse("2013-11-25T22:00Z"),
                              ZonedDateTime.parse("2013-11-25T23:00Z"));
        Assert.assertEquals(new TreeSet<ZonedDateTime>(expectedHours), hours);
    }

    private Schedule makeHourlySchedule() {
        return makeCronSchedule("0 0 * * * ?");
    }
    
    @Test
    public void minutelyScheduleEmpty() {
        // Start time equals end time
        // -> empty schedule
        Schedule sch = makeMinutelySchedule();
        ZonedDateTime t = ZonedDateTime.parse("2013-11-25T20:00Z");
        Set<ZonedDateTime> minutes = sch.getScheduledTimes(null, t, t);
        Assert.assertEquals(0, minutes.size());
    }

    @Test
    public void minutelyScheduleOneMinute() {
        // Start time is full minute, end time one minute later
        // -> start time is the single minute in schedule
        Schedule sch = makeMinutelySchedule();
        ZonedDateTime t1 = ZonedDateTime.parse("2013-11-25T20:00Z");
        ZonedDateTime t2 = ZonedDateTime.parse("2013-11-25T20:01Z");
        Set<ZonedDateTime> minutes = sch.getScheduledTimes(null, t1, t2);
        Assert.assertEquals(new TreeSet<ZonedDateTime>(Arrays.asList(t1)), minutes);
    }
    
    @Test
    public void minutelyScheduleOneMinuteWithSeconds() {
        // Start time is non-full minute with some seconds,
        // end time one minute later
        // -> empty schedule
        Schedule sch = makeMinutelySchedule();
        ZonedDateTime t1 = ZonedDateTime.parse("2013-11-25T20:01:05Z");
        ZonedDateTime t2 = ZonedDateTime.parse("2013-11-25T20:02:00Z");
        Set<ZonedDateTime> minutes = sch.getScheduledTimes(null, t1, t2);
        Assert.assertEquals(0, minutes.size());
    }

    @Test
    public void minutelyScheduleSomeMinutes() {
        // Start time is non-full minute
        // End time is a couple of minutes later
        // -> schedule contains all full minutes after start and before end
        Schedule sch = makeMinutelySchedule();
        ZonedDateTime t1 = ZonedDateTime.parse("2013-11-25T20:05:12.00182Z");
        ZonedDateTime t2 = ZonedDateTime.parse("2013-11-25T20:08:56.2182Z");
        Set<ZonedDateTime> minutes = sch.getScheduledTimes(null, t1, t2);
        List<ZonedDateTime> expectedMinutes =
                Arrays.asList(ZonedDateTime.parse("2013-11-25T20:06Z"),
                              ZonedDateTime.parse("2013-11-25T20:07Z"),
                              ZonedDateTime.parse("2013-11-25T20:08Z"));
        Assert.assertEquals(new TreeSet<ZonedDateTime>(expectedMinutes), minutes);
    }
    
    @Test
    public void minutelyScheduleSomeMinutesWithStartingFullMinute() {
        // Start time is full minuteconfig
        // End time is a couple of minutes later
        // -> schedule contains all full minutes after (and including) start and before end
        Schedule sch = makeMinutelySchedule();
        ZonedDateTime t1 = ZonedDateTime.parse("2013-11-25T20:00Z");
        ZonedDateTime t2 = ZonedDateTime.parse("2013-11-25T20:03:56.2182Z");
        Set<ZonedDateTime> minutes = sch.getScheduledTimes(null, t1, t2);
        List<ZonedDateTime> expectedMinutes =
                Arrays.asList(ZonedDateTime.parse("2013-11-25T20:00Z"),
                              ZonedDateTime.parse("2013-11-25T20:01Z"),
                              ZonedDateTime.parse("2013-11-25T20:02Z"),
                              ZonedDateTime.parse("2013-11-25T20:03Z"));
        Assert.assertEquals(new TreeSet<ZonedDateTime>(expectedMinutes), minutes);
    }

    private Schedule makeMinutelySchedule() {
        return makeCronSchedule("0 * * * * ?");
    }

}
