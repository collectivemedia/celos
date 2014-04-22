package com.collective.celos;

import org.junit.Assert;
import org.junit.Test;

public class DelayTriggerTest {
    
    @Test
    public void canBeConfiguredFromJSON() throws Exception {
        Assert.assertEquals(12, new DelayTrigger(12).getSeconds());
    }
    
    @Test
    public void works() throws Exception {
        ScheduledTime now = new ScheduledTime("2014-01-01T05:00:00Z");
        Trigger t = new DelayTrigger(60 * 60);
        Assert.assertTrue(t.isDataAvailable(now, new ScheduledTime("1980-01-01T03:59:00Z")));
        Assert.assertTrue(t.isDataAvailable(now, new ScheduledTime("2014-01-01T03:59:00Z")));
        Assert.assertTrue(t.isDataAvailable(now, new ScheduledTime("2014-01-01T03:59:59Z")));
        Assert.assertFalse(t.isDataAvailable(now, new ScheduledTime("2014-01-01T04:00:00Z")));
        Assert.assertFalse(t.isDataAvailable(now, new ScheduledTime("2014-01-01T05:00:00Z")));
        Assert.assertFalse(t.isDataAvailable(now, new ScheduledTime("2014-01-01T06:00:00Z")));
        Assert.assertFalse(t.isDataAvailable(now, new ScheduledTime("2080-01-01T06:00:00Z")));
    }
}
