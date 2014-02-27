package com.collective.celos;

import java.text.ParseException;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.quartz.CronExpression;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class CronSchedule implements Schedule {

    public final static String CONFIG_PROP = "celos.cron.config";
    
    private CronExpression cronExpression;

    public CronSchedule(ObjectNode properties) {
        String cronConfig = Util.getStringProperty(properties, CONFIG_PROP);
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
    public SortedSet<ScheduledTime> getScheduledTimes(ScheduledTime start, ScheduledTime end) {
        Date startDT = start.getDateTime().toDate();

        SortedSet<ScheduledTime> scheduledTimes = new TreeSet<ScheduledTime>();
        if (cronExpression.isSatisfiedBy(startDT) && start.getDateTime().isBefore(end.getDateTime())) {
            scheduledTimes.add(start);
        }

        DateTime candidateTime = start.getDateTime();

        while((candidateTime = getNextDateTime(candidateTime)) != null &&
                candidateTime.isBefore(end.getDateTime())) {

            scheduledTimes.add(new ScheduledTime(candidateTime));
        }
        return scheduledTimes;
    }

    private DateTime getNextDateTime(DateTime candidateTime) {
        Date date = cronExpression.getNextValidTimeAfter(candidateTime.toDate());
        if (date != null) {
            return new DateTime(date).withZone(DateTimeZone.UTC);
        }
        return null;
    }

}
