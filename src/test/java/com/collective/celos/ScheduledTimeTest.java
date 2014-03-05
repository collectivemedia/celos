package com.collective.celos;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

public class ScheduledTimeTest {
    
    @Test
    public void scheduledTimeCompareToWorks() {
        ScheduledTime t1 = new ScheduledTime("2013-11-26T13:00Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-26T13:01Z");
        Assert.assertEquals(-1, t1.compareTo(t2));
        Assert.assertEquals(1, t2.compareTo(t1));
        Assert.assertEquals(0, t1.compareTo(t1));
        Assert.assertEquals(0, t2.compareTo(t2));
    }

    @Test
    public void testMax() {
        ScheduledTime t1 = new ScheduledTime("2013-11-26T13:00Z");
        ScheduledTime t2 = new ScheduledTime("2013-11-26T13:01Z");
        Assert.assertEquals(t2, ScheduledTime.max(t1, t2));
        Assert.assertEquals(t2, ScheduledTime.max(t2, t1));
    }
    
    @Test
    public void testGetYear() {
        ScheduledTime t = new ScheduledTime("2013-11-18T20:00Z");
        assertEquals(2013, t.getYear());
    }

    @Test
    public void testGetMonth() {
        ScheduledTime t = new ScheduledTime("2013-11-18T20:00Z");
        assertEquals(11, t.getMonth());
    }

    @Test
    public void testGetDay() {
        ScheduledTime t = new ScheduledTime("2013-11-18T20:00Z");
        assertEquals(18, t.getDay());
    }

    @Test
    public void testGetHour() {
        ScheduledTime t = new ScheduledTime("2013-11-18T20:00Z");
        assertEquals(20, t.getHour());
    }

    @Test
    public void testGetMinute() {
        ScheduledTime t = new ScheduledTime("2013-11-18T20:12Z");
        assertEquals(12, t.getMinute());
    }
    
    @Test
    public void testGetSecond() {
        ScheduledTime t = new ScheduledTime("2013-11-18T20:12:53Z");
        assertEquals(53, t.getSecond());
    }
    
    @Test
    public void testGetMillisecond() {
        ScheduledTime t = new ScheduledTime("2013-11-18T20:12:53.023Z");
        assertEquals(23, t.getMillisecond());
    }

}
