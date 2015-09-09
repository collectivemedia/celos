/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class OozieExternalServiceTest {

    @Test(expected=NullPointerException.class)
    public void oozieURLIsRequired() {
        new OozieExternalService(null, PropertiesGenerator.EMPTY);
    }

    @Test(expected=NullPointerException.class)
    public void propertiesGeneratorIsRequired() {
        new OozieExternalService("http://oozie", null);
    }

    @Test
    public void runPropertiesAreCorrectlySetup() {
        ScheduledTime t = new ScheduledTime("2013-11-26T17:23Z");
        SlotID id = new SlotID(new WorkflowID("test"), t); 
        ObjectNode defaults = Util.newObjectNode();
        defaults.put("foo", "bar");
        defaults.put("uses-variables", "${year}-${month}-${day}-${hour}-${year}");
        defaults.put("another-one", "${year}-${month}-${day}T${hour}:${minute}:${second}.${millisecond}Z");
        Properties runProperties = makeOozieExternalService().setupRunProperties(defaults, id);
        Assert.assertEquals("bar", runProperties.getProperty("foo"));
        Assert.assertEquals("2013-11-26-17-2013", runProperties.getProperty("uses-variables"));
        Assert.assertEquals("2013-11-26T17:23:00.000Z", runProperties.getProperty("another-one"));
        Assert.assertEquals("2013", runProperties.getProperty(OozieExternalService.YEAR_PROP));
        Assert.assertEquals("11", runProperties.getProperty(OozieExternalService.MONTH_PROP));
        Assert.assertEquals("26", runProperties.getProperty(OozieExternalService.DAY_PROP));
        Assert.assertEquals("17", runProperties.getProperty(OozieExternalService.HOUR_PROP));
        Assert.assertEquals("23", runProperties.getProperty(OozieExternalService.MINUTE_PROP));
        Assert.assertEquals("00", runProperties.getProperty(OozieExternalService.SECOND_PROP));
        Assert.assertEquals("test@2013-11-26T17:23Z", runProperties.getProperty(OozieExternalService.WORKFLOW_NAME_PROP));
    }
    
    private OozieExternalService makeOozieExternalService() {
        return new OozieExternalService("http://example.com", PropertiesGenerator.EMPTY);
    }
    
}
