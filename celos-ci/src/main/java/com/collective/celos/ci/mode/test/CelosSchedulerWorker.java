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
    private final List<WorkflowID> targetWorkflows;

    public CelosSchedulerWorker(int port, List<WorkflowID> targetWorkflows) {
        this.client = new CelosClient("http://localhost:" + port);
        this.targetWorkflows = targetWorkflows;
    }

    public void runCelosScheduler(TestCase testConfig) throws Exception {
        List<WorkflowID> workflowList;
        if (targetWorkflows.isEmpty()) {
            workflowList = client.getWorkflowList();
        } else {
            workflowList = targetWorkflows;
        }

        ScheduledTime startTime = testConfig.getSampleTimeStart();
        ScheduledTime actualTime = startTime;
        ScheduledTime endTime = testConfig.getSampleTimeEnd();

        System.out.println("Starting scheduler for: " + StringUtils.join(workflowList, ", "));
        client.iterateScheduler(actualTime);

        while (!actualTime.getDateTime().isAfter(endTime.getDateTime())) {
            if (!isThereAnyRunningWorkflows(workflowList, actualTime)) {
                actualTime = new ScheduledTime(actualTime.getDateTime().plusHours(1));
            } else {
                Thread.sleep(2000);
            }
            client.iterateScheduler(actualTime);
        }
    }

    private boolean isThereAnyRunningWorkflows(List<WorkflowID> workflowList, ScheduledTime schedTime) throws IOException {

        List<String> messages = Lists.newArrayList();
        boolean continueSchedulerRun = false;
        for (WorkflowID workflowID : workflowList) {
            List<SlotState> slotStates = client.getWorkflowStatus(workflowID, schedTime);
            for (SlotState slotState : slotStates) {
                if (slotState.getStatus() != SlotState.Status.WAITING) {
                    messages.add(slotState.toString());
                }
                if (slotState.getStatus() == SlotState.Status.READY || slotState.getStatus() == SlotState.Status.RUNNING) {
                    continueSchedulerRun = true;
                }
            }
        }
        if (!messages.isEmpty()) {
            System.out.println("Workflow statuses: " + StringUtils.join(messages, " "));
        }
        return continueSchedulerRun;
    }

}
