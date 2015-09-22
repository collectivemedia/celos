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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Set;

public class SchedulerDepententSlotsTest {

    private Scheduler scheduler;

    @Before
    public void setUp() throws Exception {
        String path = "com/collective/celos/dependent-slots-test/workflow.js";
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        File workflowFile = new File(resource.toURI());

        WorkflowConfigurationParser workflowConfigurationParser = new WorkflowConfigurationParser(new File("."), ImmutableMap.<String, String>of());
        workflowConfigurationParser.parseFile(workflowFile);
        WorkflowConfiguration cfg = workflowConfigurationParser.getWorkflowConfiguration();

        StateDatabase emptyDB = new MemoryStateDatabase();
        this.scheduler = new Scheduler(cfg, emptyDB, 24);
    }

    @Test
    public void testUpstreamFindDependentSlotsSameSchedule() throws Exception {

        ScheduledTime scheduledTime = new ScheduledTime("2014-03-10T12:00:00.000Z");
        SlotID id1 = new SlotID(new WorkflowID("workflow-1"), scheduledTime);
        Set<SlotID> idSet = scheduler.getUpstreamSlots(id1);
        Assert.assertEquals(idSet.size(), 0);

        SlotID id2 = new SlotID(new WorkflowID("workflow-2"), scheduledTime);
        Set<SlotID> idSet2 = scheduler.getUpstreamSlots(id2);
        Assert.assertEquals(idSet2, Sets.newHashSet(id1));

        SlotID id3 = new SlotID(new WorkflowID("workflow-3"), scheduledTime);
        Set<SlotID> idSet3 = scheduler.getUpstreamSlots(id3);
        Assert.assertEquals(idSet3, Sets.newHashSet(id1, id2));

        SlotID id4offset = new SlotID(new WorkflowID("workflow-4"), scheduledTime.plusHours(1));

        SlotID id5 = new SlotID(new WorkflowID("workflow-5"), scheduledTime);
        Set<SlotID> idSet5 = scheduler.getUpstreamSlots(id5);
        Assert.assertEquals(idSet5, Sets.newHashSet(id1, id4offset));
    }

    @Test
    public void testUpstreamFindDependentSlotsDifferentSchedule() throws Exception {

        ScheduledTime scheduledTime = new ScheduledTime("2014-03-10T00:00:00.000Z");

        SlotID id6 = new SlotID(new WorkflowID("workflow-6-daily"), scheduledTime);
        SlotID id7 = new SlotID(new WorkflowID("workflow-7-depend-daily"), scheduledTime);

        Set<SlotID> idSet5 = scheduler.getUpstreamSlots(id7);
        Assert.assertEquals(idSet5, Sets.newHashSet(id6));

        ScheduledTime scheduledTime2 = new ScheduledTime("2014-03-10T01:00:00.000Z");

        SlotID id7_2 = new SlotID(new WorkflowID("workflow-7-depend-daily"), scheduledTime2);

        Set<SlotID> idSet5_2 = scheduler.getUpstreamSlots(id7_2);
        Assert.assertEquals(idSet5_2.size(), 0);

    }

    @Test
    public void testUpstreamTimeDoesntAlign() throws Exception {

        ScheduledTime scheduledTime = new ScheduledTime("2014-03-10T12:00:01.000Z");

        SlotID id2 = new SlotID(new WorkflowID("workflow-2"), scheduledTime);
        Set<SlotID> idSet2 = scheduler.getUpstreamSlots(id2);
        Assert.assertEquals(idSet2.size(), 0);
    }


    @Test
    public void testDownstreamFindDependentSlotsSameSchedule() throws Exception {

        ScheduledTime scheduledTime = new ScheduledTime("2014-03-10T12:00:00.000Z");
        SlotID id1 = new SlotID(new WorkflowID("workflow-1"), scheduledTime);
        SlotID id2 = new SlotID(new WorkflowID("workflow-2"), scheduledTime);
        SlotID id3 = new SlotID(new WorkflowID("workflow-3"), scheduledTime);
        SlotID id4 = new SlotID(new WorkflowID("workflow-4"), scheduledTime);
        SlotID id5 = new SlotID(new WorkflowID("workflow-5"), scheduledTime);
        SlotID id5_offset = new SlotID(new WorkflowID("workflow-5"), scheduledTime.plusHours(1));
        SlotID id8 = new SlotID(new WorkflowID("workflow-8"), scheduledTime);

        Assert.assertEquals(Sets.newHashSet(id2, id3, id5), scheduler.getDownstreamSlots(id1, false));
        Assert.assertEquals(Sets.newHashSet(id3), scheduler.getDownstreamSlots(id2, false));
        Assert.assertEquals(Sets.newHashSet(id8), scheduler.getDownstreamSlots(id3, false));
        Assert.assertEquals(Sets.newHashSet(id5_offset), scheduler.getDownstreamSlots(id4, false));
    }

    @Test
    public void testDownstreamTransitive() throws Exception {

        ScheduledTime scheduledTime = new ScheduledTime("2014-03-10T12:00:00.000Z");
        SlotID id1 = new SlotID(new WorkflowID("workflow-1"), scheduledTime);
        SlotID id2 = new SlotID(new WorkflowID("workflow-2"), scheduledTime);
        SlotID id3 = new SlotID(new WorkflowID("workflow-3"), scheduledTime);
        SlotID id5 = new SlotID(new WorkflowID("workflow-5"), scheduledTime);
        SlotID id8 = new SlotID(new WorkflowID("workflow-8"), scheduledTime);
        SlotID id9 = new SlotID(new WorkflowID("workflow-9"), scheduledTime);

        Assert.assertEquals(Sets.newHashSet(id2, id3, id5, id8, id9), scheduler.getDownstreamSlots(id1, true));
    }

    @Test
    public void testDownstreamFindDependentSlotsDifferentSchedule() throws Exception {

        ScheduledTime scheduledTime = new ScheduledTime("2014-03-10T00:00:00.000Z");

        SlotID id6 = new SlotID(new WorkflowID("workflow-6-daily"), scheduledTime);
        SlotID id7 = new SlotID(new WorkflowID("workflow-7-depend-daily"), scheduledTime);

        Set<SlotID> idSet5 = scheduler.getDownstreamSlots(id6, false);
        Assert.assertEquals(idSet5, Sets.newHashSet(id7));

        SlotID id5_1 = new SlotID(new WorkflowID("workflow-5"), scheduledTime);
        Set<SlotID> idSet5_1 = scheduler.getDownstreamSlots(id5_1, false);
        Assert.assertEquals(idSet5_1, Sets.newHashSet(id6));

        SlotID id5_2 = new SlotID(new WorkflowID("workflow-5"), scheduledTime.plusHours(1));
        Set<SlotID> idSet5_2 = scheduler.getDownstreamSlots(id5_2, false);
        Assert.assertEquals(idSet5_2, Sets.newHashSet());
    }

    @Test
    public void testDownstreamTimeDoesntAlign() throws Exception {

        ScheduledTime scheduledTime = new ScheduledTime("2014-03-10T12:00:01.000Z");

        SlotID id1 = new SlotID(new WorkflowID("workflow-1"), scheduledTime);
        Set<SlotID> idSet2 = scheduler.getDownstreamSlots(id1, false);
        Assert.assertEquals(idSet2.size(), 0);
    }
}