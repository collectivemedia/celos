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
package com.collective.celos.old;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.google.common.collect.*;
import org.junit.Assert;
import org.junit.Test;

import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import com.collective.celos.WorkflowInfo;
import com.collective.celos.WorkflowStatus;
import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;

public class UIServletTest {

    @Test
    public void testGetFirstTileTime() {
        ScheduledTime t = new ScheduledTime("2015-09-02T20:19:23Z");
        Assert.assertEquals(new ScheduledTime("2015-09-02T00:00Z"), UIServlet.getFirstTileTime(t, 60*24));
        Assert.assertEquals(new ScheduledTime("2015-09-02T18:00Z"), UIServlet.getFirstTileTime(t, 60*6));
        Assert.assertEquals(new ScheduledTime("2015-09-02T20:00Z"), UIServlet.getFirstTileTime(t, 60*2));
        Assert.assertEquals(new ScheduledTime("2015-09-02T20:00Z"), UIServlet.getFirstTileTime(t, 60));
        Assert.assertEquals(new ScheduledTime("2015-09-02T20:15Z"), UIServlet.getFirstTileTime(t, 15));
        Assert.assertEquals(new ScheduledTime("2015-09-02T20:15Z"), UIServlet.getFirstTileTime(t, 5));
        Assert.assertEquals(new ScheduledTime("2015-09-02T20:19Z"), UIServlet.getFirstTileTime(t, 1));
        // Test full day
        Assert.assertEquals(new ScheduledTime("2015-09-02T00:00Z"), UIServlet.getFirstTileTime(new ScheduledTime("2015-09-02T00:00Z"), 60*24));
    }
    
    @Test
    public void testNumTiles() {
        // At zoom level of 1 day, display at most 7 tiles
        Assert.assertEquals(7, UIServlet.getNumTiles(60*24, 60*24*7, 48));
        // At zoom level of 1 hour, display at most 48 tiles
        Assert.assertEquals(48, UIServlet.getNumTiles(60, 60*24*7, 48));
        // At zoom level of 1 minute, display at most 48 tiles
        Assert.assertEquals(48, UIServlet.getNumTiles(1, 60*24*7, 48));
    }
    
    @Test
    public void testTileTimesSet1() {
        SortedSet<ScheduledTime> expected = 
                new TreeSet(ImmutableSet.of(new ScheduledTime("2015-08-06T18:00Z"), new ScheduledTime("2015-08-06T19:00Z"), new ScheduledTime("2015-08-06T20:00Z")));
        Assert.assertEquals(expected, UIServlet.getTileTimesSet(new ScheduledTime("2015-08-06T20:00Z"), 60, 1000, 3));
    }
    
    @Test
    public void testTileTimesSet2() {
        SortedSet<ScheduledTime> expected = 
                new TreeSet(ImmutableSet.of(new ScheduledTime("2015-08-06T19:50Z"), new ScheduledTime("2015-08-06T19:55Z"), new ScheduledTime("2015-08-06T20:00Z")));
        Assert.assertEquals(expected, UIServlet.getTileTimesSet(new ScheduledTime("2015-08-06T20:00Z"), 5, 1000, 3));
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
        Assert.assertEquals(expected, UIServlet.bucketSlotsByTime(states, UIServlet.getTileTimesSet(new ScheduledTime("2015-08-06T19:30Z"), 5, 1000, 2)));    
    }
    
    @Test
    public void testZoomLevelParam() {
        Assert.assertEquals(UIServlet.DEFAULT_ZOOM_LEVEL_MINUTES, UIServlet.getZoomLevel(null));
        Assert.assertEquals(12, UIServlet.getZoomLevel("12"));
        Assert.assertEquals(UIServlet.MAX_ZOOM_LEVEL_MINUTES, UIServlet.getZoomLevel("1000000"));
        Assert.assertEquals(UIServlet.MIN_ZOOM_LEVEL_MINUTES, UIServlet.getZoomLevel("-1"));
    }
    
