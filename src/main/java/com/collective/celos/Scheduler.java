package com.collective.celos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.junit.Assert;

/**
 * TODO: timeout slots if trigger doesn't return true for too long
 * TODO: retry handling
 */
public class Scheduler {

    private final int slidingWindowHours;

    public Scheduler(int slidingWindowHours) {
        if (slidingWindowHours <= 0) {
            throw new IllegalArgumentException("Sliding window hours must greater then zero.");
        }
        this.slidingWindowHours = slidingWindowHours;
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
    public void step(ScheduledTime current, WorkflowConfiguration cfg, StateDatabase db) {
        for (Iterator<Workflow> it = cfg.getWorkflows().iterator(); it.hasNext();) {
            Workflow wf = it.next();
            try {
                stepWorkflow(current, db, wf);
            } catch(Exception e) {
                Util.logException(e);
            }
        }
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
    private void stepWorkflow(ScheduledTime current, StateDatabase db, Workflow wf) throws Exception {
        List<SlotState> slotStates = getSlotStates(current, db, wf);
        runExternalWorkflows(wf, slotStates, db);
        checkDataAvailability(wf, slotStates, db);
        checkExternalWorkflowStatuses(wf, slotStates, db);
    }

    /**
     * Get the slot states of all slots of the workflow from within the sliding window.
     */
    private List<SlotState> getSlotStates(ScheduledTime current, StateDatabase db, Workflow wf) throws Exception {
        SortedSet<ScheduledTime> scheduledTimes =  wf.getSchedule().getScheduledTimes(getStartTime(current), current);
        List<SlotState> slotStates = new ArrayList<SlotState>(scheduledTimes.size());
        for (Iterator<ScheduledTime> it = scheduledTimes.iterator(); it.hasNext();) {
            SlotID slotID = new SlotID(wf.getID(), it.next());
            SlotState slotState = db.getSlotState(slotID);
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
    private void runExternalWorkflows(Workflow wf, List<SlotState> slotStates, StateDatabase db) throws Exception {
        List<SlotState> scheduledSlots = wf.getSchedulingStrategy().getSchedulingCandidates(slotStates);
        for (Iterator<SlotState> it = scheduledSlots.iterator(); it.hasNext();) {
            SlotState slotState = it.next();
            Assert.assertEquals(SlotState.Status.READY, slotState.getStatus());
            String externalID = wf.getExternalService().run(slotState.getScheduledTime());
            db.putSlotState(slotState.transitionToRunning(externalID));
        }
    }

    /**
     * Check the trigger for all WAITING slots, and update them to READY if data is available.
     */
    private void checkDataAvailability(Workflow wf, List<SlotState> slotStates, StateDatabase db) {
        for (Iterator<SlotState> it = slotStates.iterator(); it.hasNext();) {
            SlotState slotState = it.next();
            if (slotState.getStatus().equals(SlotState.Status.WAITING)) {
                try {
                    if (wf.getTrigger().isDataAvailable(slotState.getScheduledTime())) {
                        db.putSlotState(slotState.transitionToReady());
                    }
                } catch(Exception e) {
                    Util.logException(e);
                }
            }
        }
    }

    /**
     * Check the external status of all RUNNING slots, and update them to SUCCESS or FAILURE if they're finished.
     */
    private void checkExternalWorkflowStatuses(Workflow wf, List<SlotState> slotStates, StateDatabase db) {
        for (Iterator<SlotState> it = slotStates.iterator(); it.hasNext();) {
            SlotState slotState = it.next();
            if (slotState.getStatus().equals(SlotState.Status.RUNNING)) {
                try {
                    ExternalStatus xStatus = wf.getExternalService().getStatus(slotState.getExternalID());
                    if (!xStatus.isRunning()) {
                        if (xStatus.isSuccess()) {
                            db.putSlotState(slotState.transitionToSuccess());
                        } else {
                            db.putSlotState(slotState.transitionToFailure());
                        }
                    }
                } catch(Exception e) {
                    Util.logException(e);
                }
            }
        }
    }
    
}
