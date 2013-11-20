package com.collective.celos;

import java.util.Properties;

import org.apache.oozie.client.AuthOozieClient;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.client.WorkflowJob;

public class OozieExternalService implements ExternalService {
    
    private OozieClient client;

    public OozieExternalService() {
        client = new AuthOozieClient("http://oj01.ny7.collective-media.net:11000/oozie");
        System.out.println("client=" + client);
    }

    @Override
    public String run(Properties props) throws ExternalServiceException {
        try {
            return client.dryrun(props);
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
            return new ExternalStatus(status);
        } catch (OozieClientException e) {
            throw new ExternalServiceException(e);
        }
    }

}
