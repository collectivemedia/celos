package com.collective.celos;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.quartz.CronExpression;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

public class CronSchedule implements Schedule {

    private final static String CRON_CONFIG_PROPERTY = "cron_config";
    private CronExpression cronExpression;

    public CronSchedule(ObjectNode ignored) {

        String cronConfig = ignored.get(CRON_CONFIG_PROPERTY).asText();
        if (!CronExpression.isValidExpression(cronConfig)) {
            throw new IllegalArgumentException("Invalid Cron configuration: " + cronConfig);
        }
        try {
            cronExpression = new CronExpression(cronConfig);
            cronExpression.setTimeZone(DateTimeZone.UTC.toTimeZone());
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    public String getCronExpression() {
        return cronExpression.getCronExpression();
    }

    @Override
    public SortedSet<ScheduledTime> getScheduledTimes(ScheduledTime start, ScheduledTime end) {
        Date startDT = start.getDateTime().toDate();

        SortedSet<ScheduledTime> sheduledTimes = new TreeSet<ScheduledTime>();
        if (cronExpression.isSatisfiedBy(startDT)) {
            sheduledTimes.add(start);
        }

        DateTime candidateTime = start.getDateTime();

        while((candidateTime = getNextDateTime(candidateTime)) != null &&
                candidateTime.isBefore(end.getDateTime())) {

            sheduledTimes.add(new ScheduledTime(candidateTime));
        }
        return sheduledTimes;
    }

    private DateTime getNextDateTime(DateTime candidateTime) {
        Date date = cronExpression.getNextValidTimeAfter(candidateTime.toDate());
        if (date != null) {
            return new DateTime(date).withZone(DateTimeZone.UTC);
        }
        return null;
    }

}
