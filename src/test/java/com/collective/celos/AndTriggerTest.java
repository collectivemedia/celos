package com.collective.celos;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AndTriggerTest {

    @Test
    public void returnsTrueWhenAllSubTriggersReturnTrue() throws Exception {
        ArrayNode subTriggersArray = Util.newArrayNode();
        subTriggersArray.add(createAlwaysTriggerConfiguration());
        subTriggersArray.add(createAlwaysTriggerConfiguration());
        subTriggersArray.add(createAlwaysTriggerConfiguration());
        ObjectNode properties = Util.newObjectNode();
        properties.put(AndTrigger.TRIGGERS_PROP, subTriggersArray);
        AndTrigger trigger = new AndTrigger(properties);
        Assert.assertTrue(trigger.isDataAvailable(new ScheduledTime("2014-02-26T16:13:00Z")));
    }
    
    @Test
    public void returnsFalseWhenOnlyOneSubTriggerReturnsTrue() throws Exception {
        ArrayNode subTriggersArray = Util.newArrayNode();
        subTriggersArray.add(createNeverTriggerConfiguration());
        subTriggersArray.add(createAlwaysTriggerConfiguration());
        subTriggersArray.add(createNeverTriggerConfiguration());
        ObjectNode properties = Util.newObjectNode();
        properties.put(AndTrigger.TRIGGERS_PROP, subTriggersArray);
        AndTrigger trigger = new AndTrigger(properties);
        Assert.assertFalse(trigger.isDataAvailable(new ScheduledTime("2014-02-26T16:13:00Z")));
    }

    @Test
    public void returnsFalseWhenNoSubTriggersReturnsTrue() throws Exception {
        ArrayNode subTriggersArray = Util.newArrayNode();
        subTriggersArray.add(createNeverTriggerConfiguration());
        subTriggersArray.add(createNeverTriggerConfiguration());
        subTriggersArray.add(createNeverTriggerConfiguration());
        ObjectNode properties = Util.newObjectNode();
        properties.put(AndTrigger.TRIGGERS_PROP, subTriggersArray);
        AndTrigger trigger = new AndTrigger(properties);
        Assert.assertFalse(trigger.isDataAvailable(new ScheduledTime("2014-02-26T16:13:00Z")));
    }

    private ObjectNode createAlwaysTriggerConfiguration() {
        ObjectNode config = Util.newObjectNode();
        config.put("type", "com.collective.celos.AlwaysTrigger");
        return config;
    }
    
    private ObjectNode createNeverTriggerConfiguration() {
        ObjectNode config = Util.newObjectNode();
        config.put("type", "com.collective.celos.NeverTrigger");
        return config;
    }
    
}
