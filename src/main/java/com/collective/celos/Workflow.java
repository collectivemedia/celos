package com.collective.celos;

public class Workflow {

    public static final ScheduledTime DEFAULT_START_TIME = new ScheduledTime("1970-01-01T00:00:00.000Z");
    public static final int DEFAULT_TRIGGER_TIMEOUT = -1;
    
    private final WorkflowID id;
    private final Schedule schedule;
    private final SchedulingStrategy schedulingStrategy;
    private final Trigger trigger;
    private final ExternalService externalService;
    private final int maxRetryCount;
    private final ScheduledTime startTime;
    private final int triggerTimeoutSeconds;
    
    public Workflow(WorkflowID id,
            Schedule schedule,
            SchedulingStrategy strategy,
            Trigger trigger,
            ExternalService service,
            int maxRetryCount,
            ScheduledTime startTime,
            int triggerTimeoutSeconds) {
        
        this.id = Util.requireNonNull(id);
        this.schedule = Util.requireNonNull(schedule);
        this.schedulingStrategy = Util.requireNonNull(strategy);
        this.trigger = Util.requireNonNull(trigger);
        this.externalService = Util.requireNonNull(service);
        this.maxRetryCount = maxRetryCount;
        this.startTime = Util.requireNonNull(startTime);
        this.triggerTimeoutSeconds = triggerTimeoutSeconds;
    }

    public WorkflowID getID() {
        return id;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public SchedulingStrategy getSchedulingStrategy() {
        return schedulingStrategy;
    }

    public Trigger getTrigger() {
        return trigger;
    }
    
    public ExternalService getExternalService() {
        return externalService;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public ScheduledTime getStartTime() {
        return startTime;
    }

    public int getTriggerTimeoutSeconds() {
        return triggerTimeoutSeconds;
    }

}
