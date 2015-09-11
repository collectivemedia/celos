/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos.ci.mode.test;

import com.collective.celos.*;
import com.google.common.collect.Sets;
import junit.framework.Assert;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
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
        ZonedDateTime scheduledTime = ZonedDateTime.parse("2013-12-20T16:00Z");

        SlotState slotState1 = new SlotState(new SlotID(wf1, scheduledTime), SlotState.Status.WAITING);
        SlotState slotState2 = new SlotState(new SlotID(wf1, scheduledTime), SlotState.Status.SUCCESS);

        SlotState slotState3 = new SlotState(new SlotID(wf2, scheduledTime), SlotState.Status.WAITING);
        SlotState slotState4 = new SlotState(new SlotID(wf2, scheduledTime), SlotState.Status.FAILURE);

        doReturn(createWfStatus(slotState1, slotState2)).when(celosClient).getWorkflowStatus(wf1, scheduledTime);
        doReturn(createWfStatus(slotState3, slotState4)).when(celosClient).getWorkflowStatus(wf2, scheduledTime);
        Assert.assertFalse(worker.isThereAnyRunningWorkflows(workflowSet, scheduledTime));
    }

    private WorkflowStatus createWfStatus(SlotState... states) {
        WorkflowInfo info = new WorkflowInfo(null, Collections.<WorkflowInfo.ContactsInfo>emptyList());
        return new WorkflowStatus(info, Arrays.asList(states));
    }


    @Test
    public void testIsThereAnyRunningWorkflowsTrue() throws Exception {
        CelosClient celosClient = mock(CelosClient.class);
        CelosSchedulerWorker worker = new CelosSchedulerWorker(celosClient);
        WorkflowID wf1 = new WorkflowID("wf1");
        WorkflowID wf2 = new WorkflowID("wf2");
        Set<WorkflowID> workflowSet = Sets.newHashSet(wf1, wf2);
        ZonedDateTime scheduledTime = ZonedDateTime.parse("2013-12-20T16:00Z");

        SlotState slotState1 = new SlotState(new SlotID(wf1, scheduledTime), SlotState.Status.WAITING);
        SlotState slotState2 = new SlotState(new SlotID(wf1, scheduledTime), SlotState.Status.READY);

        SlotState slotState3 = new SlotState(new SlotID(wf2, scheduledTime), SlotState.Status.WAITING);
        SlotState slotState4 = new SlotState(new SlotID(wf2, scheduledTime), SlotState.Status.RUNNING);


        doReturn(createWfStatus(slotState1, slotState2)).when(celosClient).getWorkflowStatus(wf1, scheduledTime);
        doReturn(createWfStatus(slotState3, slotState4)).when(celosClient).getWorkflowStatus(wf2, scheduledTime);

        Assert.assertTrue(worker.isThereAnyRunningWorkflows(workflowSet, scheduledTime));
    }


    @Test
    public void testGetWorkflowStatusesInfo() throws Exception {
        CelosClient celosClient = mock(CelosClient.class);
        CelosSchedulerWorker worker = new CelosSchedulerWorker(celosClient);

        WorkflowID wf1 = new WorkflowID("wf1");
        WorkflowID wf2 = new WorkflowID("wf2");
        Set<WorkflowID> workflowSet = Sets.newHashSet(wf1, wf2);
        ZonedDateTime scheduledTime = ZonedDateTime.parse("2013-12-20T16:00Z");

        SlotState slotState1 = new SlotState(new SlotID(wf1, scheduledTime), SlotState.Status.WAITING);
        SlotState slotState2 = new SlotState(new SlotID(wf1, scheduledTime), SlotState.Status.READY);

        SlotState slotState3 = new SlotState(new SlotID(wf2, scheduledTime), SlotState.Status.WAITING);
        SlotState slotState4 = new SlotState(new SlotID(wf2, scheduledTime), SlotState.Status.RUNNING);


        doReturn(createWfStatus(slotState1, slotState2)).when(celosClient).getWorkflowStatus(wf1, scheduledTime);
        doReturn(createWfStatus(slotState3, slotState4)).when(celosClient).getWorkflowStatus(wf2, scheduledTime);

        Set<String> statuses = worker.getWorkflowStatusesInfo(workflowSet, scheduledTime);
        Assert.assertEquals(statuses, Sets.newHashSet(
                "SlotState[slotID=wf1@2013-12-20T16:00Z,status=READY,externalID=<null>,retryCount=0]",
                "SlotState[slotID=wf2@2013-12-20T16:00Z,status=RUNNING,externalID=<null>,retryCount=0]")
        );
    }

    @Test
    public void testRunCelosSchedulerAllWfs() throws Exception {

        CelosClient celosClient = mock(CelosClient.class);
        CelosSchedulerWorker worker = new CelosSchedulerWorker(celosClient);

        ZonedDateTime scheduledTimeStart = ZonedDateTime.parse("2013-12-20T16:00Z");
        ZonedDateTime scheduledTimeEnd = ZonedDateTime.parse("2013-12-20T18:00Z");

        ZonedDateTime time1 = scheduledTimeStart.plusSeconds(1);
        ZonedDateTime time2 = scheduledTimeStart.plusHours(1).plusSeconds(1);
        ZonedDateTime time3 = scheduledTimeStart.plusHours(2).plusSeconds(1);

        WorkflowID wf1 = new WorkflowID("wf1");
        WorkflowID wf2 = new WorkflowID("wf2");

        TestCase testCase = new TestCase("name", scheduledTimeStart, scheduledTimeEnd);

        Set<WorkflowID> workflowIDs = Sets.newHashSet(wf1, wf2);
        doReturn(workflowIDs).when(celosClient).getWorkflowList();

        doReturn(createWfStatus()).when(celosClient).getWorkflowStatus(wf1, time1);
        doReturn(createWfStatus()).when(celosClient).getWorkflowStatus(wf2, time1);
        doReturn(createWfStatus()).when(celosClient).getWorkflowStatus(wf1, time2);
        doReturn(createWfStatus()).when(celosClient).getWorkflowStatus(wf2, time2);
        doReturn(createWfStatus()).when(celosClient).getWorkflowStatus(wf1, time3);
        doReturn(createWfStatus()).when(celosClient).getWorkflowStatus(wf2, time3);

        worker.runCelosScheduler(testCase);

        verify(celosClient).iterateScheduler(time1, Collections.<WorkflowID>emptySet());
        verify(celosClient).iterateScheduler(time2, Collections.<WorkflowID>emptySet());
        verify(celosClient).iterateScheduler(time3, Collections.<WorkflowID>emptySet());
    }

    @Test
    public void testRunCelosSchedulerRestrictWfs() throws Exception {

        CelosClient celosClient = mock(CelosClient.class);
        CelosSchedulerWorker worker = new CelosSchedulerWorker(celosClient);

        ZonedDateTime scheduledTimeStart = ZonedDateTime.parse("2013-12-20T16:00Z");
        ZonedDateTime scheduledTimeEnd = ZonedDateTime.parse("2013-12-20T18:00Z");

        ZonedDateTime time1 = scheduledTimeStart.plusSeconds(1);
        ZonedDateTime time2 = scheduledTimeStart.plusHours(1).plusSeconds(1);
        ZonedDateTime time3 = scheduledTimeStart.plusHours(2).plusSeconds(1);

        WorkflowID wf1 = new WorkflowID("wf1");
        WorkflowID wf2 = new WorkflowID("wf2");

        TestCase testCase = new TestCase("name", scheduledTimeStart, scheduledTimeEnd);
        testCase.addTargetWorkflow(wf1);

        Set<WorkflowID> workflowIDs = Sets.newHashSet(wf1, wf2);
        doReturn(workflowIDs).when(celosClient).getWorkflowList();

        doReturn(createWfStatus()).when(celosClient).getWorkflowStatus(wf1, time1);
        doReturn(createWfStatus()).when(celosClient).getWorkflowStatus(wf2, time1);
        doReturn(createWfStatus()).when(celosClient).getWorkflowStatus(wf1, time2);
        doReturn(createWfStatus()).when(celosClient).getWorkflowStatus(wf2, time2);
        doReturn(createWfStatus()).when(celosClient).getWorkflowStatus(wf1, time3);
        doReturn(createWfStatus()).when(celosClient).getWorkflowStatus(wf2, time3);

        worker.runCelosScheduler(testCase);

        Set<WorkflowID> workflowIDSet = Sets.newHashSet(wf1);
        verify(celosClient).iterateScheduler(scheduledTimeStart.plusSeconds(1), workflowIDSet);
        verify(celosClient).iterateScheduler(scheduledTimeStart.plusHours(1).plusSeconds(1), workflowIDSet);
        verify(celosClient).iterateScheduler(scheduledTimeStart.plusHours(2).plusSeconds(1), workflowIDSet);

        verify(celosClient, never()).iterateScheduler(time1, Collections.<WorkflowID>emptySet());
        verify(celosClient, never()).iterateScheduler(time2, Collections.<WorkflowID>emptySet());
        verify(celosClient, never()).iterateScheduler(time3, Collections.<WorkflowID>emptySet());
    }

}
