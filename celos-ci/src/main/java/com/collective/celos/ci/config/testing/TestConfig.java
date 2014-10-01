package com.collective.celos.ci.config.testing;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Util;

public class TestConfig {

    private final ScheduledTime sampleTimeStart;
    private final ScheduledTime sampleTimeEnd;

    public TestConfig(ScheduledTime sampleTimeStart, ScheduledTime sampleTimeEnd) {
        this.sampleTimeStart = Util.requireNonNull(sampleTimeStart);
        this.sampleTimeEnd = Util.requireNonNull(sampleTimeEnd);
    }

    public ScheduledTime getSampleTimeStart() {
        return sampleTimeStart;
    }

    public ScheduledTime getSampleTimeEnd() {
        return sampleTimeEnd;
    }
}
