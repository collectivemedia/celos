package com.collective.celos;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

public class CronSchedule implements Schedule {

    private CronExpression cronExpression;

    public CronSchedule(String cronConfig) {
        try {
            CronExpression.validateExpression(cronConfig);
            cronExpression = new CronExpression(cronConfig);
            cronExpression.setTimeZone(DateTimeZone.UTC.toTimeZone());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Error in cron expression", e);
        }
    }

    public String getCronExpression() {
        return cronExpression.getCronExpression();
    }

    @Override
    public SortedSet<ZonedDateTime> getScheduledTimes(Scheduler scheduler, ZonedDateTime start, ZonedDateTime end) {
        SortedSet<ZonedDateTime> zonedDateTimes = new TreeSet<ZonedDateTime>();
        DateTime startDT = new DateTime(start.toString());
        DateTime endDT = new DateTime(end.toString());

        if (!startDT.isBefore(endDT)) {
            return zonedDateTimes;
        }
        
        if (cronExpression.isSatisfiedBy(startDT.toDate())) {
            zonedDateTimes.add(start);
        }

        DateTime candidate = startDT;
        while((candidate = getNextDateTime(candidate)) != null && candidate.isBefore(endDT)) {
            // FIXME
            zonedDateTimes.add(ZonedDateTime.parse(candidate.toString()));
        }
        
        return zonedDateTimes;
    }

    private DateTime getNextDateTime(DateTime candidate) {
        Date date = cronExpression.getNextValidTimeAfter(candidate.toDate());
        if (date != null) {
            return new DateTime(date, DateTimeZone.UTC);
        } else {
            return null;
        }
    }

}
