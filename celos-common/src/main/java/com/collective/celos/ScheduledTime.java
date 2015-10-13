/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos;

import org.apache.commons.lang.builder.CompareToBuilder;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Time of a workflow run in UTC.
 */
public class ScheduledTime extends ValueObject implements Comparable<ScheduledTime> {

    public static final ScheduledTimeFormatter FORMATTER = new ScheduledTimeFormatter();
    protected final ZonedDateTime dateTime;

    public ScheduledTime(String formattedDate) {
        this(ZonedDateTime.parse(formattedDate));
    }

    public ScheduledTime(ZonedDateTime dateTime) {
        this.dateTime = Util.requireNonNull(dateTime);
        if (!dateTime.getZone().equals(ZoneOffset.UTC)) {
            throw new IllegalArgumentException(
                    "Scheduled time must be in UTC, but isn't: " + dateTime);
        }
    }

    @Override
    public int compareTo(ScheduledTime t) {
        return CompareToBuilder.reflectionCompare(this, t);
    }

    public int getYear() {
        return dateTime.getYear();
    }

    public int getMonth() {
        return dateTime.getMonthValue();
    }

    public int getDay() {
        return dateTime.getDayOfMonth();
    }

    public int getHour() {
        return dateTime.getHour();
    }

    public int getMinute() {
        return dateTime.getMinute();
    }

    public int getSecond() {
        return dateTime.getSecond();
    }

    public int getMillisecond() {
        return dateTime.getNano() / 1000 / 1000;
    }

    public String toString() {
        return dateTime.toString();
    }

    public ScheduledTime minusYears(int i) {
        return new ScheduledTime(dateTime.minusYears(i));
    }

    public ScheduledTime plusYears(int i) {
        return new ScheduledTime(dateTime.plusYears(i));
    }

    public ScheduledTime minusMonths(int i) {
        return new ScheduledTime(dateTime.minusMonths(i));
    }

    public ScheduledTime plusMonths(int i) {
        return new ScheduledTime(dateTime.plusMonths(i));
    }

    public ScheduledTime minusDays(int i) {
        return new ScheduledTime(dateTime.minusDays(i));
    }

    public ScheduledTime plusDays(int i) {
        return new ScheduledTime(dateTime.plusDays(i));
    }

    public ScheduledTime minusHours(int i) {
        return new ScheduledTime(dateTime.minusHours(i));
    }

    public ScheduledTime plusHours(int i) {
        return new ScheduledTime(dateTime.plusHours(i));
    }

    public ScheduledTime minusMinutes(int i) {
        return new ScheduledTime(dateTime.minusMinutes(i));
    }

    public ScheduledTime plusMinutes(int i) {
        return new ScheduledTime(dateTime.plusMinutes(i));
    }


    public ScheduledTime minusSeconds(int i) {
        return new ScheduledTime(dateTime.minusSeconds(i));
    }

    public ScheduledTime plusSeconds(int i) {
        return new ScheduledTime(dateTime.plusSeconds(i));
    }

    public String year() {
        return FORMATTER.formatYear(dateTime);
    }

    public String month() {
        return FORMATTER.formatMonth(dateTime);
    }

    public String day() {
        return FORMATTER.formatDay(dateTime);
    }

    public String hour() {
        return FORMATTER.formatHour(dateTime);
    }

    public String minute() {
        return FORMATTER.formatMinute(dateTime);
    }

    public String second() {
        return FORMATTER.formatSecond(dateTime);
    }

}
