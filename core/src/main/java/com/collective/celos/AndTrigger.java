package com.collective.celos;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.collective.celos.api.ScheduledTime;
import com.collective.celos.api.Trigger;
import com.collective.celos.api.Util;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/*
Trigger that combines multiple triggers and signals data availability only if all of them do.

Example that combines two HDFSCheckTriggers to wait for /foo and /bar paths in HDFS:

{
    "type": "com.collective.celos.AndTrigger",
    "properties": {
       "celos.andTrigger.triggers": [
           {
               "type": "com.collective.celos.HDFSCheckTrigger",
               "properties": {
                   "celos.hdfs.path": "/foo"
               }
           },
           {
               "type": "com.collective.celos.HDFSCheckTrigger",
               "properties": {
                   "celos.hdfs.path": "/bar"
               }
           }
       ]
    }
}
 */
public class AndTrigger implements Trigger {

    public static final String TRIGGERS_PROP = "celos.andTrigger.triggers";

    private static final Logger LOGGER = Logger.getLogger(AndTrigger.class);
    
    private final List<Trigger> triggers = new LinkedList<>();
    private final JSONInstanceCreator creator = new JSONInstanceCreator();
    
    public AndTrigger(ObjectNode properties) throws Exception {
        ArrayNode triggersArray = Util.getArrayProperty(properties, TRIGGERS_PROP);
        for (int i = 0; i < triggersArray.size(); i++) {
            LOGGER.info("Creating sub-trigger: " + i);
            triggers.add((Trigger) creator.createInstance(triggersArray.get(i)));
        }
    }
    
    @Override
    public boolean isDataAvailable(ScheduledTime now, ScheduledTime t) throws Exception {
        for (Trigger trigger : triggers) {
            if (!trigger.isDataAvailable(now, t)) {
                return false;
            }
        }
        return true;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

}
