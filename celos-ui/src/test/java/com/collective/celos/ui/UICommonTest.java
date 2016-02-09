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

package com.collective.celos.ui;

import com.collective.celos.*;
import com.collective.celos.pojo.Workflow;
import com.collective.celos.pojo.WorkflowGroup;
import com.google.common.collect.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static com.collective.celos.ui.UICommon.getWorkflowGroups;
import static com.collective.celos.ui.UICommon.printTileClass;

public class UICommonTest {

    final private String TEST_WORKFLOW_NAME = "one";
    final private Workflow TEST_WORKFLOW_ID = new Workflow(TEST_WORKFLOW_NAME);

    @Test
    public void testGetFirstTileTime() {
        ScheduledTime t = new ScheduledTime("2015-09-02T20:19:23Z");
        Assert.assertEquals(new ScheduledTime("2015-09-02T00:00Z"), UICommon.getFirstTileTime(t, 60*24));
        Assert.assertEquals(new ScheduledTime("2015-09-02T18:00Z"), UICommon.getFirstTileTime(t, 60*6));
        Assert.assertEquals(new ScheduledTime("2015-09-02T20:00Z"), UICommon.getFirstTileTime(t, 60*2));
        Assert.assertEquals(new ScheduledTime("2015-09-02T20:00Z"), UICommon.getFirstTileTime(t, 60));
        Assert.assertEquals(new ScheduledTime("2015-09-02T20:15Z"), UICommon.getFirstTileTime(t, 15));
        Assert.assertEquals(new ScheduledTime("2015-09-02T20:15Z"), UICommon.getFirstTileTime(t, 5));
        Assert.assertEquals(new ScheduledTime("2015-09-02T20:19Z"), UICommon.getFirstTileTime(t, 1));
        // Test full day
        Assert.assertEquals(new ScheduledTime("2015-09-02T00:00Z"), UICommon.getFirstTileTime(new ScheduledTime("2015-09-02T00:00Z"), 60*24));
    }

    @Test
    public void testNumTiles() {
        // At zoom level of 1 day, display at most 7 tiles
        Assert.assertEquals(7, UICommon.getNumTiles(60*24, 60*24*7, 48));
        // At zoom level of 1 hour, display at most 48 tiles
        Assert.assertEquals(48, UICommon.getNumTiles(60, 60*24*7, 48));
        // At zoom level of 1 minute, display at most 48 tiles
        Assert.assertEquals(48, UICommon.getNumTiles(1, 60*24*7, 48));
    }

    @Test
    public void testBucketByTime() {
        WorkflowID id = new WorkflowID("foo");
        SlotState s1 = new SlotState(new SlotID(id, new ScheduledTime("2015-08-06T19:27Z")), SlotState.Status.SUCCESS);
        SlotState s2 = new SlotState(new SlotID(id, new ScheduledTime("2015-08-06T19:31Z")), SlotState.Status.SUCCESS);
        SlotState s3 = new SlotState(new SlotID(id, new ScheduledTime("2015-08-06T19:34Z")), SlotState.Status.SUCCESS);
        List<SlotState> states = ImmutableList.of(s1, s2, s3);
        Set<SlotState> bucket1 = ImmutableSet.of(s1);
        Set<SlotState> bucket2 = ImmutableSet.of(s2, s3);
        Map<ScheduledTime, Set<SlotState>> expected =
                ImmutableMap.of(new ScheduledTime("2015-08-06T19:25Z"), bucket1, new ScheduledTime("2015-08-06T19:30Z"), bucket2);
        Assert.assertEquals(expected, UICommon.bucketSlotsByTime(states, UICommon.getTileTimesSet(new ScheduledTime("2015-08-06T19:30Z"), 5, 1000, 2)));
    }

    @Test
    public void testZoomLevelParam() {
        Assert.assertEquals(UICommon.DEFAULT_ZOOM_LEVEL_MINUTES, UICommon.getZoomLevel(null));
        Assert.assertEquals(12, UICommon.getZoomLevel("12"));
        Assert.assertEquals(UICommon.MAX_ZOOM_LEVEL_MINUTES, UICommon.getZoomLevel("1000000"));
        Assert.assertEquals(UICommon.MIN_ZOOM_LEVEL_MINUTES, UICommon.getZoomLevel("-1"));
    }

