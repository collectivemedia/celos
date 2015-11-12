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

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import com.collective.celos.trigger.Trigger;
import org.eclipse.jetty.util.ConcurrentHashSet;

import javax.swing.text.StyledEditorKit;

/**
 * Master control program.
 */
public class Scheduler {

    private final int slidingWindowHours;
    private final WorkflowConfiguration configuration;

    private static Logger LOGGER = Logger.getLogger(Scheduler.class);

    public Scheduler(WorkflowConfiguration configuration, int slidingWindowHours) {
        if (slidingWindowHours <= 0) {
            throw new IllegalArgumentException("Sliding window hours must greater then zero.");
        }
        this.slidingWindowHours = slidingWindowHours;
        this.configuration = Util.requireNonNull(configuration);
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
    public void step(ScheduledTime current, StateDatabaseConnection connection) throws Exception {
        // by default, schedule all workflows
        step(current, Collections.<WorkflowID>emptySet(), connection);
    }

    /**
     * If workflowIDs is empty, schedule all workflows.
     * <p>
     * Otherwise, schedule only workflows in the set.
     */

    static ExecutorService executor = Executors.newFixedThreadPool(50);
    static CompletionService<Void> completionService = new ExecutorCompletionService<Void>(executor);
    static int stepNum = 0;

    public void step(ScheduledTime current, Set<WorkflowID> workflowIDs, final StateDatabaseConnection connection) throws Exception {
        LOGGER.info("##" +stepNum + ": Starting scheduler step: " + current + " -- " + getSlidingWindowStartTime(current));
        
        List<Workflow> workflows = new ArrayList<>(getWorkflowConfiguration().getWorkflows());
        List<List<Workflow>> splited = Lists.partition(workflows, workflows.size() / 100);
        LOGGER.info("##" +stepNum + " there are " + workflows.size());

        long time1 = System.currentTimeMillis();

        final AtomicInteger i = new AtomicInteger(0);
        for(List<Workflow> workflowsSplit: splited) {
            Thread.sleep(10);
            Callable<Void> callable = () -> {
                LOGGER.info("##" +stepNum + " part " + i.getAndIncrement() + " processing " + workflowsSplit.size());
                long max = 0, avg = 0;
                for (Workflow wf : workflowsSplit) {
                    long l1 = System.currentTimeMillis();
                    processWorkflow(wf, workflowIDs, connection, current);
                    long time = System.currentTimeMillis() - l1;
                    if (time > max) max  = time;
                    avg += time;
                }
                avg /= workflowsSplit.size();
                LOGGER.info("##" +stepNum + " part " + i.get() + " finished. Max time is " + max + " Avg is " + avg);
                return null;
            };
            completionService.submit(callable);
        }

        int received = 0;
        while(received < splited.size()) {
            completionService.take(); //blocks if none available
            received++;
        }
        stepNum++;
        long time2 = System.currentTimeMillis();
        LOGGER.info("##" +stepNum + " is finished in " + (time2-time1));
        LOGGER.info("Ending scheduler step: " + current + " -- " + getSlidingWindowStartTime(current));
    }


    private void processWorkflow(Workflow wf, Set<WorkflowID> workflowIDs, StateDatabaseConnection connection, ScheduledTime current) throws Exception {
        WorkflowID id = wf.getID();
        boolean shouldProcess = workflowIDs.isEmpty() || workflowIDs.contains(id);
        if (!shouldProcess) {
            LOGGER.info("Ignoring workflow: " + id);
        } else if (connection.isPaused(id)) {
            LOGGER.info("Workflow is paused: " + id);
        } else {
            try {
                stepWorkflow(wf, current, connection);
            } catch (Exception e) {
                LOGGER.error("Exception in workflow: " + id + ": " + e.getMessage(), e);
            }
        }
    }

    static Set<WorkflowID> test = new HashSet<>();
    static {
        test.add(new WorkflowID("spawn-1517"));
        test.add(new WorkflowID("spawn-2517"));
        test.add(new WorkflowID("spawn-3517"));
    }

    private void stepWorkflow(Workflow wf, ScheduledTime current, StateDatabaseConnection connection) throws Exception {
//        LOGGER.info("Processing workflow: " + wf.getID() + " at: " + current);

        long t1 = System.currentTimeMillis();
        List<SlotState> slotStates = getSlotStatesIncludingMarkedForRerun(wf, current, getWorkflowStartTime(wf, current), current, connection);
        long t2 = System.currentTimeMillis();
        runExternalWorkflows(connection, wf, slotStates);
        long t3 = System.currentTimeMillis();
        for (SlotState slotState : slotStates) {
            updateSlotState(wf, slotState, current, connection);
        }
        long t4 = System.currentTimeMillis();

        if (test.contains(wf.getID())) {
            LOGGER.info(wf.getID() + ": getSlotStatesIncludingMarkedForRerun " + (t2 - t1));
            LOGGER.info(wf.getID() + ": runExternalWorkflows " + (t3 - t2));
            LOGGER.info(wf.getID() + ": UpdateSlotStates " + (t4 - t3));
        }
    }

    private void runExternalWorkflows(StateDatabaseConnection connection, Workflow wf, List<SlotState> slotStates) {
        try {
            List<SlotState> scheduledSlots = wf.getSchedulingStrategy().getSchedulingCandidates(slotStates);
            for (SlotState slotState : scheduledSlots) {
                if (!slotState.getStatus().equals(SlotState.Status.READY)) {
                    throw new IllegalStateException("Scheduling strategy returned non-ready slot: " + slotState);
                }
                SlotID slotID = slotState.getSlotID();
                LOGGER.info("Submitting slot to external service: " + slotID);
                String externalID = wf.getExternalService().submit(slotID);
                connection.putSlotState(slotState.transitionToRunning(externalID));
                LOGGER.info("Starting slot: " + slotID + " with external ID: " + externalID);
                wf.getExternalService().start(slotID, externalID);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the slot states of all slots of the workflow from within the window defined by start (inclusive) and end (exclusive),
     * as well as the slots states of all slots marked for rerun in the database.
     */
    public List<SlotState> getSlotStatesIncludingMarkedForRerun(Workflow wf, ScheduledTime current, ScheduledTime start, ScheduledTime end, StateDatabaseConnection connection) throws RuntimeException {
        try {
            SortedSet<ScheduledTime> times = new TreeSet<>();
            times.addAll(wf.getSchedule().getScheduledTimes(this, start, end));
            times.addAll(connection.getTimesMarkedForRerun(wf.getID(), current));
            return fetchSlotStates(wf, times, connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<SlotState> fetchSlotStates(Workflow wf, SortedSet<ScheduledTime> scheduledTimes, StateDatabaseConnection connection) throws Exception {
        List<SlotState> slotStates = new ArrayList<SlotState>(scheduledTimes.size());
        for (ScheduledTime t : scheduledTimes) {
            SlotID slotID = new SlotID(wf.getID(), t);
            SlotState slotState = connection.getSlotState(slotID);
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
     * Get the slot states of all slots of the workflow from within the window defined by start (inclusive) and end (exclusive).
     * This is used for servlets that return the slot states within the window, and don't care about rerun slots.
     */
    public List<SlotState> getSlotStates(Workflow wf, ScheduledTime start, ScheduledTime end, StateDatabaseConnection connection) throws Exception {
        SortedSet<ScheduledTime> times = new TreeSet<>();
        times.addAll(wf.getSchedule().getScheduledTimes(this, start, end));
        Map<SlotID, SlotState> fetchedSlots = connection.getSlotStates(wf.getID(), start, end);
        return matchScheduledToFetched(wf, times, fetchedSlots);
    }

    private List<SlotState> matchScheduledToFetched(Workflow wf, SortedSet<ScheduledTime> scheduledTimes, Map<SlotID, SlotState> timeToSlots) throws Exception {
        List<SlotState> slotStates = new ArrayList<SlotState>(scheduledTimes.size());
        for (ScheduledTime t : scheduledTimes) {
            SlotID slotID = new SlotID(wf.getID(), t);
            SlotState slotState = timeToSlots.get(slotID);
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
     * Check the trigger for all WAITING slots, and update them to READY if data is available.
     * <p>
     * Check the external status of all RUNNING slots, and update them to SUCCESS or FAILURE if they're finished.
     */
    void updateSlotState(Workflow wf, SlotState slotState, ScheduledTime current, StateDatabaseConnection connection) {
        try {
            SlotID slotID = slotState.getSlotID();
            SlotState.Status status = slotState.getStatus();
            if (status.equals(SlotState.Status.WAITING)) {
                if (callTrigger(wf, slotState, current, connection)) {
//                    LOGGER.info("Slot is ready: " + slotID);
                    connection.putSlotState(slotState.transitionToReady());
                } else if (isSlotTimedOut(slotState.getScheduledTime(), current, wf.getWaitTimeoutSeconds())) {
                    LOGGER.info("Slot timed out waiting: " + slotID);
                    connection.putSlotState(slotState.transitionToWaitTimeout());
                } else {
//                    LOGGER.info("Waiting for slot: " + slotID);
                }
            } else if (status.equals(SlotState.Status.RUNNING)) {
                String externalID = slotState.getExternalID();
                ExternalStatus xStatus = wf.getExternalService().getStatus(slotID, externalID);
                if (!xStatus.isRunning()) {
                    if (xStatus.isSuccess()) {
                        LOGGER.info("Slot successful: " + slotID + " external ID: " + externalID);
                        connection.putSlotState(slotState.transitionToSuccess());
                    } else {
                        if (slotState.getRetryCount() < wf.getMaxRetryCount()) {
                            LOGGER.info("Slot failed, preparing for retry: " + slotID + " external ID: " + externalID);
                            connection.putSlotState(slotState.transitionToRetry());
                        } else {
                            LOGGER.info("Slot failed permanently: " + slotID + " external ID: " + externalID);
                            connection.putSlotState(slotState.transitionToFailure());
                        }
                    }
                } else {
                    LOGGER.info("Slot still running: " + slotID + " external ID: " + externalID);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean callTrigger(Workflow wf, SlotState slotState, ScheduledTime current, StateDatabaseConnection connection) throws Exception {
        Trigger trigger = wf.getTrigger();
        ScheduledTime scheduledTime = slotState.getScheduledTime();
        return trigger.isDataAvailable(connection, current, scheduledTime);
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

}