    @Test
    public void testTileClass() {
        WorkflowID id = new WorkflowID("foo");
        SlotState succ = new SlotState(new SlotID(id, new ScheduledTime("2015-08-06T19:27Z")), SlotState.Status.SUCCESS);
        SlotState fail = new SlotState(new SlotID(id, new ScheduledTime("2015-08-06T19:31Z")), SlotState.Status.FAILURE);
        SlotState wait = new SlotState(new SlotID(id, new ScheduledTime("2015-08-06T19:35Z")), SlotState.Status.WAITING);

        Assert.assertEquals("SUCCESS", UIServlet.printTileClass(ImmutableSet.of(succ)));
        Assert.assertEquals("SUCCESS", UIServlet.printTileClass(ImmutableSet.of(succ, succ, succ)));
        Assert.assertEquals("WAITING", UIServlet.printTileClass(ImmutableSet.of(succ, wait, succ)));
        Assert.assertEquals("WAITING", UIServlet.printTileClass(ImmutableSet.of(wait)));
        Assert.assertEquals("WAITING", UIServlet.printTileClass(ImmutableSet.of(wait, wait, wait)));
        Assert.assertEquals("FAILURE", UIServlet.printTileClass(ImmutableSet.of(fail)));
        Assert.assertEquals("FAILURE", UIServlet.printTileClass(ImmutableSet.of(succ, fail)));
        Assert.assertEquals("FAILURE", UIServlet.printTileClass(ImmutableSet.of(succ, fail, wait)));
        Assert.assertEquals("FAILURE", UIServlet.printTileClass(ImmutableSet.of(wait, wait, fail, wait, succ)));
    }
    
    @Test
    public void testRender() throws Exception {
        ScheduledTime end = new ScheduledTime("2015-09-03T13:17Z");
        ScheduledTime start = new ScheduledTime("2015-09-03T13:11Z");
        NavigableSet<ScheduledTime> tileTimes = new TreeSet<>(ImmutableSet.of(new ScheduledTime("2015-09-03T13:10Z"), new ScheduledTime("2015-09-03T13:15Z")));
        WorkflowID id = new WorkflowID("foo");
        List<WorkflowGroup> groups = ImmutableList.of(new WorkflowGroup("All workflows", ImmutableList.of(id)));
        WorkflowInfo workflowInfo = new WorkflowInfo(new URL("http://example.com"), ImmutableList.of());
        SlotState state1 = new SlotState(new SlotID(id, new ScheduledTime("2015-09-03T13:16Z")), SlotState.Status.FAILURE);
        SlotState state2 = new SlotState(new SlotID(id, new ScheduledTime("2015-09-03T13:12Z")), SlotState.Status.WAITING);
        List<SlotState> slotStates = ImmutableList.of(state1, state2);
        Map<WorkflowID, WorkflowStatus> statuses = ImmutableMap.of(id, new WorkflowStatus(workflowInfo, slotStates, false));
        Configuration conf = new Configuration(start, end, tileTimes, groups, statuses, new URL("http://example.com"));
        
        StringWebResponse response = new StringWebResponse(UIServlet.render(conf), new URL("http://example.com"));
        WebClient webClient = new WebClient();
        webClient.setThrowExceptionOnFailingStatusCode(false);
        HtmlPage page = HTMLParser.parse(response, new TopLevelWindow("top", webClient));
        
        // Some basic sanity checking
        
        List<HtmlTableDataCell> slotCells = (List<HtmlTableDataCell>) page.getByXPath("//td[contains(@class, 'slot')]");
        Assert.assertEquals("fail", slotCells.get(0).getTextContent());
        Assert.assertEquals("wait", slotCells.get(1).getTextContent());
        
        List<HtmlTableDataCell> hourCells = (List<HtmlTableDataCell>) page.getByXPath("//td[contains(@class, 'hour')]");
        Assert.assertEquals("1315", hourCells.get(0).getTextContent());
        Assert.assertEquals("1310", hourCells.get(1).getTextContent());
        
        List<HtmlTableDataCell> workflowCells = (List<HtmlTableDataCell>) page.getByXPath("//td[@class='workflow']");
        Assert.assertEquals("foo", workflowCells.get(0).getTextContent());
        
        System.out.println(response.getContentAsString());
    }

