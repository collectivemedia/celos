package com.collective.celos;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class SuccessTriggerTest {

    @Test(expected=NullPointerException.class)
    public void testRequiresWorkflow() throws Exception {
        new SuccessTrigger(null);
    }

    @Test
    public void testSucessfullyCreates() throws Exception {
        new SuccessTrigger("foo");
    }
    
    @Test
    public void testUsesWorkflowProvided() throws Exception {
        Assert.assertEquals(new WorkflowID("foo"), new SuccessTrigger("foo").getTriggerWorkflowId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailIsDataAvailableInternalTrigger() throws Exception {
        SuccessTrigger trigger = new SuccessTrigger("foo");
        trigger.isDataAvailable(new ScheduledTime(DateTime.now()), new ScheduledTime(DateTime.now()));
    }

    @Test
    public void testIsDataAvailableSuccess() throws Exception {
        SuccessTrigger trigger = new SuccessTrigger("foo");

        Scheduler s = Mockito.mock(Scheduler.class);
        MemoryStateDatabase msd = new MemoryStateDatabase();

        ScheduledTime scheduledTime = new ScheduledTime(DateTime.now(DateTimeZone.UTC));
        ScheduledTime stNow = new ScheduledTime(DateTime.now(DateTimeZone.UTC).plusMinutes(30));

        msd.putSlotState(new SlotState(new SlotID(new WorkflowID("foo"), scheduledTime), SlotState.Status.SUCCESS));

        Mockito.when(s.getStateDatabase()).thenReturn(msd);

        Assert.assertTrue(trigger.isDataAvailable(s, stNow, scheduledTime));
    }

    @Test
    public void testIsDataAvailableWait() throws Exception {
        SuccessTrigger trigger = new SuccessTrigger("foo");

        Scheduler s = Mockito.mock(Scheduler.class);
        MemoryStateDatabase msd = new MemoryStateDatabase();

        ScheduledTime scheduledTime = new ScheduledTime(DateTime.now(DateTimeZone.UTC));
        ScheduledTime stNow = new ScheduledTime(DateTime.now(DateTimeZone.UTC).plusMinutes(30));

        msd.putSlotState(new SlotState(new SlotID(new WorkflowID("foo"), scheduledTime), SlotState.Status.WAITING));

        Mockito.when(s.getStateDatabase()).thenReturn(msd);

        Assert.assertFalse(trigger.isDataAvailable(s, stNow, scheduledTime));
    }

    @Test
    public void testIsDataAvailableNoData() throws Exception {
        SuccessTrigger trigger = new SuccessTrigger("foo");

        Scheduler s = Mockito.mock(Scheduler.class);
        MemoryStateDatabase msd = new MemoryStateDatabase();

        ScheduledTime scheduledTime = new ScheduledTime(DateTime.now(DateTimeZone.UTC));
        ScheduledTime stNow = new ScheduledTime(DateTime.now(DateTimeZone.UTC).plusMinutes(30));

        msd.putSlotState(new SlotState(new SlotID(new WorkflowID("foo2"), stNow), SlotState.Status.SUCCESS));

        Mockito.when(s.getStateDatabase()).thenReturn(msd);

        Assert.assertFalse(trigger.isDataAvailable(s, stNow, scheduledTime));
    }

}
