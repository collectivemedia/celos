package com.collective.celos.ci.mode.test;

import com.collective.celos.CelosClient;
import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by akonopko on 9/18/14.
 */
public class CelosSchedulerWorker {

    private final CelosClient client;

    public CelosSchedulerWorker(CelosClient client) {
        this.client = client;
    }

    public void runCelosScheduler(TestCase testCase) throws Exception {
        Set<WorkflowID> workflowList;
        if (testCase.getTargetWorkflows().isEmpty()) {
            workflowList = client.getWorkflowList();
        } else {
            workflowList = testCase.getTargetWorkflows();
        }

        ScheduledTime startTime = testCase.getSampleTimeStart().plusSeconds(1);
        ScheduledTime actualTime = startTime;
        ScheduledTime endTime = testCase.getSampleTimeEnd().plusSeconds(1);

        if (!workflowList.isEmpty()) {
            startScheduler(testCase, workflowList, actualTime, endTime);
        } else {
            System.out.println(testCase.getName() + ": there are no workflows to run");
        }
    }

    private void startScheduler(TestCase testCase, Set<WorkflowID> workflowList, ScheduledTime actualTime, ScheduledTime endTime) throws Exception {
        System.out.println(testCase.getName() + ": Starting scheduler for: " + StringUtils.join(workflowList, ", "));
        restartRerunnable(workflowList, actualTime);

        client.iterateScheduler(actualTime, testCase.getTargetWorkflows());

        while (!actualTime.getDateTime().isAfter(endTime.getDateTime())) {
            String workflowStatuses = StringUtils.join(getWorkflowStatusesInfo(workflowList, actualTime), " ");
            if (!workflowStatuses.trim().isEmpty()) {
                System.out.println(testCase.getName() + ": Workflow statuses: " + workflowStatuses);
            }
            if (!isThereAnyRunningWorkflows(workflowList, actualTime)) {
                actualTime = new ScheduledTime(actualTime.getDateTime().plusHours(1));
            } else {
                Thread.sleep(2000);
            }
            client.iterateScheduler(actualTime, testCase.getTargetWorkflows());
        }
    }

    boolean isThereAnyRunningWorkflows(Set<WorkflowID> workflowList, ScheduledTime schedTime) throws Exception {
        for (WorkflowID workflowID : workflowList) {
            List<SlotState> slotStates = client.getWorkflowStatus(workflowID, schedTime).getSlotStates();
            for (SlotState slotState : slotStates) {
                if (slotState.getStatus() == SlotState.Status.READY || slotState.getStatus() == SlotState.Status.RUNNING) {
                    return true;
                }
            }
        }
        return false;
    }

    private void restartRerunnable(Set<WorkflowID> workflowList, ScheduledTime schedTime) throws Exception {
        for (WorkflowID workflowID : workflowList) {
            List<SlotState> slotStates = client.getWorkflowStatus(workflowID, schedTime).getSlotStates();
            for (SlotState slotState : slotStates) {
                if (slotState.isRerunnable()) {
                    client.rerunSlot(slotState.getSlotID().getWorkflowID(), slotState.getSlotID().getScheduledTime());
                }
            }
        }
    }

    Set<String> getWorkflowStatusesInfo(Set<WorkflowID> workflowList, ScheduledTime schedTime) throws Exception {
        Set<String> messages = Sets.newHashSet();
        for (WorkflowID workflowID : workflowList) {
            List<SlotState> slotStates = client.getWorkflowStatus(workflowID, schedTime).getSlotStates();
            for (SlotState slotState : slotStates) {
                if (slotState.getStatus() != SlotState.Status.WAITING) {
                    messages.add(slotState.toString());
                }
            }
        }
        return messages;
    }

}