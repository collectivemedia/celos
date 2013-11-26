package com.collective.celos;

import static org.junit.Assert.assertEquals;

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
    public void testReplaceTokensPadding() {
        ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
        String actual = formatter.replaceTimeTokens(
                "/${year}/${month}/${day}/${hour}", new ScheduledTime(
                        "0001-02-03T04:00Z"));
        assertEquals("/0001/02/03/04", actual);
    }

    public void testReplaceTokens(String input, String expected) {
        ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
        String actual = formatter.replaceTimeTokens(input, new ScheduledTime(
                "2013-11-22T14:00Z"));
        assertEquals(expected, actual);
    }

}
