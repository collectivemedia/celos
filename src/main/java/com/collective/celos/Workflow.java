package com.collective.celos;

public class Workflow {

    private final WorkflowID id;
    private final Schedule schedule;
    private final SchedulingStrategy schedulingStrategy;
    private final Trigger trigger;
    private final ExternalService externalService;
    private final int maxRetryCount;
    
    public Workflow(WorkflowID id,
            Schedule schedule,
            SchedulingStrategy strategy,
            Trigger trigger,
            ExternalService service,
            int maxRetryCount) {
        
        this.id = Util.requireNonNull(id);
        this.schedule = Util.requireNonNull(schedule);
        this.schedulingStrategy = Util.requireNonNull(strategy);
        this.trigger = Util.requireNonNull(trigger);
        this.externalService = Util.requireNonNull(service);
        this.maxRetryCount = maxRetryCount;
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

}
