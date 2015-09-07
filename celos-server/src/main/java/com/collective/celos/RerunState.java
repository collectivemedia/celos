package com.collective.celos;

public class RerunState {

    public static final int EXPIRATION_DAYS = 14;
    
    // The wallclock time at which the slot was marked for rerun
    private final ScheduledTime rerunTime;
    
    public RerunState(ScheduledTime rerunTime) {
        this.rerunTime = Util.requireNonNull(rerunTime);
    }

    public ScheduledTime getRerunTime() {
        return rerunTime;
    }

    public boolean isExpired(ScheduledTime now) {
        return rerunTime.plusDays(EXPIRATION_DAYS).getDateTime().isAfter(now.getDateTime());
    }
    
}
