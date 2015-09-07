package com.collective.celos;

/**
 * Status of a workflow in an external service.
 */
public interface ExternalStatus {

    boolean isRunning();

    boolean isSuccess();

}
