package com.collective.celos;

import java.util.Arrays;
import java.util.Collections;

import com.collective.celos.trigger.AlwaysTrigger;
import com.collective.celos.trigger.OrTrigger;
import com.collective.celos.trigger.Trigger;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class OrTriggerTest {
    
    @Test
    public void returnsTrueWhenAllSubTriggersReturnTrue() throws Exception {
        OrTrigger trigger = new OrTrigger(Arrays.asList(new Trigger[] { createAlwaysTrigger(), createAlwaysTrigger(), createAlwaysTrigger() }));
        Scheduler scheduler = mock(Scheduler.class);
        Assert.assertTrue(trigger.isDataAvailable(scheduler, ScheduledTime.now(), new ScheduledTime("2014-02-26T16:13:00Z")));
    }
    
    @Test
    public void returnsTrueWhenOnlyOneSubTriggerReturnsTrue() throws Exception {
        OrTrigger trigger = new OrTrigger(Arrays.asList(new Trigger[] { createNeverTrigger(), createAlwaysTrigger(), createNeverTrigger() }));
        Scheduler scheduler = mock(Scheduler.class);
        Assert.assertTrue(trigger.isDataAvailable(scheduler, ScheduledTime.now(), new ScheduledTime("2014-02-26T16:13:00Z")));
    }

    @Test
    public void returnsFalseWhenNoSubTriggersReturnsTrue() throws Exception {
        OrTrigger trigger = new OrTrigger(Arrays.asList(new Trigger[] { createNeverTrigger(), createNeverTrigger(), createNeverTrigger() }));
        Scheduler scheduler = mock(Scheduler.class);
        Assert.assertFalse(trigger.isDataAvailable(scheduler, ScheduledTime.now(), new ScheduledTime("2014-02-26T16:13:00Z")));
    }
    
    @Test
    public void returnsFalseWhenThereAreNoSubTriggers() throws Exception {
        OrTrigger trigger = new OrTrigger(Collections.<Trigger>emptyList());
        Scheduler scheduler = mock(Scheduler.class);
        Assert.assertFalse(trigger.isDataAvailable(scheduler, ScheduledTime.now(), new ScheduledTime("2014-02-26T16:13:00Z")));
    }
    
    public static Trigger createAlwaysTrigger() {
        return new AlwaysTrigger();
    }
    
    public static Trigger createNeverTrigger() {
        return new NeverTrigger();
    }
    
}
