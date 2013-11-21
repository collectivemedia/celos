package com.collective.celos;

import static org.apache.oozie.client.WorkflowJob.Status.PREP;
import static org.apache.oozie.client.WorkflowJob.Status.RUNNING;
import static org.apache.oozie.client.WorkflowJob.Status.SUCCEEDED;

import org.apache.oozie.client.WorkflowJob.Status;

public class OozieExternalStatus implements ExternalStatus {

    private Status status;

    public OozieExternalStatus(String statusString) {
        this.status = Status.valueOf(statusString);
        if (status == null) {
            throw new IllegalArgumentException("Invalid status string: '"
                    + statusString + "'");
        }
    }

    public boolean isRunning() {
        return status == RUNNING || status == PREP;
    }

    public boolean isSuccess() {
        return status == SUCCEEDED;
    }

}
