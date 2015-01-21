package com.collective.celos.ci.mode.test;

import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import com.collective.celos.ci.mode.test.client.CelosClient;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by akonopko on 9/18/14.
 */
public class CelosSchedulerWorker {

    private final CelosClient client;

    public CelosSchedulerWorker(int port) {
        this.client = new CelosClient("http://localhost:" + port);
    }

    public void runCelosScheduler(TestCase testConfig) throws Exception {
        List<WorkflowID> workflowList;
        if (testConfig.getTargetWorkflows().isEmpty()) {
            workflowList = client.getWorkflowList();
        } else {
            workflowList = testConfig.getTargetWorkflows();
        }

        ScheduledTime startTime = testConfig.getSampleTimeStart().plusSeconds(1);
        ScheduledTime actualTime = startTime;
        ScheduledTime endTime = testConfig.getSampleTimeEnd().plusSeconds(1);

        System.out.println("Starting scheduler for: " + StringUtils.join(workflowList, ", "));
        client.iterateScheduler(actualTime, testConfig.getTargetWorkflows());

        while (!actualTime.getDateTime().isAfter(endTime.getDateTime())) {
            printWorkflowStatuses(workflowList, actualTime);
            if (!isThereAnyRunningWorkflows(workflowList, actualTime)) {
                actualTime = new ScheduledTime(actualTime.getDateTime().plusHours(1));
            } else {
                Thread.sleep(2000);
            }
            client.iterateScheduler(actualTime, testConfig.getTargetWorkflows());
        }
    }

    private boolean isThereAnyRunningWorkflows(List<WorkflowID> workflowList, ScheduledTime schedTime) throws IOException {
        for (WorkflowID workflowID : workflowList) {
            List<SlotState> slotStates = client.getWorkflowStatus(workflowID, schedTime);
            for (SlotState slotState : slotStates) {
                if (slotState.getStatus() == SlotState.Status.READY || slotState.getStatus() == SlotState.Status.RUNNING) {
                    return true;
                }
            }
        }
        return false;
    }

    private void printWorkflowStatuses(List<WorkflowID> workflowList, ScheduledTime schedTime) throws IOException {
        List<String> messages = Lists.newArrayList();
        for (WorkflowID workflowID : workflowList) {
            List<SlotState> slotStates = client.getWorkflowStatus(workflowID, schedTime);
            for (SlotState slotState : slotStates) {
                if (slotState.getStatus() != SlotState.Status.WAITING) {
                    messages.add(slotState.toString());
                }
            }
        }
        if (!messages.isEmpty()) {
            System.out.println("Workflow statuses: " + StringUtils.join(messages, " "));
        }
    }

}