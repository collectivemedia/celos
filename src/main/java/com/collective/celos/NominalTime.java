package com.collective.celos;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Nominal time of a workflow run in UTC.
 */
public class NominalTime extends ValueObject {
    
    protected final DateTime dateTime;
    
    public NominalTime(String formattedDate) {
        this(DateTime.parse(formattedDate));
    }
    
    public NominalTime(DateTime dateTime) {
        this.dateTime = Util.requireNonNull(dateTime);
        if (!dateTime.getZone().equals(DateTimeZone.UTC)) {
            throw new IllegalArgumentException("Nominal time must be in UTC, but isn't: " + dateTime);
        }
    }
    
}