    @Test
    public void testTileClass() {
        WorkflowID id = new WorkflowID("foo");
        SlotState succ = new SlotState(new SlotID(id, new ScheduledTime("2015-08-06T19:27Z")), SlotState.Status.SUCCESS);
        SlotState fail = new SlotState(new SlotID(id, new ScheduledTime("2015-08-06T19:31Z")), SlotState.Status.FAILURE);
        SlotState wait = new SlotState(new SlotID(id, new ScheduledTime("2015-08-06T19:35Z")), SlotState.Status.WAITING);

        Assert.assertEquals("SUCCESS", printTileClass(ImmutableList.of(succ)));
        Assert.assertEquals("SUCCESS", printTileClass(ImmutableList.of(succ, succ, succ)));
        Assert.assertEquals("WAITING", printTileClass(ImmutableList.of(succ, wait, succ)));
        Assert.assertEquals("WAITING", printTileClass(ImmutableList.of(wait)));
        Assert.assertEquals("WAITING", printTileClass(ImmutableList.of(wait, wait, wait)));
        Assert.assertEquals("FAILURE", printTileClass(ImmutableList.of(fail)));
        Assert.assertEquals("FAILURE", printTileClass(ImmutableList.of(succ, fail)));
        Assert.assertEquals("FAILURE", printTileClass(ImmutableList.of(succ, fail, wait)));
        Assert.assertEquals("FAILURE", printTileClass(ImmutableList.of(wait, wait, fail, wait, succ)));
    }

    @Test
    public void testGetWorkflowGroups0() throws Exception {
        final List<WorkflowGroup> result = UICommon.getWorkflowGroups(Optional.empty(), Collections.singletonList(TEST_WORKFLOW_ID));
        final List<Workflow> tmp = Collections.singletonList(new Workflow(TEST_WORKFLOW_NAME));
        final List<WorkflowGroup> check = Collections.singletonList(new WorkflowGroup(UICommon.DEFAULT_CAPTION).withRows(tmp));
        Assert.assertEquals(check, result);
    }

    @Test
    public void testGetWorkflowGroups1() throws Exception {
        final String config = "{" +
                "\"groups\": [{" +
                "\"name\": \"Flume\"," +
                "\"workflows\": [" +
                "\"flume-ready-dc3\"," +
                "\"flume-ready-sv4\"," +
                "\"flume-tmp-file-closer\"]" +
                "}]" +
                "}";
        final List<WorkflowGroup> result = UICommon.getWorkflowGroups(Optional.of(config), Collections.singletonList(TEST_WORKFLOW_ID));
        String check = "[ {" +
                "  \"name\" : \"Flume\"," +
                "  \"times\" : [ ]," +
                "  \"days\" : [ ]," +
                "  \"rows\" : [ {" +
                "    \"workflowName\" : \"flume-ready-dc3\"," +
                "    \"rows\" : [ ]" +
                "  }, {" +
                "    \"workflowName\" : \"flume-ready-sv4\"," +
                "    \"rows\" : [ ]" +
                "  }, {" +
                "    \"workflowName\" : \"flume-tmp-file-closer\"," +
                "    \"rows\" : [ ]" +
                "  } ]" +
                "}, {" +
                "  \"name\" : \"Unlisted workflows\"," +
                "  \"times\" : [ ]," +
                "  \"days\" : [ ]," +
                "  \"rows\" : [ {" +
                "    \"workflowName\" : \"one\"," +
                "    \"rows\" : [ ]" +
                "  } ]" +
                "} ]";
        Assert.assertEquals(Util.MAPPER.readTree(check), Util.MAPPER.valueToTree(result));

    }

    @Test
    public void testTileTimesSet1() {
        final SortedSet<ScheduledTime> expected = new TreeSet<>();
        expected.add(new ScheduledTime("2015-08-06T18:00Z"));
        expected.add(new ScheduledTime("2015-08-06T19:00Z"));
        expected.add(new ScheduledTime("2015-08-06T20:00Z"));
        Assert.assertEquals(expected, UICommon.getTileTimesSet(new ScheduledTime("2015-08-06T20:00Z"), 60, 1000, 3));
    }

