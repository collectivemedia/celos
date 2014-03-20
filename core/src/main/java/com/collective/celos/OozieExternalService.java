package com.collective.celos;

import java.util.Iterator;
import java.util.Properties;

import com.collective.celos.api.ScheduledTime;
import com.collective.celos.api.Util;
import org.apache.oozie.client.AuthOozieClient;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.client.WorkflowJob;

import com.fasterxml.jackson.databind.node.ObjectNode;

// TODO: mock AuthOozieClient and check that its methods are called with the right args
public class OozieExternalService implements ExternalService {
    
    public static final String OOZIE_URL_PROP = "celos.oozie.url";
    public static final String YEAR_PROP = "year";
    public static final String MONTH_PROP = "month";
    public static final String DAY_PROP = "day";
    public static final String HOUR_PROP = "hour";
    public static final String MINUTE_PROP = "minute";
    public static final String SECOND_PROP = "second";
    public static final String WORKFLOW_NAME_PROP = "celosWorkflowName";
    
    private final OozieClient client;
    private final ObjectNode properties;
    private final String oozieURL;

    public OozieExternalService(ObjectNode properties) {
        this.oozieURL = Util.getStringProperty(properties, OOZIE_URL_PROP);
        this.client = new AuthOozieClient(getOozieURL());
        this.properties = properties;
    }

    @Override
    public String submit(Workflow wf, ScheduledTime t) throws ExternalServiceException {
        Properties runProperties = setupRunProperties(properties, wf.getID(), t);
        try {
            return client.submit(runProperties);
        } catch (OozieClientException e) {
            throw new ExternalServiceException(e);
        }
    }
    
    @Override
    public void start(String externalID) throws ExternalServiceException {
        try {
            client.start(externalID);
        } catch (OozieClientException e) {
            throw new ExternalServiceException(e);
        }
    }

    Properties setupRunProperties(ObjectNode defaults, WorkflowID workflowID, ScheduledTime t) {
        Properties runProperties = setupDefaultProperties(defaults, t);
        ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
        runProperties.setProperty(YEAR_PROP, formatter.formatYear(t));
        runProperties.setProperty(MONTH_PROP, formatter.formatMonth(t));
        runProperties.setProperty(DAY_PROP, formatter.formatDay(t));
        runProperties.setProperty(HOUR_PROP, formatter.formatHour(t));
        runProperties.setProperty(MINUTE_PROP, formatter.formatMinute(t));
        runProperties.setProperty(SECOND_PROP, formatter.formatSecond(t));
        runProperties.setProperty(WORKFLOW_NAME_PROP, getWorkflowName(workflowID, t, formatter));
        return runProperties;
    }

    private String getWorkflowName(WorkflowID workflowID, ScheduledTime t, ScheduledTimeFormatter formatter) {
        return workflowID.toString() + "@" + formatter.formatDatestamp(t) + "T" + formatter.formatTimestamp(t);
    }

    private Properties setupDefaultProperties(ObjectNode defaults, ScheduledTime t) {
        Properties props = new Properties();
        ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
        for (Iterator<String> names = defaults.fieldNames(); names.hasNext();) {
            String name = names.next();
            String value = defaults.get(name).textValue();
            props.setProperty(name, formatter.replaceTimeTokens(value, t));
        }
        return props;
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

    public String getOozieURL() {
        return oozieURL;
    }

    public ObjectNode getProperties() {
        return properties;
    }

}
