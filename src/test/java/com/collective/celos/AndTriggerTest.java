package com.collective.celos;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

public class AndTriggerTest {
    
    @Test
    public void returnsTrueWhenAllSubTriggersReturnTrue() throws Exception {
        AndTrigger trigger = new AndTrigger(Arrays.asList(new Trigger[] { createAlwaysTrigger(), createAlwaysTrigger(), createAlwaysTrigger() }));
        Assert.assertTrue(trigger.isDataAvailable(ScheduledTime.now(), new ScheduledTime("2014-02-26T16:13:00Z")));
    }
    
    @Test
    public void returnsFalseWhenOnlyOneSubTriggerReturnsTrue() throws Exception {
        AndTrigger trigger = new AndTrigger(Arrays.asList(new Trigger[] { createAlwaysTrigger(), createNeverTrigger(), createAlwaysTrigger() }));
        Assert.assertFalse(trigger.isDataAvailable(ScheduledTime.now(), new ScheduledTime("2014-02-26T16:13:00Z")));
    }

    @Test
    public void returnsFalseWhenNoSubTriggersReturnsTrue() throws Exception {
        AndTrigger trigger = new AndTrigger(Arrays.asList(new Trigger[] { createNeverTrigger(), createNeverTrigger(), createNeverTrigger() }));
        Assert.assertFalse(trigger.isDataAvailable(ScheduledTime.now(), new ScheduledTime("2014-02-26T16:13:00Z")));
    }
    
    @Test
    public void returnsTrueWhenThereAreNoSubTriggers() throws Exception {
        AndTrigger trigger = new AndTrigger(Collections.<Trigger>emptyList());
        Assert.assertTrue(trigger.isDataAvailable(ScheduledTime.now(), new ScheduledTime("2014-02-26T16:13:00Z")));
    }
    
    public static Trigger createAlwaysTrigger() {
        return new AlwaysTrigger();
    }
    
    public static Trigger createNeverTrigger() {
        return new NeverTrigger();
    }
    
}
