package com.collective.celos;

import org.junit.Assert;
import org.junit.Test;

import com.collective.celos.api.ScheduledTime;
import com.collective.celos.api.Trigger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DelayTriggerTest {
    
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void canBeConfiguredFromJSON() throws Exception {
        ObjectNode properties = mapper.createObjectNode();
        properties.put(DelayTrigger.SECONDS_PROP, 12);
        Assert.assertEquals(12, new DelayTrigger(properties).getSeconds());
    }

    @Test(expected=IllegalArgumentException.class)
    public void failsIfSecondsPropertyMissing() throws Exception {
        ObjectNode properties = mapper.createObjectNode();
        new DelayTrigger(properties);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void failsIfSecondsPropertyNotAnInt() throws Exception {
        ObjectNode properties = mapper.createObjectNode();
        properties.put(DelayTrigger.SECONDS_PROP, "foo");
        new DelayTrigger(properties);
    }
    
    @Test
    public void works() throws Exception {
        ScheduledTime now = new ScheduledTime("2014-01-01T05:00:00Z");
        ObjectNode properties = mapper.createObjectNode();
        properties.put(DelayTrigger.SECONDS_PROP, 60 * 60); // one hour
        Trigger t = new DelayTrigger(properties);
        Assert.assertTrue(t.isDataAvailable(now, new ScheduledTime("1980-01-01T03:59:00Z")));
        Assert.assertTrue(t.isDataAvailable(now, new ScheduledTime("2014-01-01T03:59:00Z")));
        Assert.assertTrue(t.isDataAvailable(now, new ScheduledTime("2014-01-01T03:59:59Z")));
        Assert.assertFalse(t.isDataAvailable(now, new ScheduledTime("2014-01-01T04:00:00Z")));
        Assert.assertFalse(t.isDataAvailable(now, new ScheduledTime("2014-01-01T05:00:00Z")));
        Assert.assertFalse(t.isDataAvailable(now, new ScheduledTime("2014-01-01T06:00:00Z")));
        Assert.assertFalse(t.isDataAvailable(now, new ScheduledTime("2080-01-01T06:00:00Z")));
    }
}
