package com.collective.celos;

import org.apache.log4j.Logger;

import com.collective.celos.api.ScheduledTime;
import com.collective.celos.api.Trigger;
import com.collective.celos.api.Util;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class NotTrigger implements Trigger {

    public static final String TRIGGER_PROP = "celos.notTrigger.trigger";

    private static final Logger LOGGER = Logger.getLogger(NotTrigger.class);
    
    private final Trigger trigger;
    private final JSONInstanceCreator creator = new JSONInstanceCreator();
    
    public NotTrigger(ObjectNode properties) throws Exception {
        JsonNode triggerProps = Util.getObjectProperty(properties, TRIGGER_PROP);
        LOGGER.info("Creating sub-trigger");
        this.trigger = (Trigger) Util.requireNonNull(creator.createInstance(triggerProps));
    }
    
    @Override
    public boolean isDataAvailable(ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        return !trigger.isDataAvailable(now, scheduledTime);
    }

    public Trigger getTrigger() {
        return trigger;
    }

}
