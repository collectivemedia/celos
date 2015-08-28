package com.collective.celos;

import com.collective.celos.trigger.SuccessTrigger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class SuccessTriggerTest {

    private Scheduler scheduler = Mockito.mock(Scheduler.class);
    private Workflow workflow = Mockito.mock(Workflow.class);
    private WorkflowConfiguration workflowConfiguration = Mockito.mock(WorkflowConfiguration.class);
    private StateDatabase stateDatabase = Mockito.mock(StateDatabase.class);
    private WorkflowID workflowID = new WorkflowID("foo");

    @Before
    public void setUp() {
        when(scheduler.getWorkflowConfiguration()).thenReturn(workflowConfiguration);
        when(workflowConfiguration.findWorkflow(workflowID)).thenReturn(workflow);
        when(scheduler.getStateDatabase()).thenReturn(stateDatabase);
        when(workflow.getID()).thenReturn(workflowID);
    }



    @Test(expected=NullPointerException.class)
    public void testRequiresWorkflow() throws Exception {
        new SuccessTrigger(null);
    }

    @Test
    public void testSuccessfullyCreates() throws Exception {
        new SuccessTrigger("foo");
    }
    
    @Test
    public void testUsesWorkflowProvided() throws Exception {
        Assert.assertEquals(new WorkflowID("foo"), new SuccessTrigger("foo").getTriggerWorkflowId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailIsDataAvailableInternalTrigger() throws Exception {
        SuccessTrigger trigger = new SuccessTrigger("foo");

        trigger.isDataAvailable(scheduler, new ScheduledTime(DateTime.now()), new ScheduledTime(DateTime.now()));
    }

    @Test
    public void testIsDataAvailableSuccess() throws Exception {

        MemoryStateDatabase msd = new MemoryStateDatabase();

        ScheduledTime scheduledTime = new ScheduledTime(DateTime.now(DateTimeZone.UTC));
        ScheduledTime stNow = new ScheduledTime(DateTime.now(DateTimeZone.UTC).plusMinutes(30));

        msd.putSlotState(new SlotState(new SlotID(new WorkflowID("foo"), scheduledTime), SlotState.Status.SUCCESS));

        Mockito.when(scheduler.getStateDatabase()).thenReturn(msd);
        SuccessTrigger trigger = new SuccessTrigger("foo");

        Assert.assertTrue(trigger.isDataAvailable(scheduler, stNow, scheduledTime));
    }

    @Test
    public void testIsDataAvailableWait() throws Exception {

        MemoryStateDatabase msd = new MemoryStateDatabase();

        ScheduledTime scheduledTime = new ScheduledTime(DateTime.now(DateTimeZone.UTC));
        ScheduledTime stNow = new ScheduledTime(DateTime.now(DateTimeZone.UTC).plusMinutes(30));

        msd.putSlotState(new SlotState(new SlotID(new WorkflowID("foo"), scheduledTime), SlotState.Status.WAITING));

        Mockito.when(scheduler.getStateDatabase()).thenReturn(msd);
        SuccessTrigger trigger = new SuccessTrigger("foo");


        Assert.assertFalse(trigger.isDataAvailable(scheduler, stNow, scheduledTime));
    }

    @Test
    public void testIsDataAvailableNoData() throws Exception {

        MemoryStateDatabase msd = new MemoryStateDatabase();

        ScheduledTime scheduledTime = new ScheduledTime(DateTime.now(DateTimeZone.UTC));
        ScheduledTime stNow = new ScheduledTime(DateTime.now(DateTimeZone.UTC).plusMinutes(30));

        msd.putSlotState(new SlotState(new SlotID(new WorkflowID("foo2"), stNow), SlotState.Status.SUCCESS));

        Mockito.when(scheduler.getStateDatabase()).thenReturn(msd);
        SuccessTrigger trigger = new SuccessTrigger("foo");

        Assert.assertFalse(trigger.isDataAvailable(scheduler, stNow, scheduledTime));
    }

}
