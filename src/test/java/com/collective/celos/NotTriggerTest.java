package com.collective.celos;

import junit.framework.Assert;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class NotTriggerTest {
    
    @Test(expected=IllegalArgumentException.class)
    public void failsWhenTriggerPropertyNotSet() throws Exception {
        ObjectNode properties = Util.newObjectNode();
        new NotTrigger(properties);
    }

    @Test(expected=IllegalArgumentException.class)
    public void failsWhenTriggerPropertyNotAnObject() throws Exception {
        ObjectNode properties = Util.newObjectNode();
        properties.put(NotTrigger.TRIGGER_PROP, 12);
        new NotTrigger(properties);
    }

    @Test
    public void invertsTrue() throws Exception {
        ObjectNode properties = Util.newObjectNode();
        properties.put(NotTrigger.TRIGGER_PROP, AndTriggerTest.createAlwaysTriggerConfiguration());
        Assert.assertFalse(new NotTrigger(properties).isDataAvailable(ScheduledTime.now(), ScheduledTime.now()));
    }
    
    @Test
    public void invertsFalse() throws Exception {
        ObjectNode properties = Util.newObjectNode();
        properties.put(NotTrigger.TRIGGER_PROP, AndTriggerTest.createNeverTriggerConfiguration());
        Assert.assertTrue(new NotTrigger(properties).isDataAvailable(ScheduledTime.now(), ScheduledTime.now()));
    }
    
}
