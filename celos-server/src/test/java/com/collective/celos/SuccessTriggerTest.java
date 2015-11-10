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
    private StateDatabaseConnection connection = Mockito.mock(StateDatabaseConnection.class);
    private Workflow workflow = Mockito.mock(Workflow.class);
    private WorkflowConfiguration workflowConfiguration = Mockito.mock(WorkflowConfiguration.class);
    private StateDatabase stateDatabase = Mockito.mock(StateDatabase.class);
    private WorkflowID workflowID = new WorkflowID("foo");

    @Before
    public void setUp() {
        when(scheduler.getWorkflowConfiguration()).thenReturn(workflowConfiguration);
        when(workflowConfiguration.findWorkflow(workflowID)).thenReturn(workflow);
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

        trigger.isDataAvailable(connection, new ScheduledTime(DateTime.now()), new ScheduledTime(DateTime.now()));
    }

    @Test
    public void testIsDataAvailableSuccess() throws Exception {

        MemoryStateDatabase msd = new MemoryStateDatabase();

        ScheduledTime scheduledTime = new ScheduledTime(DateTime.now(DateTimeZone.UTC));
        ScheduledTime stNow = new ScheduledTime(DateTime.now(DateTimeZone.UTC).plusMinutes(30));

        msd.openConnection().putSlotState(new SlotState(new SlotID(new WorkflowID("foo"), scheduledTime), SlotState.Status.SUCCESS));

        SuccessTrigger trigger = new SuccessTrigger("foo");

        Assert.assertTrue(trigger.isDataAvailable(msd.openConnection(), stNow, scheduledTime));
    }

    @Test
    public void testIsDataAvailableWait() throws Exception {

        MemoryStateDatabase msd = new MemoryStateDatabase();

        ScheduledTime scheduledTime = new ScheduledTime(DateTime.now(DateTimeZone.UTC));
        ScheduledTime stNow = new ScheduledTime(DateTime.now(DateTimeZone.UTC).plusMinutes(30));

        msd.openConnection().putSlotState(new SlotState(new SlotID(new WorkflowID("foo"), scheduledTime), SlotState.Status.WAITING));

        SuccessTrigger trigger = new SuccessTrigger("foo");


        Assert.assertFalse(trigger.isDataAvailable(connection, stNow, scheduledTime));
    }

    @Test
    public void testIsDataAvailableNoData() throws Exception {

        MemoryStateDatabase msd = new MemoryStateDatabase();

        ScheduledTime scheduledTime = new ScheduledTime(DateTime.now(DateTimeZone.UTC));
        ScheduledTime stNow = new ScheduledTime(DateTime.now(DateTimeZone.UTC).plusMinutes(30));

        msd.openConnection().putSlotState(new SlotState(new SlotID(new WorkflowID("foo2"), stNow), SlotState.Status.SUCCESS));

        SuccessTrigger trigger = new SuccessTrigger("foo");

        Assert.assertFalse(trigger.isDataAvailable(connection, stNow, scheduledTime));
    }

}
