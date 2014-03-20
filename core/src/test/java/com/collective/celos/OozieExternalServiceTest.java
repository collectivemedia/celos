package com.collective.celos;

import java.util.Properties;

import com.collective.celos.api.ScheduledTime;
import com.collective.celos.api.Util;
import junit.framework.Assert;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class OozieExternalServiceTest {

    @Test(expected=IllegalArgumentException.class)
    public void oozieURLIsRequired() {
        new OozieExternalService(Util.newObjectNode());
    }

    @Test
    public void runPropertiesAreCorrectlySetup() {
        ObjectNode defaults = Util.newObjectNode();
        defaults.put("foo", "bar");
        defaults.put("uses-variables", "${year}-${month}-${day}-${hour}-${year}");
        defaults.put("another-one", "${year}-${month}-${day}T${hour}:${minute}:${second}.${millisecond}Z");
        ScheduledTime t = new ScheduledTime("2013-11-26T17:00:23.054Z");
        Properties runProperties = makeOozieExternalService().setupRunProperties(defaults, t);
        Assert.assertEquals("bar", runProperties.getProperty("foo"));
        Assert.assertEquals("2013-11-26-17-2013", runProperties.getProperty("uses-variables"));
        Assert.assertEquals("2013-11-26T17:00:23.054Z", runProperties.getProperty("another-one"));
        Assert.assertEquals("2013", runProperties.getProperty(OozieExternalService.YEAR_PROP));
        Assert.assertEquals("11", runProperties.getProperty(OozieExternalService.MONTH_PROP));
        Assert.assertEquals("26", runProperties.getProperty(OozieExternalService.DAY_PROP));
        Assert.assertEquals("17", runProperties.getProperty(OozieExternalService.HOUR_PROP));
        Assert.assertEquals("00", runProperties.getProperty(OozieExternalService.MINUTE_PROP));
        Assert.assertEquals("23", runProperties.getProperty(OozieExternalService.SECOND_PROP));
        Assert.assertEquals("054", runProperties.getProperty(OozieExternalService.MILLISECOND_PROP));
    }

    private OozieExternalService makeOozieExternalService() {
        ObjectNode props = Util.newObjectNode();
        props.put(OozieExternalService.OOZIE_URL_PROP, "http://example.com");
        return new OozieExternalService(props);
    }
    
}
