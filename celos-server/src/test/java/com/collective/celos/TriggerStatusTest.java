package com.collective.celos;

import com.collective.celos.trigger.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.mockito.Mockito.when;

public class TriggerStatusTest {

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



    @Test
    public void testRequiresWorkflow() throws Exception {
        final ScheduledTime now = ScheduledTime.now();
        final int size = new NeverTrigger().getTriggerStatus(scheduler, now, now).getSubStatuses().size();
        Assert.assertEquals(size, 0);
    }

    @Test
    public void testSuccessfullyAnd() throws Exception {
        final ScheduledTime now = ScheduledTime.now();
        final ArrayList<Trigger> triggers = new ArrayList<>();
        triggers.add(new AlwaysTrigger());
        triggers.add(new AlwaysTrigger());
        final TriggerStatus statusAnd1 = new AndTrigger(triggers).getTriggerStatus(scheduler, now, now);
        Assert.assertTrue(statusAnd1.isReady());
        triggers.add(new NeverTrigger());
        final TriggerStatus statusAnd2 = new AndTrigger(triggers).getTriggerStatus(scheduler, now, now);
        Assert.assertFalse(statusAnd2.isReady());
    }

    @Test
    public void testSuccessfullyOr() throws Exception {
        final ScheduledTime now = ScheduledTime.now();
        final ArrayList<Trigger> triggers = new ArrayList<>();
        final TriggerStatus statusOr0 = new OrTrigger(triggers).getTriggerStatus(scheduler, now, now);
        Assert.assertFalse(statusOr0.isReady());
        Assert.assertEquals(statusOr0.getSubStatuses().size(), 0);
        triggers.add(new NeverTrigger());
        triggers.add(new NeverTrigger());
        triggers.add(new NeverTrigger());
        triggers.add(new NeverTrigger());
        final TriggerStatus statusOr1 = new OrTrigger(triggers).getTriggerStatus(scheduler, now, now);
        Assert.assertFalse(statusOr1.isReady());
        Assert.assertEquals(statusOr1.getSubStatuses().size(), 4);
        Assert.assertTrue(statusOr1.getSubStatuses().get(0) != null);
        triggers.add(new AlwaysTrigger());
        triggers.add(new AlwaysTrigger());
        final TriggerStatus statusOr2 = new OrTrigger(triggers).getTriggerStatus(scheduler, now, now);
        Assert.assertTrue(statusOr2.isReady());
        Assert.assertEquals(statusOr2.getSubStatuses().size(), 6);



    }

    @Test
    public void testSuccessfullySingleton() throws Exception {
        final ScheduledTime now = ScheduledTime.now();
        final ArrayList<Trigger> triggers = new ArrayList<>();
        triggers.add(new NeverTrigger());
        final TriggerStatus statusOr0 = new OrTrigger(triggers).getTriggerStatus(scheduler, now, now);
        Assert.assertFalse(statusOr0.getSubStatuses().get(0).isReady());
    }

}
