package com.collective.celos;

/**
 * Fake implementation of external service for testing.
 */
public class MockExternalService implements ExternalService {

    @Override
    public String run(ScheduledTime t) throws ExternalServiceException {
        return "mock-" + Math.random();
    }

    @Override
    public ExternalStatus getStatus(String externalWorkflowID)
            throws ExternalServiceException {
        
        return new ExternalStatus() {

            public boolean isRunning() {
                return true;
            }

            @Override
            public boolean isSuccess() {
                return true;
            }
            
        };
    }

}
