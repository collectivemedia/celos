package com.collective.celos;

import junit.framework.Assert;

import org.junit.Test;

public class NotTriggerTest {
    
    @Test
    public void invertsTrue() throws Exception {
        Assert.assertFalse(new NotTrigger(AndTriggerTest.createAlwaysTrigger()).isDataAvailable(ScheduledTime.now(), ScheduledTime.now()));
    }
    
    @Test
    public void invertsFalse() throws Exception {
        Assert.assertTrue(new NotTrigger(AndTriggerTest.createNeverTrigger()).isDataAvailable(ScheduledTime.now(), ScheduledTime.now()));
    }
    
}
