package com.collective.celos;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ScheduledTimeTest {

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
