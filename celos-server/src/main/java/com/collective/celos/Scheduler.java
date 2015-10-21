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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.collective.celos.trigger.Trigger;

/**
 * Master control program.
 */
public class Scheduler {

    private final int slidingWindowHours;
    private final WorkflowConfiguration configuration;
    private final StateDatabase database;
    private final int swarmSize;
    private final int celosNumber;

    private static Logger LOGGER = Logger.getLogger(Scheduler.class);

    public Scheduler(WorkflowConfiguration configuration, StateDatabase database, int slidingWindowHours) {
        this(configuration, database, slidingWindowHours, 1, 1);
    }

    public Scheduler(WorkflowConfiguration configuration, StateDatabase database, int slidingWindowHours, int swarmSize, int celosNumber) {
        if (slidingWindowHours <= 0) {
            throw new IllegalArgumentException("Sliding window hours must greater then zero.");
        }
        this.slidingWindowHours = slidingWindowHours;
        this.configuration = Util.requireNonNull(configuration);
        this.database = Util.requireNonNull(database);
        this.swarmSize = swarmSize;
        this.celosNumber = celosNumber;
    }

    /**
     * Returns the start of the sliding window, given the current time.
     */
    ScheduledTime getSlidingWindowStartTime(ScheduledTime current) {
        return new ScheduledTime(current.getDateTime().minusHours(slidingWindowHours));
    }

    /**
     * Main method, called every minute.
     * <p>
     * Steps through all workflows.
     */
    public void step(ScheduledTime current) {
        // by default, schedule all workflows
        step(current, Collections.<WorkflowID>emptySet());
    }

    /**
     * If workflowIDs is empty, schedule all workflows.
     * <p>
     * Otherwise, schedule only workflows in the set.
     */
    public void step(ScheduledTime current, Set<WorkflowID> workflowIDs) {
        LOGGER.info("Starting scheduler step: " + current + " -- " + getSlidingWindowStartTime(current));
        for (Workflow wf : configuration.getWorkflows()) {
            WorkflowID id = wf.getID();
            if (isItMyWorkflow(id)) {
                processWorkflow(current, workflowIDs, wf, id);
            }
        }
        LOGGER.info("Ending scheduler step: " + current + " -- " + getSlidingWindowStartTime(current));
    }

    private boolean isItMyWorkflow(WorkflowID id) {
        return Math.abs(id.toString().hashCode()) % swarmSize == celosNumber;
    }

    private void processWorkflow(ScheduledTime current, Set<WorkflowID> workflowIDs, Workflow wf, WorkflowID id) {
        boolean shouldProcess = workflowIDs.isEmpty() || workflowIDs.contains(id);
        if (!shouldProcess) {
            LOGGER.info("Ignoring workflow: " + id);
        } else if (database.isPaused(id)) {
            LOGGER.info("Workflow is paused: " + id);
        } else {
            try {
                stepWorkflow(wf, current);
            } catch (Exception e) {
                LOGGER.error("Exception in workflow: " + id + ": " + e.getMessage(), e);
            }
        }
    }

    /**
     * Steps a single workflow:
     * <p>
     * - Submit any READY slots to the external service.
     * <p>
     * - Check any WAITING slots for data availability.
     * <p>
     * - Check any RUNNING slots for their current external status.
     */
    private void stepWorkflow(Workflow wf, ScheduledTime current) throws Exception {
        LOGGER.info("Processing workflow: " + wf.getID() + " at: " + current);
        List<SlotState> slotStates = getSlotStatesIncludingMarkedForRerun(wf, current, getWorkflowStartTime(wf, current), current);
        runExternalWorkflows(wf, slotStates);
        for (SlotState slotState : slotStates) {
            updateSlotState(wf, slotState, current);
        }
    }

    /**
     * Get the slot states of all slots of the workflow from within the window defined by start (inclusive) and end (exclusive),
     * as well as the slots states of all slots marked for rerun in the database.
     */
    public List<SlotState> getSlotStatesIncludingMarkedForRerun(Workflow wf, ScheduledTime current, ScheduledTime start, ScheduledTime end) throws Exception {
        SortedSet<ScheduledTime> times = new TreeSet<>();
        times.addAll(wf.getSchedule().getScheduledTimes(this, start, end));
        times.addAll(database.getTimesMarkedForRerun(wf.getID(), current));
        return fetchSlotStates(wf, times);
    }

    /**
     * Get the slot states of all slots of the workflow from within the window defined by start (inclusive) and end (exclusive).
     * This is used for servlets that return the slot states within the window, and don't care about rerun slots.
     */
    public List<SlotState> getSlotStates(Workflow wf, ScheduledTime start, ScheduledTime end) throws Exception {
        SortedSet<ScheduledTime> times = new TreeSet<>();
        times.addAll(wf.getSchedule().getScheduledTimes(this, start, end));
        return fetchSlotStates(wf, times);
    }

    private List<SlotState> fetchSlotStates(Workflow wf, SortedSet<ScheduledTime> scheduledTimes) throws Exception {
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
     * <p>
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
