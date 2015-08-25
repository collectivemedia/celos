package com.collective.celos;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class AndTriggerTest {
    
    @Test
    public void returnsTrueWhenAllSubTriggersReturnTrue() throws Exception {
        AndTrigger trigger = new AndTrigger(Arrays.asList(new Trigger[] { createAlwaysTrigger(), createAlwaysTrigger(), createAlwaysTrigger() }));
        Scheduler scheduler = mock(Scheduler.class);
        Assert.assertTrue(trigger.isDataAvailable(scheduler, ScheduledTime.now(), new ScheduledTime("2014-02-26T16:13:00Z")));
    }
    
    @Test
    public void returnsFalseWhenOnlyOneSubTriggerReturnsTrue() throws Exception {
        AndTrigger trigger = new AndTrigger(Arrays.asList(new Trigger[] { createAlwaysTrigger(), createNeverTrigger(), createAlwaysTrigger() }));
        Scheduler scheduler = mock(Scheduler.class);
        Assert.assertFalse(trigger.isDataAvailable(scheduler, ScheduledTime.now(), new ScheduledTime("2014-02-26T16:13:00Z")));
    }

    @Test
    public void returnsFalseWhenNoSubTriggersReturnsTrue() throws Exception {
        AndTrigger trigger = new AndTrigger(Arrays.asList(new Trigger[] { createNeverTrigger(), createNeverTrigger(), createNeverTrigger() }));
        Scheduler scheduler = mock(Scheduler.class);
        Assert.assertFalse(trigger.isDataAvailable(scheduler, ScheduledTime.now(), new ScheduledTime("2014-02-26T16:13:00Z")));
    }
    
    @Test
    public void returnsTrueWhenThereAreNoSubTriggers() throws Exception {
        AndTrigger trigger = new AndTrigger(Collections.<Trigger>emptyList());
        Scheduler scheduler = mock(Scheduler.class);
        Assert.assertTrue(trigger.isDataAvailable(scheduler, ScheduledTime.now(), new ScheduledTime("2014-02-26T16:13:00Z")));
    }
    
    public static Trigger createAlwaysTrigger() {
        return new AlwaysTrigger();
    }
    
    public static Trigger createNeverTrigger() {
        return new NeverTrigger();
    }
    
}
