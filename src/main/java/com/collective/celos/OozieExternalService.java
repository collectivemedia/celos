package com.collective.celos;

import java.util.Properties;

import org.apache.oozie.client.AuthOozieClient;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.client.WorkflowJob;

public class OozieExternalService implements ExternalService {
    
    private OozieClient client;

    public OozieExternalService(String oozieUrl) {
        client = new AuthOozieClient(oozieUrl);
    }

    @Override
    public String run(Properties props) throws ExternalServiceException {
        try {
            return client.run(props);
        } catch (OozieClientException e) {
            throw new ExternalServiceException(e);
        }
    }

    @Override
    public ExternalStatus getStatus(String jobId)
            throws ExternalServiceException {

        try {
            WorkflowJob jobInfo = client.getJobInfo(jobId);
            String status = jobInfo.getStatus().toString();
            return new OozieExternalStatus(status);
        } catch (OozieClientException e) {
            throw new ExternalServiceException(e);
        }
    }

}
