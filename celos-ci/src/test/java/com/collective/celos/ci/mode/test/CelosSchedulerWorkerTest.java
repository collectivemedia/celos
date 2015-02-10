package com.collective.celos.ci.mode.test;

import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import com.collective.celos.ci.mode.test.client.CelosClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

/**
 * Created by akonopko on 21.01.15.
 */
public class CelosSchedulerWorkerTest {

    @Test
    public void testIsThereAnyRunningWorkflowsFalse1() throws Exception {
        CelosClient celosClient = mock(CelosClient.class);
        CelosSchedulerWorker worker = new CelosSchedulerWorker(celosClient);
        WorkflowID wf1 = new WorkflowID("wf1");
        WorkflowID wf2 = new WorkflowID("wf2");
        Set<WorkflowID> workflowSet = Sets.newHashSet(wf1, wf2);
        ScheduledTime scheduledTime = new ScheduledTime("2013-12-20T16:00Z");

        SlotState slotState1 = new SlotState(new SlotID(wf1, scheduledTime), SlotState.Status.WAITING);
        SlotState slotState2 = new SlotState(new SlotID(wf1, scheduledTime), SlotState.Status.SUCCESS);

        SlotState slotState3 = new SlotState(new SlotID(wf2, scheduledTime), SlotState.Status.WAITING);
        SlotState slotState4 = new SlotState(new SlotID(wf2, scheduledTime), SlotState.Status.FAILURE);

        doReturn(Lists.newArrayList(slotState1, slotState2)).when(celosClient).getWorkflowStatus(wf1, scheduledTime);
        doReturn(Lists.newArrayList(slotState3, slotState4)).when(celosClient).getWorkflowStatus(wf2, scheduledTime);
        Assert.assertFalse(worker.isThereAnyRunningWorkflows(workflowSet, scheduledTime));
    }


    @Test
    public void testIsThereAnyRunningWorkflowsTrue() throws Exception {
        CelosClient celosClient = mock(CelosClient.class);
        CelosSchedulerWorker worker = new CelosSchedulerWorker(celosClient);
        WorkflowID wf1 = new WorkflowID("wf1");
        WorkflowID wf2 = new WorkflowID("wf2");
        Set<WorkflowID> workflowSet = Sets.newHashSet(wf1, wf2);
        ScheduledTime scheduledTime = new ScheduledTime("2013-12-20T16:00Z");

        SlotState slotState1 = new SlotState(new SlotID(wf1, scheduledTime), SlotState.Status.WAITING);
        SlotState slotState2 = new SlotState(new SlotID(wf1, scheduledTime), SlotState.Status.READY);

        SlotState slotState3 = new SlotState(new SlotID(wf2, scheduledTime), SlotState.Status.WAITING);
        SlotState slotState4 = new SlotState(new SlotID(wf2, scheduledTime), SlotState.Status.RUNNING);


        doReturn(Lists.newArrayList(slotState1, slotState2)).when(celosClient).getWorkflowStatus(wf1, scheduledTime);
        doReturn(Lists.newArrayList(slotState3, slotState4)).when(celosClient).getWorkflowStatus(wf2, scheduledTime);

        Assert.assertTrue(worker.isThereAnyRunningWorkflows(workflowSet, scheduledTime));
    }


    @Test
    public void testGetWorkflowStatusesInfo() throws Exception {
        CelosClient celosClient = mock(CelosClient.class);
        CelosSchedulerWorker worker = new CelosSchedulerWorker(celosClient);

        WorkflowID wf1 = new WorkflowID("wf1");
        WorkflowID wf2 = new WorkflowID("wf2");
        Set<WorkflowID> workflowSet = Sets.newHashSet(wf1, wf2);
        ScheduledTime scheduledTime = new ScheduledTime("2013-12-20T16:00Z");

        SlotState slotState1 = new SlotState(new SlotID(wf1, scheduledTime), SlotState.Status.WAITING);
        SlotState slotState2 = new SlotState(new SlotID(wf1, scheduledTime), SlotState.Status.READY);

        SlotState slotState3 = new SlotState(new SlotID(wf2, scheduledTime), SlotState.Status.WAITING);
        SlotState slotState4 = new SlotState(new SlotID(wf2, scheduledTime), SlotState.Status.RUNNING);


        doReturn(Lists.newArrayList(slotState1, slotState2)).when(celosClient).getWorkflowStatus(wf1, scheduledTime);
        doReturn(Lists.newArrayList(slotState3, slotState4)).when(celosClient).getWorkflowStatus(wf2, scheduledTime);

        Set<String> statuses = worker.getWorkflowStatusesInfo(workflowSet, scheduledTime);
        Assert.assertEquals(statuses, Sets.newHashSet(
                "SlotState[slotID=wf1@2013-12-20T16:00:00.000Z,status=READY,externalID=<null>,retryCount=0]",
                "SlotState[slotID=wf2@2013-12-20T16:00:00.000Z,status=RUNNING,externalID=<null>,retryCount=0]")
        );
    }

    @Test
    public void testRunCelosSchedulerAllWfs() throws Exception {

        CelosClient celosClient = mock(CelosClient.class);
        CelosSchedulerWorker worker = new CelosSchedulerWorker(celosClient);

        ScheduledTime scheduledTimeStart = new ScheduledTime("2013-12-20T16:00Z");
        ScheduledTime scheduledTimeEnd = new ScheduledTime("2013-12-20T18:00Z");

        TestCase testCase = new TestCase("name", scheduledTimeStart, scheduledTimeEnd);
        Set<WorkflowID> workflowIDs = Sets.newHashSet(new WorkflowID("wf1"), new WorkflowID("wf2"));
        doReturn(workflowIDs).when(celosClient).getWorkflowList();

        worker.runCelosScheduler(testCase);

        verify(celosClient).iterateScheduler(scheduledTimeStart.plusSeconds(1), Collections.<WorkflowID>emptySet());
        verify(celosClient).iterateScheduler(scheduledTimeStart.plusHours(1).plusSeconds(1), Collections.<WorkflowID>emptySet());
        verify(celosClient).iterateScheduler(scheduledTimeStart.plusHours(2).plusSeconds(1), Collections.<WorkflowID>emptySet());
    }

    @Test
    public void testRunCelosSchedulerRestrictWfs() throws Exception {

        CelosClient celosClient = mock(CelosClient.class);
        CelosSchedulerWorker worker = new CelosSchedulerWorker(celosClient);

        ScheduledTime scheduledTimeStart = new ScheduledTime("2013-12-20T16:00Z");
        ScheduledTime scheduledTimeEnd = new ScheduledTime("2013-12-20T18:00Z");

        TestCase testCase = new TestCase("name", scheduledTimeStart, scheduledTimeEnd);
        WorkflowID workflowID = new WorkflowID("wf1");
        testCase.addTargetWorkflow(workflowID);
        Set<WorkflowID> workflowIDs = Sets.newHashSet(new WorkflowID("wf1"), new WorkflowID("wf2"));
        doReturn(workflowIDs).when(celosClient).getWorkflowList();

        worker.runCelosScheduler(testCase);

        Set<WorkflowID> workflowIDSet = Sets.newHashSet(workflowID);
        verify(celosClient).iterateScheduler(scheduledTimeStart.plusSeconds(1), workflowIDSet);
        verify(celosClient).iterateScheduler(scheduledTimeStart.plusHours(1).plusSeconds(1), workflowIDSet);
        verify(celosClient).iterateScheduler(scheduledTimeStart.plusHours(2).plusSeconds(1), workflowIDSet);
    }

}
