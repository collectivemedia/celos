package com.collective.celos;

import java.util.Properties;

public interface ExternalService {
    // returns external ID or throws Exception if something goes wrong
    // the system will set YEAR/MONTH/DAY/HOUR in the props automatically
    // other properties like path to workflow.xml will simply be passed through from the workflow configuration
    public String run(Properties props) throws ExternalServiceException;
    public ExternalStatus getStatus(String externalWorkflowID) throws ExternalServiceException;
}