package com.collective.celos;

/**
 * Execution engine for workflows.
 */
public interface ExternalService {

    /**
     * Submits slot to external service.
     * 
     * Returns external ID of submitted workflow, or throws an exception.
     */
    public String submit(SlotID id) throws ExternalServiceException;

    /**
     * Starts workflow with the given external ID.
     * 
     * The slot ID is the same that previously passed to submit.
     */
    public void start(SlotID id, String externalID) throws ExternalServiceException;
    
    /**
     * Gets the status of the externally running workflow with the given ID.
     */
    public ExternalStatus getStatus(SlotID id, String externalID) throws ExternalServiceException;

}