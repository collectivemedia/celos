package com.collective.celos;

import org.apache.log4j.Logger;

import java.util.*;

public class Scheduler {

    public static final int DEFAULT_SLIDING_WINDOW_DAYS = 7;

    private final int slidingWindowHours;
    private final WorkflowConfiguration configuration;
    private final StateDatabase database;

    private static Logger LOGGER = Logger.getLogger(Scheduler.class);

    public Scheduler(WorkflowConfiguration configuration,
                     StateDatabase database,
                     RerunDatabase rerunDatabase,
                     int slidingWindowHours) {
        if (slidingWindowHours <= 0) {
            throw new IllegalArgumentException("Sliding window hours must greater then zero.");
        }
        this.slidingWindowHours = slidingWindowHours;
        this.configuration = Util.requireNonNull(configuration);
        this.database = Util.requireNonNull(database);
    }
    
    /**
     * Returns the start of the sliding window, given the current time.
     */
    ScheduledTime getSlidingWindowStartTime(ScheduledTime current) {
        return new ScheduledTime(current.getDateTime().minusHours(slidingWindowHours));
    }
    
    /**
     * Main method, called every minute.
     * 
     * Steps through all workflows.
     */
    public void step(ScheduledTime current) {
    	// by default, schedule all workflows
    	step(current, Collections.<WorkflowID>emptySet());
    }
    
    /**
     * If workflowIDs is empty, schedule all workflows.
     * 
     * Otherwise, schedule only workflows in the set.
     */
    public void step(ScheduledTime current, Set<WorkflowID> workflowIDs) {
        LOGGER.info("Starting scheduler step: " + current + " -- " + getSlidingWindowStartTime(current));
        for (Workflow wf : configuration.getWorkflows()) {
            WorkflowID id = wf.getID();
            if (workflowIDs.isEmpty() || workflowIDs.contains(id)) {
                try {
                    stepWorkflow(wf, current);
                } catch (Exception e) {
                    LOGGER.error("Exception in workflow: " + id + ": " + e.getMessage(), e);
                }
            } else {
                LOGGER.info("Ignoring workflow: " + id);
            }
        }
        LOGGER.info("Ending scheduler step: " + current + " -- " + getSlidingWindowStartTime(current));
    }

    /**
     * Steps a single workflow:
     * 
     * - Submit any READY slots to the external service.
     * 
     * - Check any WAITING slots for data availability.
     * 
     * - Check any RUNNING slots for their current external status.
     */
    private void stepWorkflow(Workflow wf, ScheduledTime current) throws Exception {
        LOGGER.info("Processing workflow: " + wf.getID() + " at: " + current);
        List<SlotState> slotStates = getSlotStates(wf, getWorkflowStartTime(wf, current), current);
        runExternalWorkflows(wf, slotStates);
        for (SlotState slotState : slotStates) {
            updateSlotState(wf, slotState, current);
        }
    }

    /**
     * Get the slot states of all slots of the workflow from within the window defined by start (inclusive) and end (exclusive)
     */
    public List<SlotState> getSlotStates(Workflow wf, ScheduledTime start, ScheduledTime end) throws Exception {
        SortedSet<ScheduledTime> scheduledTimes =  wf.getSchedule().getScheduledTimes(this, start, end);
        List<SlotState> slotStates = new ArrayList<SlotState>(scheduledTimes.size());
        for (ScheduledTime t : scheduledTimes) {
            SlotID slotID = new SlotID(wf.getID(), t);
            SlotState slotState = database.getSlotState(slotID);
            if (slotState != null) {
                slotStates.add(slotState);
            } else {
                // Database doesn't have any info on the slot yet -
                // synthesize a fresh waiting slot and put it in the list
                // (not in the database).
                slotStates.add(new SlotState(slotID, SlotState.Status.WAITING));
            }
        }
        return Collections.unmodifiableList(slotStates);
    }

