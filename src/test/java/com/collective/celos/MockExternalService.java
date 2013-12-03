package com.collective.celos;

import java.util.HashMap;
import java.util.Map;

/**
 * Fake implementation of external service for testing.
 * 
 * Always returns the external status passed to its constructor.
 * 
 * Remembers scheduled times that have been submitted for running and their associated fake external IDs.
 */
public class MockExternalService implements ExternalService {

    private final ExternalStatus status;
    private final Map<ScheduledTime, String> times2ExternalID = new HashMap<ScheduledTime, String>();

    public MockExternalService(ExternalStatus status) {
        this.status = Util.requireNonNull(status);
    }
    
    @Override
    public String submit(ScheduledTime t) {
        String externalID = "mock-" + Math.random();
        times2ExternalID.put(t, externalID);
        return externalID;
    }
    
    @Override
    public void start(String externalID) throws ExternalServiceException {
    }

    @Override
    public ExternalStatus getStatus(String externalWorkflowID) {
        return status;
    }
    
    public Map<ScheduledTime, String> getTimes2ExternalID() {
        return times2ExternalID;
    }

    public static class MockExternalStatusRunning implements ExternalStatus {

        @Override
        public boolean isRunning() {
            return true;
        }

        @Override
        public boolean isSuccess() {
            throw new IllegalStateException("Still running.");
        }
        
    }
    
    public static class MockExternalStatusSuccess implements ExternalStatus {

        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }
        
    }

    public static class MockExternalStatusFailure implements ExternalStatus {

        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }
        
    }

}
