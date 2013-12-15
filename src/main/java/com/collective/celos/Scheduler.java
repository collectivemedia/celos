package com.collective.celos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

/**
 * TODO: timeout slots if trigger doesn't return true for too long
 * TODO: retry handling
 */
public class Scheduler {

    private final int slidingWindowHours;
    private final WorkflowConfiguration configuration;
    private final StateDatabase database;
    
    public Scheduler(WorkflowConfiguration configuration, StateDatabase database, int slidingWindowHours) {
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
    ScheduledTime getStartTime(ScheduledTime current) {
        return new ScheduledTime(current.getDateTime().minusHours(slidingWindowHours));
    }
    
    /**
     * Main method, called every minute.
     * 
     * Steps through all workflows.
     */
    public void step(ScheduledTime current) {
        Util.logInfo("Starting scheduler step: " + current + " -- " + getStartTime(current));
        for (Workflow wf : configuration.getWorkflows()) {
            try {
                stepWorkflow(wf, current);
            } catch(Exception e) {
                Util.logException(e);
                Util.logInfo("Aborted processing workflow: " + wf.getID() + " due to exception.");
            }
        }
        Util.logInfo("Ending scheduler step: " + current + " -- " + getStartTime(current));
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
        Util.logInfo("Processing workflow: " + wf.getID() + " at: " + current);
        List<SlotState> slotStates = getSlotStates(wf, current);
        runExternalWorkflows(wf, slotStates);
        for (SlotState slotState : slotStates) {
            updateSlotState(wf, slotState);
        }
    }

    /**
     * Get the slot states of all slots of the workflow from within the sliding window.
     */
    private List<SlotState> getSlotStates(Workflow wf, ScheduledTime current) throws Exception {
        SortedSet<ScheduledTime> scheduledTimes =  wf.getSchedule().getScheduledTimes(getStartTime(current), current);
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
    
    /**
     * Get scheduled slots from scheduling strategy and submit them to external system.
     */
    void runExternalWorkflows(Workflow wf, List<SlotState> slotStates) throws Exception {
        List<SlotState> scheduledSlots = wf.getSchedulingStrategy().getSchedulingCandidates(slotStates);
        for (SlotState slotState : scheduledSlots) {
            if (!slotState.getStatus().equals(SlotState.Status.READY)) {
                throw new IllegalStateException("Scheduling strategy returned non-ready slot: " + slotState);
            }
            Util.logInfo("Submitting slot to external service: " + slotState.getSlotID());
            String externalID = wf.getExternalService().submit(slotState.getScheduledTime());
            database.putSlotState(slotState.transitionToRunning(externalID));
            Util.logInfo("Starting slot: " + slotState.getSlotID() + " with external ID: " + externalID);
            wf.getExternalService().start(externalID);
        }
    }

    /**
     * Check the trigger for all WAITING slots, and update them to READY if data is available.
     * 
     * Check the external status of all RUNNING slots, and update them to SUCCESS or FAILURE if they're finished.
     */
    void updateSlotState(Workflow wf, SlotState slotState) throws Exception {
        SlotState.Status status = slotState.getStatus();
        if (status.equals(SlotState.Status.WAITING)) {
            if (wf.getTrigger().isDataAvailable(slotState.getScheduledTime())) {
                Util.logInfo("Data available: " + slotState.getSlotID());
                database.putSlotState(slotState.transitionToReady());
            } else {
                Util.logInfo("No data available: " + slotState.getSlotID());
            }
        } else if (status.equals(SlotState.Status.RUNNING)) {
            ExternalStatus xStatus = wf.getExternalService().getStatus(slotState.getExternalID());
            if (!xStatus.isRunning()) {
                if (xStatus.isSuccess()) {
                    Util.logInfo("Slot successful: " + slotState.getSlotID() + " external ID: " + slotState.getExternalID());
                    database.putSlotState(slotState.transitionToSuccess());
                } else {
                    if (slotState.getRetryCount() < wf.getMaxRetryCount()) {
                        Util.logInfo("Slot failed, preparing for retry: " + slotState.getSlotID() + " external ID: " + slotState.getExternalID());
                        database.putSlotState(slotState.transitionToRetry());
                    } else {
                        Util.logInfo("Slot failed permanently: " + slotState.getSlotID() + " external ID: " + slotState.getExternalID());
                        database.putSlotState(slotState.transitionToFailure());
                    }
                }
            } else {
                Util.logInfo("Slot still running: " + slotState.getSlotID() + " external ID: " + slotState.getExternalID());
            }
        }
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
