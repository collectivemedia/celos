package com.collective.celos;

import org.joda.time.DateTime;

import com.collective.celos.api.ScheduledTime;
import com.collective.celos.api.Trigger;
import com.collective.celos.api.Util;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A trigger that signals data availability for a given scheduled time
 * only if it is a configurable number of seconds past the current time.
 * 
 * In combination with an AND trigger, this allows to delay the firing
 * of another trigger, for example to clean up data after a day.
 * 
 * The following example shows a trigger that only fires if the given
 * HDFS path is available, and the current time is one day after 
 * the workflow's scheduled time.
 * 
 * var oneDay = 60 * 60 * 24;
 * andTrigger(delayTrigger(oneDay), hdfsCheckTrigger("/${year}/${month}/${day}/..."))
*/
public class DelayTrigger implements Trigger {

    public static final String SECONDS_PROP = "celos.delayTrigger.seconds";

    private final int seconds;
    
    public DelayTrigger(ObjectNode properties) throws Exception {
        this.seconds = Util.getIntProperty(properties, SECONDS_PROP);
    }
    
    public boolean isDataAvailable(ScheduledTime now, ScheduledTime t) throws Exception {
        DateTime nowDT = now.getDateTime();
        DateTime waitUntilDT = t.getDateTime().plusSeconds(getSeconds());
        return nowDT.isAfter(waitUntilDT);
    }

    public int getSeconds() {
        return seconds;
    }

}
