package com.collective.celos.ui;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class UIServletTest {

    @Test
    public void testBucketTime() {
        ScheduledTime now = new ScheduledTime("2015-08-06T21:21:21Z");
        Assert.assertEquals(new ScheduledTime("2015-08-07T00:00:00Z"), CelosUIServlet.bucketTime(now, 60*24));
        Assert.assertEquals(new ScheduledTime("2015-08-06T22:00:00Z"), CelosUIServlet.bucketTime(now, 60));
        Assert.assertEquals(new ScheduledTime("2015-08-06T21:30:00Z"), CelosUIServlet.bucketTime(now, 30));
        Assert.assertEquals(new ScheduledTime("2015-08-06T21:30:00Z"), CelosUIServlet.bucketTime(now, 15));
        Assert.assertEquals(new ScheduledTime("2015-08-06T21:30:00Z"), CelosUIServlet.bucketTime(now, 10));
        Assert.assertEquals(new ScheduledTime("2015-08-06T21:25:00Z"), CelosUIServlet.bucketTime(now, 5));
        Assert.assertEquals(new ScheduledTime("2015-08-06T21:22:00Z"), CelosUIServlet.bucketTime(now, 1));
        // Test full minute
        Assert.assertEquals(new ScheduledTime("2015-08-06T21:21:00Z"), CelosUIServlet.bucketTime(new ScheduledTime("2015-08-06T21:21:00Z"), 1));
        // Test full day
        Assert.assertEquals(new ScheduledTime("2015-08-06T00:00:00Z"), CelosUIServlet.bucketTime(new ScheduledTime("2015-08-06T00:00:00Z"), 60*24));
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
    public void testTileTimes1() {
        List<ScheduledTime> expected = 
                ImmutableList.of(new ScheduledTime("2015-08-06T20:00Z"), new ScheduledTime("2015-08-06T19:00Z"), new ScheduledTime("2015-08-06T18:00Z"));
        Assert.assertEquals(expected, CelosUIServlet.getTileTimes(new ScheduledTime("2015-08-06T19:31Z"), 60, 1000, 3));
    }
    
    @Test
    public void testTileTimes2() {
        List<ScheduledTime> expected = 
                ImmutableList.of(new ScheduledTime("2015-08-06T19:35Z"), new ScheduledTime("2015-08-06T19:30Z"), new ScheduledTime("2015-08-06T19:25Z"));
        Assert.assertEquals(expected, CelosUIServlet.getTileTimes(new ScheduledTime("2015-08-06T19:31Z"), 5, 1000, 3));
    }
    
    @Test
    public void testBucketSlotsByTime() {
        WorkflowID id = new WorkflowID("foo");
        SlotState s1 = new SlotState(new SlotID(id, new ScheduledTime("2015-08-06T19:27Z")), SlotState.Status.SUCCESS);
        SlotState s2 = new SlotState(new SlotID(id, new ScheduledTime("2015-08-06T19:31Z")), SlotState.Status.SUCCESS);
        SlotState s3 = new SlotState(new SlotID(id, new ScheduledTime("2015-08-06T19:35Z")), SlotState.Status.SUCCESS);
        List<SlotState> states = ImmutableList.of(s1, s2, s3);
        Set<SlotState> bucket1 = ImmutableSet.of(s1);
        Set<SlotState> bucket2 = ImmutableSet.of(s2, s3);
        Map<ScheduledTime, Set<SlotState>> expected =
            ImmutableMap.of(new ScheduledTime("2015-08-06T19:30Z"), bucket1, new ScheduledTime("2015-08-06T19:35Z"), bucket2);
        Assert.assertEquals(expected, CelosUIServlet.bucketSlotsByTime(states, 5));    
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
    
}
