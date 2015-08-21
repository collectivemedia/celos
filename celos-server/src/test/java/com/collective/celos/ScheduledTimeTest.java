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
        Assert.assertEquals(t2, Util.max(t1, t2));
        Assert.assertEquals(t2, Util.max(t2, t1));
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

    @Test
    public void testChainSchedTimeModifications() {
        ScheduledTime t = new ScheduledTime("2013-11-18T20:12:53.023Z");
        ScheduledTime tNew = new ScheduledTime("2015-01-22T00:12:53.023Z");

        ScheduledTime t2 = t.plusYears(1).plusMonths(2).plusDays(3).plusHours(4);
        assertEquals(t2, tNew);

        ScheduledTime tOrig = tNew.minusYears(1).minusMonths(2).minusDays(3).minusHours(4);
        assertEquals(tOrig, t);
    }

    @Test
    public void testZeroPaddingStringFuncs() {
        ScheduledTime t = new ScheduledTime("2013-01-09T00:12:53.023Z");
        assertEquals("2013", t.year());
        assertEquals("01", t.month());
        assertEquals("09", t.day());
        assertEquals("00", t.hour());
        assertEquals("12", t.minute());
        assertEquals("53", t.second());

        ScheduledTime t2 = new ScheduledTime("2013-11-19T10:02:03.023Z");
        assertEquals("2013", t2.year());
        assertEquals("11", t2.month());
        assertEquals("19", t2.day());
        assertEquals("10", t2.hour());
        assertEquals("02", t2.minute());
        assertEquals("03", t2.second());

    }

    @Test
    public void testChainSchedTimeModificationsOneByOne() {

        ScheduledTime t = new ScheduledTime("2013-11-18T20:12:53.023Z");

        assertEquals(new ScheduledTime("2014-11-18T20:12:53.023Z"), t.plusYears(1));
        assertEquals(new ScheduledTime("2012-11-18T20:12:53.023Z"), t.minusYears(1));

        assertEquals(new ScheduledTime("2013-12-18T20:12:53.023Z"), t.plusMonths(1));
        assertEquals(new ScheduledTime("2013-10-18T20:12:53.023Z"), t.minusMonths(1));

        assertEquals(new ScheduledTime("2013-11-19T20:12:53.023Z"), t.plusDays(1));
        assertEquals(new ScheduledTime("2013-11-17T20:12:53.023Z"), t.minusDays(1));

        assertEquals(new ScheduledTime("2013-11-18T21:12:53.023Z"), t.plusHours(1));
        assertEquals(new ScheduledTime("2013-11-18T19:12:53.023Z"), t.minusHours(1));

        assertEquals(new ScheduledTime("2013-11-18T20:13:53.023Z"), t.plusMinutes(1));
        assertEquals(new ScheduledTime("2013-11-18T20:11:53.023Z"), t.minusMinutes(1));

        assertEquals(new ScheduledTime("2013-11-18T20:12:54.023Z"), t.plusSeconds(1));
        assertEquals(new ScheduledTime("2013-11-18T20:12:52.023Z"), t.minusSeconds(1));
    }

}
