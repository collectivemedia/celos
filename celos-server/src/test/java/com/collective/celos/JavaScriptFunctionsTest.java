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

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaObject;

import com.collective.celos.trigger.AlwaysTrigger;
import com.collective.celos.trigger.AndTrigger;
import com.collective.celos.trigger.DelayTrigger;
import com.collective.celos.trigger.HDFSCheckTrigger;
import com.collective.celos.trigger.NotTrigger;
import com.collective.celos.trigger.OffsetTrigger;
import com.collective.celos.trigger.OrTrigger;
import com.collective.celos.trigger.SuccessTrigger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;

public class JavaScriptFunctionsTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private final ObjectMapper mapper = new ObjectMapper();
    
    @Test
    public void testHourlySchedule() throws Exception {
        HourlySchedule s = (HourlySchedule) runJS("celos.hourlySchedule()");
    }

    @Test
    public void testMinutelySchedule() throws Exception {
        MinutelySchedule s = (MinutelySchedule) runJS("celos.minutelySchedule()");
    }

    @Test
    public void testCronSchedule() throws Exception {
        CronSchedule s = (CronSchedule) runJS("celos.cronSchedule('* 15 * * * ?')");
        Assert.assertEquals("* 15 * * * ?", s.getCronExpression());
    }
    
    @Test
    public void testDependentSchedule() throws Exception {
        DependentSchedule s = (DependentSchedule) runJS("celos.dependentSchedule('foo')");
        Assert.assertEquals(new WorkflowID("foo"), s.getOtherWorkflowID());
    }

    @Test
    public void testCronScheduleRequiresExpr() throws Exception {
        expectMessage("celos.cronSchedule()", "Undefined cron expression");
    }
    
    @Test
    public void testSerialSchedulingStrategyDefault() throws Exception {
        SerialSchedulingStrategy s = (SerialSchedulingStrategy) runJS("celos.serialSchedulingStrategy()");
        Assert.assertEquals(s.getConcurrencyLevel(), 1);
    }

    @Test
    public void testSerialSchedulingStrategy() throws Exception {
        SerialSchedulingStrategy s = (SerialSchedulingStrategy) runJS("celos.serialSchedulingStrategy(5)");
        Assert.assertEquals(s.getConcurrencyLevel(), 5);
    }

    @Test
    public void testAlwaysTrigger() throws Exception {
        AlwaysTrigger t = (AlwaysTrigger) runJS("celos.alwaysTrigger()");
    }

    @Test
    public void testHDFSCheckTrigger() throws Exception {
        HDFSCheckTrigger t = (HDFSCheckTrigger) runJS("celos.hdfsCheckTrigger('/foo', 'file:///')");
        Assert.assertEquals("/foo", t.getRawPathString());
        Assert.assertEquals("file:///", t.getFsString());
    }
    
    @Test
    public void testHDFSCheckTriggerRequiresPath() {
        expectMessage("celos.hdfsCheckTrigger()", "Undefined path");
    }

    @Test
    public void testHDFSCheckTriggerRequiresFs() {
        expectMessage("celos.hdfsCheckTrigger('/foo')", "Undefined fs");
    }

    @Test
    public void testHDFSCheckTriggerUsesDefaultNameNode() throws Exception {
        HDFSCheckTrigger t = (HDFSCheckTrigger) runJS("var CELOS_DEFAULT_HDFS = 'file:///'; celos.hdfsCheckTrigger('/foo')");
        Assert.assertEquals("/foo", t.getRawPathString());
        Assert.assertEquals("file:///", t.getFsString());
    }

    @Test
    public void testAndTrigger() throws Exception {
        AndTrigger t = (AndTrigger) runJS("celos.andTrigger(celos.delayTrigger(1), celos.alwaysTrigger())");
        DelayTrigger dt = (DelayTrigger) t.getTriggers().get(0);
        Assert.assertEquals(1, dt.getSeconds());
        AlwaysTrigger at = (AlwaysTrigger) t.getTriggers().get(1);
        Assert.assertEquals(2, t.getTriggers().size());
    }
    
    @Test
    public void testOrTrigger() throws Exception {
        OrTrigger t = (OrTrigger) runJS("celos.orTrigger(celos.delayTrigger(1), celos.alwaysTrigger())");
        DelayTrigger dt = (DelayTrigger) t.getTriggers().get(0);
        Assert.assertEquals(1, dt.getSeconds());
        AlwaysTrigger at = (AlwaysTrigger) t.getTriggers().get(1);
        Assert.assertEquals(2, t.getTriggers().size());
    }
    
    @Test
    public void testNotTrigger() throws Exception {
        NotTrigger t = (NotTrigger) runJS("celos.notTrigger(celos.alwaysTrigger())");
        AlwaysTrigger at = (AlwaysTrigger) t.getTrigger();
    }
    
    @Test
    public void testNotTriggerRequiresSubTrigger() throws Exception {
        expectMessage("celos.notTrigger()", "Undefined sub trigger");
    }

    @Test
    public void testOffsetTrigger() throws Exception {
        OffsetTrigger t = (OffsetTrigger) runJS("celos.offsetTrigger(25, celos.alwaysTrigger())");
        Assert.assertEquals(25, t.getSeconds());
        Assert.assertEquals(AlwaysTrigger.class, t.getTrigger().getClass());
    }

    @Test
    public void testOffsetTriggerRequiresSeconds() throws Exception {
        expectMessage("celos.offsetTrigger()", "Undefined seconds");
    }

    @Test
    public void testOffsetTriggerRequiresSecondsAndTrigger() throws Exception {
        expectMessage("celos.offsetTrigger(25)", "Undefined trigger");
    }


    @Test
    public void testDelayTrigger() throws Exception {
        DelayTrigger t = (DelayTrigger) runJS("celos.delayTrigger(25)");
        Assert.assertEquals(25, t.getSeconds());
    }
    
    @Test
    public void testDelayTriggerRequiresSeconds() throws Exception {
        expectMessage("celos.delayTrigger()", "Undefined seconds");
    }

    @Test
    public void testSuccessTrigger() throws Exception {
        SuccessTrigger t = (SuccessTrigger) runJS("celos.successTrigger('myworkflow')");
        Assert.assertEquals(new WorkflowID("myworkflow"), t.getTriggerWorkflowId());
    }
    
    @Test
    public void testSuccessTriggerRequiresWorkflowName() throws Exception {
        expectMessage("celos.successTrigger()", "Undefined workflow name");
    }

    @Test
    public void testOozieExternalService() throws Exception {
        OozieExternalService s = (OozieExternalService) runJS("celos.oozieExternalService({bla:'hello'}, 'http://foo')");
        Assert.assertEquals("http://foo", s.getOozieURL());
        ObjectNode props = new ObjectMapper().createObjectNode();
        props.put("bla", "hello");
        Assert.assertEquals(props, s.getProperties(new SlotID(new WorkflowID("foo"), ScheduledTime.now())));
    }
    
    @Test
    public void testOozieURLRequired() throws Exception {
        expectMessage("celos.oozieExternalService({bla:'hello'})", "Undefined Oozie URL");
    }

    @Test
    public void testOozieURLUsesDefault() throws Exception {
        String js = "var CELOS_DEFAULT_OOZIE = 'http://oooooozie'; celos.oozieExternalService({bla:'hello'})";
        OozieExternalService s = (OozieExternalService) runJS(js);
        Assert.assertEquals("http://oooooozie", s.getOozieURL());
    }

    @Test
    public void testUsesOozieDefaultProperties() throws Exception {
        String js = "var CELOS_DEFAULT_OOZIE_PROPERTIES = {a:'1', b:'2'}; celos.oozieExternalService({b: '3', c:'4'}, 'http://oozie')";
        OozieExternalService s = (OozieExternalService) runJS(js);
        Assert.assertEquals("http://oozie", s.getOozieURL());
        ObjectNode props = new ObjectMapper().createObjectNode();
        props.put("a", "1");
        props.put("b", "3");
        props.put("c", "4");
        Assert.assertEquals(props, s.getProperties(new SlotID(new WorkflowID("foo"), ScheduledTime.now())));
    }

    @Test
    public void testOoziePropertiesFunction() throws Exception {
        String js = "var CELOS_DEFAULT_OOZIE_PROPERTIES = { a: '${year}' }; celos.oozieExternalService(function(slot){ return { b: '${month}', c: new String(slot.getScheduledTime().minusYears(1).year()) }; }, 'http://oozie')";
        OozieExternalService s = (OozieExternalService) runJS(js);
        Properties props = new Properties();
        props.put("a", "2014");
        props.put("b", "03");
        props.put("c", "2013");
        ScheduledTime t = new ScheduledTime("2014-03-01T00:00Z");
        Assert.assertEquals(props, s.setupDefaultProperties(s.getProperties(new SlotID(new WorkflowID("foo"), t)), t));
    }

    @Test
    public void testOoziePropertiesFunctionWithJavaObject() throws Exception {
        String js = "var CELOS_DEFAULT_OOZIE_PROPERTIES = " +
                "{ " +
                "   a: '${year}' " +
                "}; " +
                "celos.oozieExternalService(function(slot)" +
                "   { " +
                "       return { " +
                "           b: '${month}', " +
                "           c: slot.getScheduledTime().minusYears(1).year()" +
                "   }; " +
                "}, 'http://oozie')";
        OozieExternalService s = (OozieExternalService) runJS(js);
        Properties props = new Properties();
        props.put("a", "2014");
        props.put("b", "03");
        props.put("c", "2013");
        ScheduledTime t = new ScheduledTime("2014-03-01T00:00Z");
        Assert.assertEquals(props, s.setupDefaultProperties(s.getProperties(new SlotID(new WorkflowID("foo"), t)), t));
    }
    
    @Test
    public void replaceTimeVariablesWorks() throws Exception {
        String s = (String) runJS("celos.replaceTimeVariables('${year}-${month}-${day}T${hour}:${minute}:${second}Z ${year}', new Packages.com.collective.celos.ScheduledTime('2014-05-12T19:33:01Z'))");
        Assert.assertEquals("2014-05-12T19:33:01Z 2014", s);
    }


    @Test
    public void testHdfsPathFunction() throws Exception {
        String js = "var HDFS_PREFIX_JS_VAR = '/user/celos/test'; \n" +
                "celos.hdfsPath('/path')";
        String s = (String) runJS(js);
        Assert.assertEquals(s, "/user/celos/test/path");
    }

    @Test
    public void testHdfsPathFunctionNoPrefix() throws Exception {
        String js = "celos.hdfsPath('/path')";
        String s = (String) runJS(js);
        Assert.assertEquals(s, "/path");
    }

    @Test
    public void testIsRunningInTestModeFalse() throws Exception {
        String js = "celos.isRunningInTestMode()";
        Boolean s = (Boolean) runJS(js);
        Assert.assertEquals(s, false);
    }

    @Test
    public void testIsRunningInTestModeTrue() throws Exception {
        String js = "HDFS_PREFIX_JS_VAR = 'some';" +
                "celos.isRunningInTestMode()";
        Boolean s = (Boolean) runJS(js);
        Assert.assertEquals(s, true);
    }
    
    @Test
    public void testHdfsCheckNotExists() throws Exception {
        String js = "var CELOS_DEFAULT_HDFS = ''; " +
                "var schTime = new Packages.com.collective.celos.ScheduledTime('2014-05-12T19:33:01Z');" +
                "var workflowId = new Packages.com.collective.celos.WorkflowID('id');" +
                "var slotId = new Packages.com.collective.celos.SlotID(workflowId, schTime);" +
                "celos.hdfsCheck('/path', slotId)";

        Boolean s = (Boolean) runJS(js);
        Assert.assertEquals(s, false);

    }

    @Test
    public void testHdfsCheckExists() throws Exception {
        String js = "var CELOS_DEFAULT_HDFS = ''; " +
                "var schTime = new Packages.com.collective.celos.ScheduledTime('2014-05-12T19:33:01Z');" +
                "var workflowId = new Packages.com.collective.celos.WorkflowID('id');" +
                "var slotId = new Packages.com.collective.celos.SlotID(workflowId, schTime);" +
                "celos.hdfsCheck('file:///tmp', slotId)";

        Boolean s = (Boolean) runJS(js);
        Assert.assertEquals(s, true);

    }

    @Test
    public void testHdfsCheckExists2() throws Exception {
        String js = "var CELOS_DEFAULT_HDFS = ''; " +
                "var schTime = new Packages.com.collective.celos.ScheduledTime('2014-05-12T19:33:01Z');" +
                "var workflowId = new Packages.com.collective.celos.WorkflowID('id');" +
                "var slotId = new Packages.com.collective.celos.SlotID(workflowId, schTime);" +
                "celos.hdfsCheck('/tmp', slotId, 'file:///')";

        Boolean s = (Boolean) runJS(js);
        Assert.assertEquals(s, true);

    }

    @Test(expected = JavaScriptException.class)
    public void testHdfsCheckWrongType() throws Exception {
        String js = "var CELOS_DEFAULT_HDFS = ''; " +
                "var schTime = new Packages.com.collective.celos.ScheduledTime('2014-05-12T19:33:01Z');" +
                "var workflowId = new Packages.com.collective.celos.WorkflowID('id');" +
                "var slotId = 'slot';" +
                "celos.hdfsCheck('file:///tmp', slotId)";

        Boolean s = (Boolean) runJS(js);
        Assert.assertEquals(s, true);

    }

    @Test
    public void testRegisters() throws Exception {
        WorkflowConfigurationParser parser = createParser();
        StateDatabaseConnection conn = new MemoryStateDatabase().openConnection();
        
        ObjectNode v1 = mapper.createObjectNode();
        v1.put("foo", "bar-Iñtërnâtiônàlizætiøn");
        ObjectNode v2 = mapper.createObjectNode();
        v2.put("quux", "meh-Iñtërnâtiônàlizætiøn");
        ObjectNode v3 = mapper.createObjectNode();
        v3.put("bla", "baz-Iñtërnâtiônàlizætiøn");
        conn.putRegister(new BucketID("b1-Iñtërnâtiônàlizætiøn"), new RegisterKey("k1-Iñtërnâtiônàlizætiøn"), v1);
        conn.putRegister(new BucketID("b1-Iñtërnâtiônàlizætiøn"), new RegisterKey("k2-Iñtërnâtiônàlizætiøn"), v2);
        conn.putRegister(new BucketID("b2-Iñtërnâtiônàlizætiøn"), new RegisterKey("k3-Iñtërnâtiônàlizætiøn"), v3);
        File f = new File("src/test/resources/js-tests/test-registers.js");
        parser.evaluateReader(new FileReader(f), f.getName(), conn);
    }
        
    private Object runJS(String js) throws Exception {
        WorkflowConfigurationParser parser = createParser();
        // Evaluate JS function call
        Object jsResult = parser.evaluateReader(new StringReader(js), "string", new MemoryStateDatabase().openConnection());
        if (jsResult instanceof NativeJavaObject) {
            return ((NativeJavaObject) jsResult).unwrap();
        } else {
            return jsResult;
        }
    }

    private Object runJSNativeResult(String js) throws Exception {
        WorkflowConfigurationParser parser = createParser();
        // Evaluate JS function call
        return parser.evaluateReader(new StringReader(js), "string", new MemoryStateDatabase().openConnection());
    }

    private WorkflowConfigurationParser createParser() throws Exception {
        WorkflowConfigurationParser parser = new WorkflowConfigurationParser(new File("unused"), ImmutableMap.<String, String>of());
        return parser;
    }

    private void expectMessage(String js, String string) throws AssertionError {
        try {
            runJS(js);
        } catch(Exception e) {
            if (e.getMessage().contains(string)) {
                return;
            }
        }
        throw new AssertionError();
    }
    
}
