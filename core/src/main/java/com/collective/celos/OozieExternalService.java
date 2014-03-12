package com.collective.celos;

import java.util.Iterator;
import java.util.Properties;

import com.collective.celos.api.ScheduledTime;
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
    
    private final OozieClient client;
    private final ObjectNode properties;

    public OozieExternalService(ObjectNode properties) {
        String oozieURL = Util.getStringProperty(properties, OOZIE_URL_PROP);
        this.client = new AuthOozieClient(oozieURL);
        this.properties = properties;
    }

    @Override
    public String submit(ScheduledTime t) throws ExternalServiceException {
        Properties runProperties = setupRunProperties(properties, t);
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

    Properties setupRunProperties(ObjectNode defaults, ScheduledTime t) {
        Properties runProperties = setupDefaultProperties(defaults, t);
        ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
        runProperties.setProperty(YEAR_PROP, formatter.formatYear(t));
        runProperties.setProperty(MONTH_PROP, formatter.formatMonth(t));
        runProperties.setProperty(DAY_PROP, formatter.formatDay(t));
        runProperties.setProperty(HOUR_PROP, formatter.formatHour(t));
        // TODO: set minute etc
        return runProperties;
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

}
