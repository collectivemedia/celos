package com.collective.celos;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

public class OozieExternalServiceTest {

    @Test(expected=IllegalArgumentException.class)
    public void oozieURLIsRequired() {
        new OozieExternalService(new Properties());
    }

    @Test
    public void runPropertiesAreCorrectlySetup() {
        Properties defaults = new Properties();
        defaults.setProperty("foo", "bar");
        ScheduledTime t = new ScheduledTime("2013-11-26T17:00Z");
        Properties runProperties = makeOozieExternalService().setupRunProperties(defaults, t);
        Assert.assertEquals("bar", runProperties.getProperty("foo"));
        Assert.assertEquals("2013", runProperties.getProperty(OozieExternalService.YEAR_PROP));
        Assert.assertEquals("11", runProperties.getProperty(OozieExternalService.MONTH_PROP));
        Assert.assertEquals("26", runProperties.getProperty(OozieExternalService.DAY_PROP));
        Assert.assertEquals("17", runProperties.getProperty(OozieExternalService.HOUR_PROP));
    }

    private OozieExternalService makeOozieExternalService() {
        Properties props = new Properties();
        props.setProperty(OozieExternalService.OOZIE_URL_PROP, "http://example.com");
        return new OozieExternalService(props);
    }
    
}
