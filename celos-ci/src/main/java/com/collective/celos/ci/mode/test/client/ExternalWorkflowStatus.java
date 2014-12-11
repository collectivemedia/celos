package com.collective.celos.ci.mode.test.client;

import com.collective.celos.SlotState;
import com.collective.celos.ValueObject;

public class ExternalWorkflowStatus extends ValueObject {

    private final SlotState.Status status;
    private final String externalID;
    private final Integer retryCount;

    /**
     * this doesnt make any sence but is required for deserializing
     */
    ExternalWorkflowStatus() {
        this.status = null;
        this.externalID = null;
        this.retryCount = null;
    }

    public ExternalWorkflowStatus(SlotState.Status status, String externalID, Integer retryCount) {
        this.status = status;
        this.externalID = externalID;
        this.retryCount = retryCount;
    }

    public SlotState.Status getStatus() {
        return status;
    }

    public String getExternalID() {
        return externalID;
    }

    public Integer getRetryCount() {
        return retryCount;
    }
}
