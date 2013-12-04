package com.collective.celos;

import java.util.Properties;

import org.apache.oozie.client.AuthOozieClient;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.client.WorkflowJob;

// TODO: mock AuthOozieClient and check that its methods are called with the right args
public class OozieExternalService implements ExternalService {
    
    public static final String OOZIE_URL_PROP = "oozie-url";
    public static final String YEAR_PROP = "year";
    public static final String MONTH_PROP = "month";
    public static final String DAY_PROP = "day";
    public static final String HOUR_PROP = "hour";
    
    private final OozieClient client;
    private final Properties properties;

    public OozieExternalService(Properties properties) {
        String oozieURL = properties.getProperty(OOZIE_URL_PROP);
        if (oozieURL == null) {
            throw new IllegalArgumentException(OOZIE_URL_PROP + " property not set.");
        }
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

    Properties setupRunProperties(Properties defaults, ScheduledTime t) {
        Properties runProperties = setupDefaultProperties(defaults, t);
        ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
        runProperties.setProperty(YEAR_PROP, formatter.formatYear(t));
        runProperties.setProperty(MONTH_PROP, formatter.formatMonth(t));
        runProperties.setProperty(DAY_PROP, formatter.formatDay(t));
        runProperties.setProperty(HOUR_PROP, formatter.formatHour(t));
        return runProperties;
    }

    private Properties setupDefaultProperties(Properties defaults, ScheduledTime t) {
        Properties props = new Properties();
        ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
        for (String name : defaults.stringPropertyNames()) {
            String value = defaults.getProperty(name);
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
