package com.collective.celos;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Scheduled time of a workflow run in UTC.
 */
public class ScheduledTime extends ValueObject {
    
    protected final DateTime dateTime;
    
    public ScheduledTime(String formattedDate) {
        this(DateTime.parse(formattedDate));
    }
    
    public ScheduledTime(DateTime dateTime) {
        this.dateTime = Util.requireNonNull(dateTime);
        if (!dateTime.getZone().equals(DateTimeZone.UTC)) {
            throw new IllegalArgumentException("Scheduled time must be in UTC, but isn't: " + dateTime);
        }
    }
    
}
