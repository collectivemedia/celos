package com.collective.celos.ci.mode.test;

import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import com.collective.celos.ci.mode.test.client.CelosClient;
import com.collective.celos.ci.mode.test.client.ExternalWorkflowStatus;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

        while (!actualTime.getDateTime().isAfter(endTime.getDateTime())) {
            System.out.println("Scheduler iteration [" + actualTime + "]");
            client.iterateScheduler(actualTime);
            if (!isThereAnyRunningWorkflows(workflowList, actualTime)) {
                actualTime = new ScheduledTime(actualTime.getDateTime().plusHours(1));
            } else {
                Thread.sleep(2000);
            }
        }
    }

    private boolean isThereAnyRunningWorkflows(List<WorkflowID> workflowList, ScheduledTime schedTime) throws IOException {
        for (WorkflowID workflowID : workflowList) {
            if (isWorkflowRunning(workflowID, schedTime)) {
                System.out.println("Workflow is running: " + workflowID + " [" + schedTime.toString() + "]");
                return true;
            }
        }
        return false;
    }


    public boolean isWorkflowRunning(WorkflowID workflowID, ScheduledTime scheduledTime) throws IOException {
        Map<ScheduledTime, ExternalWorkflowStatus> workflowStatusMap = client.getWorkflowStatus(workflowID, scheduledTime);
        for (ExternalWorkflowStatus status : workflowStatusMap.values()) {
            if (status.getStatus() == SlotState.Status.RUNNING || status.getStatus() == status.getStatus().READY) {
                return true;
            }
        }
        return false;
    }




}
