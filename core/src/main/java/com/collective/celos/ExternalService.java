package com.collective.celos;

public interface ExternalService {

    /**
     * Submits slot to external service.
     * 
     * Returns external ID of submitted workflow, or throws an exception.
     */
    public String submit(SlotID id) throws ExternalServiceException;

    /**
     * Starts workflow with the given enternal ID.
     */
    public void start(String externalID) throws ExternalServiceException;
    
    /**
     * Gets the status of the externally running workflow with the given ID.
     */
    public ExternalStatus getStatus(String externalWorkflowID) throws ExternalServiceException;

}