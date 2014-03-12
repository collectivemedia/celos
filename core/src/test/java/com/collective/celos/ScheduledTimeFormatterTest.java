package com.collective.celos;

import static org.junit.Assert.assertEquals;

import com.collective.celos.exposed.ScheduledTime;
import org.junit.Test;

public class ScheduledTimeFormatterTest {

    @Test
    public void testReplaceYearTokens() {
        testReplaceTokens("/foo/${year}/bar", "/foo/2013/bar");
        testReplaceTokens("/foo/${year}/bar/${year}/baz",
                "/foo/2013/bar/2013/baz");
    }

    @Test
    public void testReplaceMonthTokens() {
        testReplaceTokens("/foo/${month}/bar", "/foo/11/bar");
        testReplaceTokens("/foo/${month}/bar/${month}/baz",
                "/foo/11/bar/11/baz");
    }

    @Test
    public void testReplaceDayTokens() {
        testReplaceTokens("/foo/${day}/bar", "/foo/22/bar");
        testReplaceTokens("/foo/${day}/bar/${day}/baz", "/foo/22/bar/22/baz");
    }

    @Test
    public void testReplaceHourTokens() {
        testReplaceTokens("/foo/${hour}/bar", "/foo/14/bar");
        testReplaceTokens("/foo/${hour}/bar/${hour}/baz", "/foo/14/bar/14/baz");
    }
    
    @Test
    public void testReplaceMinuteTokens() {
        testReplaceTokens("/foo/${minute}/bar", "/foo/00/bar");
        testReplaceTokens("/foo/${minute}/bar/${minute}/baz", "/foo/00/bar/00/baz");
    }
    
    @Test
    public void testReplaceSecondTokens() {
        testReplaceTokens("/foo/${second}/bar", "/foo/53/bar");
        testReplaceTokens("/foo/${second}/bar/${second}/baz", "/foo/53/bar/53/baz");
    }

    @Test
    public void testReplaceMillisecondTokens() {
        testReplaceTokens("/foo/${millisecond}/bar", "/foo/023/bar");
        testReplaceTokens("/foo/${millisecond}/bar/${millisecond}/baz", "/foo/023/bar/023/baz");
    }

    @Test
    public void millisecondPaddingTest() {
        ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
        assertEquals("001", formatter.formatMillisecond(new ScheduledTimeImpl("2014-02-25T01:02:03.001Z")));
        assertEquals("010", formatter.formatMillisecond(new ScheduledTimeImpl("2014-02-25T01:02:03.010Z")));
        assertEquals("100", formatter.formatMillisecond(new ScheduledTimeImpl("2014-02-25T01:02:03.100Z")));
    }
    
    @Test
    public void timestampTest() {
        ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
        assertEquals("20:15:00.053Z", formatter.formatTimestamp(new ScheduledTimeImpl("2014-02-25T20:15:00.053Z")));
    }
    
    @Test
    public void datestampTest() {
        ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
        assertEquals("2014-02-25", formatter.formatDatestamp(new ScheduledTimeImpl("2014-02-25T20:15:00.053Z")));
    }
    
    @Test
    public void testReplaceTokensPadding() {
        ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
        String actual = formatter.replaceTimeTokens(
                "/${year}/${month}/${day}/${hour}", new ScheduledTimeImpl(
                        "0001-02-03T04:00Z"));
        assertEquals("/0001/02/03/04", actual);
    }

    public void testReplaceTokens(String input, String expected) {
        ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
        String actual = formatter.replaceTimeTokens(input, new ScheduledTimeImpl(
                "2013-11-22T14:00:53.023Z"));
        assertEquals(expected, actual);
    }

}
