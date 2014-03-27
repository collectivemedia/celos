package com.collective.celos;

import java.util.HashMap;
import java.util.Map;

import com.collective.celos.api.Util;

/**
 * Fake implementation of external service for testing.
 * 
 * Always returns the external status passed to its constructor.
 * 
 * Remembers scheduled times that have been submitted for running and their associated fake external IDs.
 */
public class MockExternalService implements ExternalService {

    private final ExternalStatus status;
    private final Map<SlotID, String> slots2ExternalID = new HashMap<SlotID, String>();

    public MockExternalService(ExternalStatus status) {
        this.status = Util.requireNonNull(status);
    }
    
    @Override
    public String submit(SlotID id) {
        String externalID = "mock-" + Math.random();
        slots2ExternalID.put(id, externalID);
        return externalID;
    }
    
    @Override
    public void start(SlotID id, String externalID) throws ExternalServiceException {
    }

    @Override
    public ExternalStatus getStatus(SlotID id, String externalID) {
        return status;
    }
    
    public Map<SlotID, String> getSlots2ExternalID() {
        return slots2ExternalID;
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