    @Test
    public void testTileTimesSet2() {
        final SortedSet<ScheduledTime> expected = new TreeSet<>();
        expected.add(new ScheduledTime("2015-08-06T19:50Z"));
        expected.add(new ScheduledTime("2015-08-06T19:55Z"));
        expected.add(new ScheduledTime("2015-08-06T20:00Z"));
        Assert.assertEquals(expected, UICommon.getTileTimesSet(new ScheduledTime("2015-08-06T20:00Z"), 5, 1000, 3));
    }


    @Test
    public void testUIConfig() throws IOException {
        String input = "{" +
                "  \"groups\": [" +
                "    {" +
                "      \"name\": \"Group1\"," +
                "      \"workflows\": [" +
                "        \"Group1-1\"," +
                "        \"Group1-2\"," +
                "        \"Group1-3\"" +
                "      ]" +
                "    }," +
                "    {" +
                "      \"name\": \"Group2\"," +
                "      \"workflows\": [" +
                "        \"Group2-1\"" +
                "      ]" +
                "    }," +
                "    {" +
                "      \"name\": \"Group3\"," +
                "      \"workflows\": [" +
                "      ]" +
                "    }" +
                "  ]" +
                "}";

        List<WorkflowGroup> groups = getWorkflowGroups(Optional.of(input), new ArrayList<>());

        Assert.assertEquals(groups.size(), 3);

        WorkflowGroup group1 = groups.get(0);
        List<Workflow> exp1 = new ArrayList<>();
        exp1.add(new Workflow("Group1-1"));
        exp1.add(new Workflow("Group1-2"));
        exp1.add(new Workflow("Group1-3"));
        Assert.assertEquals(group1.getRows(), exp1);
        Assert.assertEquals(group1.getName(), "Group1");

        WorkflowGroup group2 = groups.get(1);
        List<Workflow> exp2 = new ArrayList<>();
        exp2.add(new Workflow("Group2-1"));
        Assert.assertEquals(group2.getRows(), exp2);
        Assert.assertEquals(group2.getName(), "Group2");

        WorkflowGroup group3 = groups.get(2);
        List<Workflow> exp3 = new ArrayList<>();
        Assert.assertEquals(group3.getRows(), exp3);
        Assert.assertEquals(group3.getName(), "Group3");
    }

    @Test
    public void testUIConfigMissingWorkflows() throws IOException {

        String input = "{" +
                "  \"groups\": [" +
                "    {" +
                "      \"name\": \"Group3\"," +
                "      \"workflows\": [" +
                "        \"Group3-1\"," +
                "        \"Group3-2\"" +
                "      ]" +
                "    }" +
                "  ]" +
                "}";

        Set<Workflow> missingWfs = new HashSet<>();
        List<Workflow> expectedWorkflows = new ArrayList<>();
        expectedWorkflows.add(new Workflow("Group3-2"));

        Workflow wf1 = new Workflow("MissingWf1");
        Workflow wf2 = new Workflow("MissingWf2");
        Workflow wf3 = new Workflow("MissingWf3");

        missingWfs.add(wf1);
        missingWfs.add(wf3);
        missingWfs.add(wf2);

        expectedWorkflows.addAll(missingWfs);
        List<WorkflowGroup> groups = getWorkflowGroups(Optional.of(input), expectedWorkflows);

        Assert.assertEquals(groups.size(), 2);

        WorkflowGroup group1 = groups.get(0);
        List<Workflow> exp1 = new ArrayList<>();
        exp1.add(new Workflow("Group3-1"));
        exp1.add(new Workflow("Group3-2"));
        Assert.assertEquals(group1.getRows(), exp1);
        Assert.assertEquals(group1.getName(), "Group3");

        WorkflowGroup missingGroup = groups.get(1);
        exp1.add(new Workflow("Group3-1"));
        List<Workflow> missingWfsSortedList = new ArrayList<>();
        missingWfsSortedList.add(wf1);
        missingWfsSortedList.add(wf2);
        missingWfsSortedList.add(wf3);

        final String valueAsString1 = Util.JSON_PRETTY.writeValueAsString(missingGroup.getRows());
        System.out.println(valueAsString1);
        System.out.println("--------------");
        final String valueAsString2 = Util.JSON_PRETTY.writeValueAsString(missingWfsSortedList);
        System.out.println(valueAsString2);

        Assert.assertEquals(missingGroup.getRows(), missingWfsSortedList);
        Assert.assertEquals(missingGroup.getName(), "Unlisted workflows");
    }

}