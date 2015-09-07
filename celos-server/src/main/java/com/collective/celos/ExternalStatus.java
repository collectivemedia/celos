package com.collective.celos;

/**
 * Status of a workflow in an external system.
 */
public interface ExternalStatus {

    boolean isRunning();

    boolean isSuccess();

}
