package com.collective.celos;


public interface ExternalService {

    /**
     * Runs workflow in external service for the scheduled time.
     * 
     * Returns external ID of launched workflow, or throws an exception.
     */
    public String run(ScheduledTime t) throws ExternalServiceException;
    
    /**
     * Gets the status of the externally running workflow with the given ID.
     */
    public ExternalStatus getStatus(String externalWorkflowID) throws ExternalServiceException;

}