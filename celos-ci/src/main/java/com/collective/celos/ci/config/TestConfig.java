package com.collective.celos.ci.config;

import com.collective.celos.ScheduledTime;

public class TestConfig {

    private final ScheduledTime sampleTimeStart;
    private final ScheduledTime sampleTimeEnd;

    public TestConfig(ScheduledTime sampleTimeStart, ScheduledTime sampleTimeEnd) {
        this.sampleTimeStart = sampleTimeStart;
        this.sampleTimeEnd = sampleTimeEnd;
    }

    public ScheduledTime getSampleTimeStart() {
        return sampleTimeStart;
    }

    public ScheduledTime getSampleTimeEnd() {
        return sampleTimeEnd;
    }
}
