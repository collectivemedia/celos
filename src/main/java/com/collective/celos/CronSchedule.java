package com.collective.celos;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.quartz.CronExpression;

import java.text.ParseException;
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
    public SortedSet<ScheduledTime> getScheduledTimes(Scheduler scheduler, ScheduledTime start, ScheduledTime end) {
        SortedSet<ScheduledTime> scheduledTimes = new TreeSet<>();
        DateTime startDT = start.getDateTime();
        DateTime endDT = end.getDateTime();

        if (!startDT.isBefore(endDT)) {
            return scheduledTimes;
        }
        
        if (cronExpression.isSatisfiedBy(startDT.toDate())) {
            scheduledTimes.add(start);
        }

        DateTime candidate = startDT;
        while((candidate = getNextDateTime(candidate)) != null && candidate.isBefore(endDT)) {
            scheduledTimes.add(new ScheduledTime(candidate));
        }
        
        return scheduledTimes;
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
