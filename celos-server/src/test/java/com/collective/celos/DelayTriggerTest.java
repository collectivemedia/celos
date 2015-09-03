package com.collective.celos;

import com.collective.celos.trigger.DelayTrigger;
import com.collective.celos.trigger.Trigger;
import com.collective.celos.trigger.TriggerStatus;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class DelayTriggerTest {

    private Scheduler scheduler = mock(Scheduler.class);
    private ScheduledTime now = new ScheduledTime("2014-01-01T05:00:00Z");

    @Test
    public void canBeConfiguredFromJSON() throws Exception {
        Assert.assertEquals(12, new DelayTrigger(12).getSeconds());
    }
    
    @Test
    public void works() throws Exception {
        Trigger t = new DelayTrigger(60 * 60);
        Assert.assertTrue(t.isDataAvailable(scheduler, now, new ScheduledTime("1980-01-01T03:59:00Z")));
        Assert.assertTrue(t.isDataAvailable(scheduler, now, new ScheduledTime("2014-01-01T03:59:00Z")));
        Assert.assertTrue(t.isDataAvailable(scheduler, now, new ScheduledTime("2014-01-01T03:59:59Z")));
        Assert.assertFalse(t.isDataAvailable(scheduler, now, new ScheduledTime("2014-01-01T04:00:00Z")));
        Assert.assertFalse(t.isDataAvailable(scheduler, now, new ScheduledTime("2014-01-01T05:00:00Z")));
        Assert.assertFalse(t.isDataAvailable(scheduler, now, new ScheduledTime("2014-01-01T06:00:00Z")));
        Assert.assertFalse(t.isDataAvailable(scheduler, now, new ScheduledTime("2080-01-01T06:00:00Z")));
    }


    @Test
    public void descriptionReady() throws Exception {
        Trigger t = new DelayTrigger(60 * 60);
        final TriggerStatus triggerStatus = t.getTriggerStatus(scheduler, now.plusHours(2), now);
        final String description = triggerStatus.getDescription();
        Assert.assertTrue(triggerStatus.isReady());
        Assert.assertEquals("Ready since 2014-01-01T06:00:00.000Z", description);
    }

    @Test
    public void descriptionNotReady() throws Exception {
        Trigger t = new DelayTrigger(60 * 60);
        final TriggerStatus triggerStatus = t.getTriggerStatus(scheduler, now, now);
        final String description = triggerStatus.getDescription();
        Assert.assertFalse(triggerStatus.isReady());
        Assert.assertEquals("Delayed until 2014-01-01T06:00:00.000Z", description);
    }

}
