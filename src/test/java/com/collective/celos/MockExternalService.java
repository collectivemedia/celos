package com.collective.celos;

/**
 * Fake implementation of external service for testing.
 * 
 * Always returns the external status passed to its constructor.
 */
public class MockExternalService implements ExternalService {

    private final ExternalStatus status;

    public MockExternalService(ExternalStatus status) {
        this.status = Util.requireNonNull(status);
    }
    
    @Override
    public String run(ScheduledTime t) {
        return "mock-" + Math.random();
    }

    @Override
    public ExternalStatus getStatus(String externalWorkflowID) {
        return status;
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
