package com.collective.celos;

import com.collective.celos.trigger.NotTrigger;
import junit.framework.Assert;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class NotTriggerTest {
    
    @Test
    public void invertsTrue() throws Exception {
        Scheduler scheduler = mock(Scheduler.class);
        Assert.assertFalse(new NotTrigger(AndTriggerTest.createAlwaysTrigger()).isDataAvailable(scheduler, ScheduledTime.now(), ScheduledTime.now()));
    }
    
    @Test
    public void invertsFalse() throws Exception {
        Scheduler scheduler = mock(Scheduler.class);
        Assert.assertTrue(new NotTrigger(AndTriggerTest.createNeverTrigger()).isDataAvailable(scheduler, ScheduledTime.now(), ScheduledTime.now()));
    }
    
}
