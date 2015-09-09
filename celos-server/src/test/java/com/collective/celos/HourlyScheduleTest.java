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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

public class HourlyScheduleTest {

    @Test
    public void hourlyScheduleEmpty() {
        Schedule sch = makeHourlySchedule();
        ScheduledTime t = new ScheduledTime("2013-11-25T20:00Z");
        Set<ScheduledTime> hours = sch.getScheduledTimes(null, t, t);
        Assert.assertEquals(0, hours.size());
    }

    @Test
    public void hourlyScheduleOneHour() {
        Schedule sch = makeHourlySchedule();
        ScheduledTime t1 = new ScheduledTime("2013-11-25T20:00Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T21:00Z");
        Set<ScheduledTime> hours = sch.getScheduledTimes(null, t1, t2);
        Assert.assertEquals(new TreeSet<ScheduledTime>(Arrays.asList(t1)), hours);
    }
    
    @Test
    public void hourlyScheduleOneHourWithMinutes() {
        Schedule sch = makeHourlySchedule();
        ScheduledTime t1 = new ScheduledTime("2013-11-25T20:05Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T21:00Z");
        Set<ScheduledTime> hours = sch.getScheduledTimes(null, t1, t2);
        Assert.assertEquals(0, hours.size());
    }

    @Test
    public void hourlyScheduleSomeHours() {
        Schedule sch = makeHourlySchedule();
        ScheduledTime t1 = new ScheduledTime("2013-11-25T20:05:12.00182Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-25T23:05:56.2182Z");
        Set<ScheduledTime> hours = sch.getScheduledTimes(null, t1, t2);
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
        Set<ScheduledTime> hours = sch.getScheduledTimes(null, t1, t2);
        List<ScheduledTime> expectedHours =
                Arrays.asList(new ScheduledTime("2013-11-25T20:00Z"),
                              new ScheduledTime("2013-11-25T21:00Z"),
                              new ScheduledTime("2013-11-25T22:00Z"),
                              new ScheduledTime("2013-11-25T23:00Z"));
        Assert.assertEquals(new TreeSet<ScheduledTime>(expectedHours), hours);
    }

    private HourlySchedule makeHourlySchedule() {
        return new HourlySchedule();
    }
}
