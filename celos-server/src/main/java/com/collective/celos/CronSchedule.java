package com.collective.celos;

import java.text.ParseException;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.quartz.CronExpression;

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
        SortedSet<ScheduledTime> scheduledTimes = new TreeSet<ScheduledTime>();
        DateTime startDT = start.getDateTime();
        //we need withMillisOfSecond call cause cronExpression.isSatisfiedBy doesnt care about millis, while isBefore does.
        //So we need endDT to be aligned to 0 ms in order to use isBefore successfully
        DateTime endDT = end.getDateTime().withMillisOfSecond(0);

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
