package com.collective.celos;

import static com.collective.celos.SlotState.Status.FAILURE;
import static com.collective.celos.SlotState.Status.RUNNING;
import static com.collective.celos.SlotState.Status.SUCCESS;

import com.collective.celos.SlotState.Status;

public abstract class ExternalStatus {

    public abstract Status getStatus();

    /*
     * I'm not sure which of these methods is needed. If none are (that is, if
     * the getStatus() call is sufficient), then we can remove these methods and
     * change this abstract class to an interface.
     */
    public boolean isRunning() {
        return getStatus().equals(RUNNING);
    }

    public boolean isSuccess() {
        return getStatus().equals(SUCCESS);
    }

    public boolean isFailure() {
        return getStatus().equals(FAILURE);
    }

}
