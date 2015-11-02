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

import com.collective.celos.trigger.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.mockito.Mockito.when;

public class TriggerStatusTest {

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
        when(scheduler.getStateDatabase()).thenReturn(stateDatabase);
        when(workflow.getID()).thenReturn(workflowID);
    }



    @Test
    public void testRequiresWorkflow() throws Exception {
        final ScheduledTime now = ScheduledTime.now();
        final int size = new NeverTrigger().getTriggerStatus(connection, now, now).getSubStatuses().size();
        Assert.assertEquals(size, 0);
    }

    @Test
    public void testSuccessfullyAnd() throws Exception {
        final ScheduledTime now = ScheduledTime.now();
        final ArrayList<Trigger> triggers = new ArrayList<>();
        triggers.add(new AlwaysTrigger());
        triggers.add(new AlwaysTrigger());
        final TriggerStatus statusAnd1 = new AndTrigger(triggers).getTriggerStatus(connection, now, now);
        Assert.assertTrue(statusAnd1.isReady());
        triggers.add(new NeverTrigger());
        final TriggerStatus statusAnd2 = new AndTrigger(triggers).getTriggerStatus(connection, now, now);
        Assert.assertFalse(statusAnd2.isReady());
    }

    @Test
    public void testSuccessfullyOr() throws Exception {
        final ScheduledTime now = ScheduledTime.now();
        final ArrayList<Trigger> triggers = new ArrayList<>();
        final TriggerStatus statusOr0 = new OrTrigger(triggers).getTriggerStatus(connection, now, now);
        Assert.assertFalse(statusOr0.isReady());
        Assert.assertEquals(statusOr0.getSubStatuses().size(), 0);
        triggers.add(new NeverTrigger());
        triggers.add(new NeverTrigger());
        triggers.add(new NeverTrigger());
        triggers.add(new NeverTrigger());
        final TriggerStatus statusOr1 = new OrTrigger(triggers).getTriggerStatus(connection, now, now);
        Assert.assertFalse(statusOr1.isReady());
        Assert.assertEquals(statusOr1.getSubStatuses().size(), 4);
        Assert.assertTrue(statusOr1.getSubStatuses().get(0) != null);
        triggers.add(new AlwaysTrigger());
        triggers.add(new AlwaysTrigger());
        final TriggerStatus statusOr2 = new OrTrigger(triggers).getTriggerStatus(connection, now, now);
        Assert.assertTrue(statusOr2.isReady());
        Assert.assertEquals(statusOr2.getSubStatuses().size(), 6);



    }

    @Test
    public void testSuccessfullySingleton() throws Exception {
        final ScheduledTime now = ScheduledTime.now();
        final ArrayList<Trigger> triggers = new ArrayList<>();
        triggers.add(new NeverTrigger());
        final TriggerStatus statusOr0 = new OrTrigger(triggers).getTriggerStatus(connection, now, now);
        Assert.assertFalse(statusOr0.getSubStatuses().get(0).isReady());
    }

}
