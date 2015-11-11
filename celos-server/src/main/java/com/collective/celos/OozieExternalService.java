package com.collective.celos;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Properties;

public class OozieExternalService implements ExternalService {

    public static final String YEAR_PROP = "year";
    public static final String MONTH_PROP = "month";
    public static final String DAY_PROP = "day";
    public static final String HOUR_PROP = "hour";
    public static final String MINUTE_PROP = "minute";
    public static final String SECOND_PROP = "second";
    public static final String WORKFLOW_NAME_PROP = "celosWorkflowName";

    private final String oozieURL;
    private PropertiesGenerator gen;

    public OozieExternalService(String oozieURL, PropertiesGenerator gen) {
        this.oozieURL = Util.requireNonNull(oozieURL);
        this.gen = Util.requireNonNull(gen);
    }

    @Override
    public String submit(SlotID id) throws ExternalServiceException {
        try {
            Properties runProperties = setupRunProperties(getProperties(id), id);
            return id.toString();
        } catch (Exception e) {
            throw new ExternalServiceException(e);
        }
    }

    public ObjectNode getProperties(SlotID id) {
        return gen.getProperties(id);
    }

    @Override
    public void start(SlotID unused, String externalID) throws ExternalServiceException {
    }

    @Override
    public void kill(SlotID unused, String externalID) throws ExternalServiceException {
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
        for (Iterator<String> names = defaults.fieldNames(); names.hasNext(); ) {
            String name = names.next();
            String value = defaults.get(name).textValue();
            props.setProperty(name, formatter.replaceTimeTokens(value, t));
        }
        return props;
    }

    @Override
    public ExternalStatus getStatus(SlotID unused, String jobId) throws ExternalServiceException {
        return new OozieExternalStatus("SUCCEEDED");
    }

    public String getOozieURL() {
        return oozieURL;
    }

}
