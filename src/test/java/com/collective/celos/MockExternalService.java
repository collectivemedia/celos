package com.collective.celos;

import java.util.HashSet;
import java.util.Set;

/**
 * Fake implementation of external service for testing.
 * 
 * Always returns the external status passed to its constructor.
 * 
 * Remembers scheduled times that have been submitted for running.
 */
public class MockExternalService implements ExternalService {

    private final ExternalStatus status;
    private final Set<ScheduledTime> times = new HashSet<ScheduledTime>();

    public MockExternalService(ExternalStatus status) {
        this.status = Util.requireNonNull(status);
    }
    
    @Override
    public String run(ScheduledTime t) {
        times.add(t);
        return "mock-" + Math.random();
    }

    @Override
    public ExternalStatus getStatus(String externalWorkflowID) {
        return status;
    }
    
    public Set<ScheduledTime> getTimes() {
        return times;
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
