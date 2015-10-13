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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.ZonedDateTime;

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

//    FIXME
//    @Test(expected = IllegalArgumentException.class)
//    public void testFailIsDataAvailableInternalTrigger() throws Exception {
//        SuccessTrigger trigger = new SuccessTrigger("foo");
//
//        trigger.isDataAvailable(scheduler, Util.zonedDateTimeNowUTC(), Util.zonedDateTimeNowUTC());
//    }

    @Test
    public void testIsDataAvailableSuccess() throws Exception {

        MemoryStateDatabase msd = new MemoryStateDatabase();

        ZonedDateTime scheduledTime = Util.zonedDateTimeNowUTC();
        ZonedDateTime stNow = Util.zonedDateTimeNowUTC().plusMinutes(30);

        msd.putSlotState(new SlotState(new SlotID(new WorkflowID("foo"), scheduledTime), SlotState.Status.SUCCESS));

        Mockito.when(scheduler.getStateDatabase()).thenReturn(msd);
        SuccessTrigger trigger = new SuccessTrigger("foo");

        Assert.assertTrue(trigger.isDataAvailable(scheduler, stNow, scheduledTime));
    }

    @Test
    public void testIsDataAvailableWait() throws Exception {

        MemoryStateDatabase msd = new MemoryStateDatabase();

        ZonedDateTime scheduledTime = Util.zonedDateTimeNowUTC();
        ZonedDateTime stNow = Util.zonedDateTimeNowUTC().plusMinutes(30);

        msd.putSlotState(new SlotState(new SlotID(new WorkflowID("foo"), scheduledTime), SlotState.Status.WAITING));

        Mockito.when(scheduler.getStateDatabase()).thenReturn(msd);
        SuccessTrigger trigger = new SuccessTrigger("foo");


        Assert.assertFalse(trigger.isDataAvailable(scheduler, stNow, scheduledTime));
    }

    @Test
    public void testIsDataAvailableNoData() throws Exception {

        MemoryStateDatabase msd = new MemoryStateDatabase();

        ZonedDateTime scheduledTime = Util.zonedDateTimeNowUTC();
        ZonedDateTime stNow = Util.zonedDateTimeNowUTC().plusMinutes(30);

        msd.putSlotState(new SlotState(new SlotID(new WorkflowID("foo2"), stNow), SlotState.Status.SUCCESS));

        Mockito.when(scheduler.getStateDatabase()).thenReturn(msd);
        SuccessTrigger trigger = new SuccessTrigger("foo");

        Assert.assertFalse(trigger.isDataAvailable(scheduler, stNow, scheduledTime));
    }

}
