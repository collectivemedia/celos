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

public class MinutelyScheduleTest {

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
        // Start time is full minute
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

    private MinutelySchedule makeMinutelySchedule() {
        return new MinutelySchedule();
    }
    
}