    public ScheduledTime getWorkflowStartTime(Workflow wf, ScheduledTime current) {
        ScheduledTime slidingWindowStartTime = getSlidingWindowStartTime(current);
        ScheduledTime workflowStartTime = wf.getStartTime();
        return Util.max(slidingWindowStartTime, workflowStartTime);
    }
    
    /**
     * Get scheduled slots from scheduling strategy and submit them to external system.
     */
    void runExternalWorkflows(Workflow wf, List<SlotState> slotStates) throws Exception {
        List<SlotState> scheduledSlots = wf.getSchedulingStrategy().getSchedulingCandidates(slotStates);
        for (SlotState slotState : scheduledSlots) {
            if (!slotState.getStatus().equals(SlotState.Status.READY)) {
                throw new IllegalStateException("Scheduling strategy returned non-ready slot: " + slotState);
            }
            SlotID slotID = slotState.getSlotID();
            LOGGER.info("Submitting slot to external service: " + slotID);
            String externalID = wf.getExternalService().submit(slotID);
            database.putSlotState(slotState.transitionToRunning(externalID));
            LOGGER.info("Starting slot: " + slotID + " with external ID: " + externalID);
            wf.getExternalService().start(slotID, externalID);
        }
    }

    /**
     * Check the trigger for all WAITING slots, and update them to READY if data is available.
     * 
     * Check the external status of all RUNNING slots, and update them to SUCCESS or FAILURE if they're finished.
     */
    void updateSlotState(Workflow wf, SlotState slotState, ScheduledTime current) throws Exception {
        SlotID slotID = slotState.getSlotID();
        SlotState.Status status = slotState.getStatus();
        if (status.equals(SlotState.Status.WAITING)) {
            if (callTrigger(wf, slotState, current)) {
                LOGGER.info("Slot is ready: " + slotID);
                database.putSlotState(slotState.transitionToReady());
            } else if (isSlotTimedOut(slotState.getScheduledTime(), current, wf.getWaitTimeoutSeconds())) {
                LOGGER.info("Slot timed out waiting: " + slotID);
                database.putSlotState(slotState.transitionToWaitTimeout());
            } else {
                LOGGER.info("Waiting for slot: " + slotID);
            }
        } else if (status.equals(SlotState.Status.RUNNING)) {
            String externalID = slotState.getExternalID();
            ExternalStatus xStatus = wf.getExternalService().getStatus(slotID, externalID);
            if (!xStatus.isRunning()) {
                if (xStatus.isSuccess()) {
                    LOGGER.info("Slot successful: " + slotID + " external ID: " + externalID);
                    database.putSlotState(slotState.transitionToSuccess());
                } else {
                    if (slotState.getRetryCount() < wf.getMaxRetryCount()) {
                        LOGGER.info("Slot failed, preparing for retry: " + slotID + " external ID: " + externalID);
                        database.putSlotState(slotState.transitionToRetry());
                    } else {
                        LOGGER.info("Slot failed permanently: " + slotID + " external ID: " + externalID);
                        database.putSlotState(slotState.transitionToFailure());
                    }
                }
            } else {
                LOGGER.info("Slot still running: " + slotID + " external ID: " + externalID);
            }
        }
    }

    private boolean callTrigger(Workflow wf, SlotState slotState, ScheduledTime current) throws Exception {
        Trigger trigger = wf.getTrigger();
        ScheduledTime scheduledTime = slotState.getScheduledTime();
        return trigger.isDataAvailable(this, current, scheduledTime);
    }
    
    static boolean isSlotTimedOut(ScheduledTime nominalTime, ScheduledTime current, int timeoutSeconds) {
        ScheduledTime timeoutTime = nominalTime.plusSeconds(timeoutSeconds);
        return current.getDateTime().isAfter(timeoutTime.getDateTime());
    }
    
    public int getSlidingWindowHours() {
        return slidingWindowHours;
    }

    public WorkflowConfiguration getWorkflowConfiguration() {
        return configuration;
    }

    public StateDatabase getStateDatabase() {
        return database;
    }

}
