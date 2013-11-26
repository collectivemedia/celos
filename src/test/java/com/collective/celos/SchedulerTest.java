package com.collective.celos;

import org.junit.Assert;
import org.junit.Test;

public class SchedulerTest {

    @Test(expected=IllegalArgumentException.class)
    public void slidingWindowHoursPositive1() {
        new Scheduler(0);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void slidingWindowHoursPositive2() {
        new Scheduler(-23);
    }

    @Test
    public void slidingWindowSizeWorks() {
        ScheduledTime t = new ScheduledTime("2013-11-26T20:00Z");
        int hours = 5;
        Assert.assertEquals(new Scheduler(hours).getStartTime(t), new ScheduledTime("2013-11-26T15:00Z"));
    }
    
}