    @Test
    public void testUIConfig() throws IOException {
        UIServlet uiServlet = new UIServlet();

        String input = "{\n" +
                "  \"groups\": [\n" +
                "    {\n" +
                "      \"name\": \"Group1\",\n" +
                "      \"workflows\": [\n" +
                "        \"Group1-1\",\n" +
                "        \"Group1-2\",\n" +
                "        \"Group1-3\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"Group2\",\n" +
                "      \"workflows\": [\n" +
                "        \"Group2-1\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"Group3\",\n" +
                "      \"workflows\": [\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";

        List<WorkflowGroup> groups = uiServlet.getWorkflowGroups(new ByteArrayInputStream(input.getBytes()), new HashSet<>());

        Assert.assertEquals(groups.size(), 3);

        WorkflowGroup group1 = groups.get(0);
        List<WorkflowID> exp1 = new ArrayList<>();
        exp1.add(new WorkflowID("Group1-1"));
        exp1.add(new WorkflowID("Group1-2"));
        exp1.add(new WorkflowID("Group1-3"));
        Assert.assertEquals(group1.getWorkflows(), exp1);
        Assert.assertEquals(group1.getName(), "Group1");

        WorkflowGroup group2 = groups.get(1);
        List<WorkflowID> exp2 = new ArrayList<>();
        exp2.add(new WorkflowID("Group2-1"));
        Assert.assertEquals(group2.getWorkflows(), exp2);
        Assert.assertEquals(group2.getName(), "Group2");

        WorkflowGroup group3 = groups.get(2);
        List<WorkflowID> exp3 = new ArrayList<>();
        Assert.assertEquals(group3.getWorkflows(), exp3);
        Assert.assertEquals(group3.getName(), "Group3");
    }

    @Test
    public void testUIConfigMissingWorkflows() throws IOException {
        UIServlet uiServlet = new UIServlet();

        String input = "{\n" +
                "  \"groups\": [\n" +
                "    {\n" +
                "      \"name\": \"Group3\",\n" +
                "      \"workflows\": [\n" +
                "        \"Group3-1\",\n" +
                "        \"Group3-2\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";

        Set<WorkflowID> missingWfs = new HashSet<>();
        HashSet<WorkflowID> expectedWorkflows = new HashSet<>();
        expectedWorkflows.add(new WorkflowID("Group3-2"));

        WorkflowID wf1 = new WorkflowID("MissingWf1");
        WorkflowID wf2 = new WorkflowID("MissingWf2");
        WorkflowID wf3 = new WorkflowID("MissingWf3");

        missingWfs.add(wf1);
        missingWfs.add(wf3);
        missingWfs.add(wf2);

        expectedWorkflows.addAll(missingWfs);
        List<WorkflowGroup> groups = uiServlet.getWorkflowGroups(new ByteArrayInputStream(input.getBytes()), expectedWorkflows);

        Assert.assertEquals(groups.size(), 2);

        WorkflowGroup group1 = groups.get(0);
        List<WorkflowID> exp1 = new ArrayList<>();
        exp1.add(new WorkflowID("Group3-1"));
        exp1.add(new WorkflowID("Group3-2"));
        Assert.assertEquals(group1.getWorkflows(), exp1);
        Assert.assertEquals(group1.getName(), "Group3");

        WorkflowGroup missingGroup = groups.get(1);
        exp1.add(new WorkflowID("Group3-1"));
        List<WorkflowID> missingWfsSortedList = new ArrayList<>();
        missingWfsSortedList.add(wf1);
        missingWfsSortedList.add(wf2);
        missingWfsSortedList.add(wf3);

        Assert.assertEquals(missingGroup.getWorkflows(), missingWfsSortedList);
        Assert.assertEquals(missingGroup.getName(), "Unlisted workflows");
    }

}
