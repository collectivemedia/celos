package com.collective.celos;

import java.util.Iterator;
import java.util.Properties;

import org.apache.oozie.client.AuthOozieClient;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.client.WorkflowJob;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Oozie external service.
 */
public class OozieExternalService implements ExternalService {
    
    public static final String YEAR_PROP = "year";
    public static final String MONTH_PROP = "month";
    public static final String DAY_PROP = "day";
    public static final String HOUR_PROP = "hour";
    public static final String MINUTE_PROP = "minute";
    public static final String SECOND_PROP = "second";
    public static final String WORKFLOW_NAME_PROP = "celosWorkflowName";
    
    private final OozieClient client;
    private final String oozieURL;
    private PropertiesGenerator gen;

    public OozieExternalService(String oozieURL, PropertiesGenerator gen) {
        this.oozieURL = Util.requireNonNull(oozieURL);
        this.gen = Util.requireNonNull(gen);
        this.client = new AuthOozieClient(oozieURL);
    }
    
    @Override
    public String submit(SlotID id) throws ExternalServiceException {
        try {
            Properties runProperties = setupRunProperties(getProperties(id), id);
            return client.submit(runProperties);
        } catch (Exception e) {
            throw new ExternalServiceException(e);
        }
    }

    public ObjectNode getProperties(SlotID id) {
        return gen.getProperties(id);
    }
    
    @Override
    public void start(SlotID unused, String externalID) throws ExternalServiceException {
        try {
            client.start(externalID);
        } catch (OozieClientException e) {
            throw new ExternalServiceException(e);
        }
    }

    Properties setupRunProperties(ObjectNode defaults, SlotID id) {
        ScheduledTime t = id.getScheduledTime();
        Properties runProperties = setupDefaultProperties(defaults, t);
        ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
        runProperties.setProperty(YEAR_PROP, formatter.formatYear(t));
        runProperties.setProperty(MONTH_PROP, formatter.formatMonth(t));
        runProperties.setProperty(DAY_PROP, formatter.formatDay(t));
        runProperties.setProperty(HOUR_PROP, formatter.formatHour(t));
        runProperties.setProperty(MINUTE_PROP, formatter.formatMinute(t));
        runProperties.setProperty(SECOND_PROP, formatter.formatSecond(t));
        runProperties.setProperty(WORKFLOW_NAME_PROP, getWorkflowName(id, formatter));
        return runProperties;
    }

    String getWorkflowName(SlotID id, ScheduledTimeFormatter formatter) {
        return id.getWorkflowID() + "@" + formatter.formatPretty(id.getScheduledTime());
    }

    Properties setupDefaultProperties(ObjectNode defaults, ScheduledTime t) {
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
    public ExternalStatus getStatus(SlotID unused, String jobId) throws ExternalServiceException {
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

}
