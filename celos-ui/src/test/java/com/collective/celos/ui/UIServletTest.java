package com.collective.celos.ui;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class UIServletTest {

    @Test
    public void testGetFirstTileTime() {
        ScheduledTime t = new ScheduledTime("2015-09-02T20:19:23Z");
        Assert.assertEquals(new ScheduledTime("2015-09-02T00:00Z"), CelosUIServlet.getFirstTileTime(t, 60*24));
        Assert.assertEquals(new ScheduledTime("2015-09-02T18:00Z"), CelosUIServlet.getFirstTileTime(t, 60*6));
        Assert.assertEquals(new ScheduledTime("2015-09-02T20:00Z"), CelosUIServlet.getFirstTileTime(t, 60*2));
        Assert.assertEquals(new ScheduledTime("2015-09-02T20:00Z"), CelosUIServlet.getFirstTileTime(t, 60));
        Assert.assertEquals(new ScheduledTime("2015-09-02T20:15Z"), CelosUIServlet.getFirstTileTime(t, 15));
        Assert.assertEquals(new ScheduledTime("2015-09-02T20:15Z"), CelosUIServlet.getFirstTileTime(t, 5));
        Assert.assertEquals(new ScheduledTime("2015-09-02T20:19Z"), CelosUIServlet.getFirstTileTime(t, 1));
        // Test full day
        Assert.assertEquals(new ScheduledTime("2015-09-02T00:00Z"), CelosUIServlet.getFirstTileTime(new ScheduledTime("2015-09-02T00:00Z"), 60*24));
    }
    
    @Test
    public void testNumTiles() {
        // At zoom level of 1 day, display at most 7 tiles
        Assert.assertEquals(7, CelosUIServlet.getNumTiles(60*24, 60*24*7, 48));
        // At zoom level of 1 hour, display at most 48 tiles
        Assert.assertEquals(48, CelosUIServlet.getNumTiles(60, 60*24*7, 48));
        // At zoom level of 1 minute, display at most 48 tiles
        Assert.assertEquals(48, CelosUIServlet.getNumTiles(1, 60*24*7, 48));
    }
    
    @Test
    public void testTileTimesSet1() {
        SortedSet<ScheduledTime> expected = 
                new TreeSet(ImmutableSet.of(new ScheduledTime("2015-08-06T18:00Z"), new ScheduledTime("2015-08-06T19:00Z"), new ScheduledTime("2015-08-06T20:00Z")));
        Assert.assertEquals(expected, CelosUIServlet.getTileTimesSet(new ScheduledTime("2015-08-06T20:00Z"), 60, 1000, 3));
    }
    
    @Test
    public void testTileTimesSet2() {
        SortedSet<ScheduledTime> expected = 
                new TreeSet(ImmutableSet.of(new ScheduledTime("2015-08-06T19:50Z"), new ScheduledTime("2015-08-06T19:55Z"), new ScheduledTime("2015-08-06T20:00Z")));
        Assert.assertEquals(expected, CelosUIServlet.getTileTimesSet(new ScheduledTime("2015-08-06T20:00Z"), 5, 1000, 3));
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
        Assert.assertEquals(expected, CelosUIServlet.bucketByTime(states, CelosUIServlet.getTileTimesSet(new ScheduledTime("2015-08-06T19:30Z"), 5, 1000, 2)));    
    }
    
    @Test
    public void testZoomLevelParam() {
        Assert.assertEquals(CelosUIServlet.DEFAULT_ZOOM_LEVEL_MINUTES, CelosUIServlet.getZoomLevel(null));
        Assert.assertEquals(12, CelosUIServlet.getZoomLevel("12"));
        Assert.assertEquals(CelosUIServlet.MAX_ZOOM_LEVEL_MINUTES, CelosUIServlet.getZoomLevel("1000000"));
        Assert.assertEquals(CelosUIServlet.MIN_ZOOM_LEVEL_MINUTES, CelosUIServlet.getZoomLevel("-1"));
    }
    
    @Test
    public void testTileClass() {
        WorkflowID id = new WorkflowID("foo");
        SlotState succ = new SlotState(new SlotID(id, new ScheduledTime("2015-08-06T19:27Z")), SlotState.Status.SUCCESS);
        SlotState fail = new SlotState(new SlotID(id, new ScheduledTime("2015-08-06T19:31Z")), SlotState.Status.FAILURE);
        SlotState wait = new SlotState(new SlotID(id, new ScheduledTime("2015-08-06T19:35Z")), SlotState.Status.WAITING);

        Assert.assertEquals("SUCCESS", CelosUIServlet.printTileClass(ImmutableSet.of(succ)));
        Assert.assertEquals("SUCCESS", CelosUIServlet.printTileClass(ImmutableSet.of(succ, succ, succ)));
        Assert.assertEquals("WAITING", CelosUIServlet.printTileClass(ImmutableSet.of(succ, wait, succ)));
        Assert.assertEquals("WAITING", CelosUIServlet.printTileClass(ImmutableSet.of(wait)));
        Assert.assertEquals("WAITING", CelosUIServlet.printTileClass(ImmutableSet.of(wait, wait, wait)));
        Assert.assertEquals("FAILURE", CelosUIServlet.printTileClass(ImmutableSet.of(fail)));
        Assert.assertEquals("FAILURE", CelosUIServlet.printTileClass(ImmutableSet.of(succ, fail)));
        Assert.assertEquals("FAILURE", CelosUIServlet.printTileClass(ImmutableSet.of(succ, fail, wait)));
        Assert.assertEquals("FAILURE", CelosUIServlet.printTileClass(ImmutableSet.of(wait, wait, fail, wait, succ)));
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
        Map<WorkflowID, WorkflowStatus> statuses = ImmutableMap.of(id, new WorkflowStatus(workflowInfo, slotStates));
        UIConfiguration conf = new UIConfiguration(start, end, tileTimes, groups, statuses);
        
        StringWebResponse response = new StringWebResponse(CelosUIServlet.render(conf), new URL("http://example.com"));
        HtmlPage page = HTMLParser.parse(response, new TopLevelWindow("top", new WebClient()));
        
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
    
}
