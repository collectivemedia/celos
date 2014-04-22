package com.collective.celos;

import java.io.File;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.tools.shell.Global;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JavaScriptFunctionsTest {

    private final JSONInstanceCreator creator = new JSONInstanceCreator();
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Test
    public void testHourlySchedule() throws Exception {
        HourlySchedule s = (HourlySchedule) runJSObject("hourlySchedule()");
    }

    @Test
    public void testMinutelySchedule() throws Exception {
        MinutelySchedule s = (MinutelySchedule) runJSObject("minutelySchedule()");
    }

    @Test
    public void testCronSchedule() throws Exception {
        CronSchedule s = (CronSchedule) runJS("cronSchedule('* 15 * * * ?')");
        Assert.assertEquals("* 15 * * * ?", s.getCronExpression());
    }
    
    @Test
    public void testSerialSchedulingStrategyDefault() throws Exception {
        SerialSchedulingStrategy s = (SerialSchedulingStrategy) runJS("serialSchedulingStrategy()");
        Assert.assertEquals(s.getConcurrencyLevel(), 1);
    }

    @Test
    public void testSerialSchedulingStrategy() throws Exception {
        SerialSchedulingStrategy s = (SerialSchedulingStrategy) runJS("serialSchedulingStrategy(5)");
        Assert.assertEquals(s.getConcurrencyLevel(), 5);
    }

    @Test
    public void testAlwaysTrigger() throws Exception {
        AlwaysTrigger t = (AlwaysTrigger) runJS("alwaysTrigger()");
    }

    @Test
    public void testHDFSCheckTrigger() throws Exception {
        HDFSCheckTrigger t = (HDFSCheckTrigger) runJS("hdfsCheckTrigger('/foo', 'file:///')");
        Assert.assertEquals("/foo", t.getRawPathString());
        Assert.assertEquals("file:///", t.getFsString());
    }
    
    @Test(expected=Exception.class)
    public void testHDFSCheckTriggerRequiresFS() throws Exception {
        runJS("hdfsCheckTrigger('/foo')");
    }
    
    @Test
    public void testHDFSCheckTriggerUsesDefaultNameNode() throws Exception {
        HDFSCheckTrigger t = (HDFSCheckTrigger) runJS("var CELOS_DEFAULT_HDFS = 'file:///'; hdfsCheckTrigger('/foo')");
        Assert.assertEquals("/foo", t.getRawPathString());
        Assert.assertEquals("file:///", t.getFsString());
    }

    @Test
    public void testAndTrigger() throws Exception {
        AndTrigger t = (AndTrigger) runJS("andTrigger(delayTrigger(1), alwaysTrigger())");
        DelayTrigger dt = (DelayTrigger) t.getTriggers().get(0);
        Assert.assertEquals(1, dt.getSeconds());
        AlwaysTrigger at = (AlwaysTrigger) t.getTriggers().get(1);
        Assert.assertEquals(2, t.getTriggers().size());
    }
    
    @Test
    public void testNotTrigger() throws Exception {
        NotTrigger t = (NotTrigger) runJS("notTrigger(alwaysTrigger())");
        AlwaysTrigger at = (AlwaysTrigger) t.getTrigger();
    }
    
    @Test
    public void testDelayTrigger() throws Exception {
        DelayTrigger t = (DelayTrigger) runJS("delayTrigger(25)");
        Assert.assertEquals(25, t.getSeconds());
    }
    
    @Test
    public void testShellCommandTrigger() throws Exception {
        CommandTrigger t = (CommandTrigger) runJS("commandTrigger('hello', 'this', 'is', 'cool')");
        Assert.assertEquals(Arrays.asList("hello", "this", "is", "cool"), t.getRawCommandElements());
    }

    @Test
    public void testSuccessTrigger() throws Exception {
        SuccessTrigger t = (SuccessTrigger) runJS("successTrigger('myworkflow')");
        Assert.assertEquals(new WorkflowID("myworkflow"), t.getTriggerWorkflowId());
    }

    @Test
    public void testOozieExternalService() throws Exception {
        OozieExternalService s = (OozieExternalService) runJSObject("oozieExternalService({bla:'hello'}, 'http://foo')");
        Assert.assertEquals("http://foo", s.getOozieURL());
        ObjectNode props = new ObjectMapper().createObjectNode();
        props.put("bla", "hello");
        Assert.assertEquals(props, s.getProperties(new SlotID(new WorkflowID("foo"), ScheduledTime.now())));
    }
    
    @Test(expected=Exception.class)
    public void testOozieURLRequired() throws Exception {
        OozieExternalService s = (OozieExternalService) runJSObject("oozieExternalService({bla:'hello'})");
    }

    @Test
    public void testOozieURLUsesDefault() throws Exception {
        String js = "var CELOS_DEFAULT_OOZIE = 'http://oooooozie'; oozieExternalService({bla:'hello'})";
        OozieExternalService s = (OozieExternalService) runJSObject(js);
        Assert.assertEquals("http://oooooozie", s.getOozieURL());
    }

    @Test
    public void testUsesOozieDefaultProperties() throws Exception {
        String js = "var CELOS_DEFAULT_OOZIE_PROPERTIES = {a:'1', b:'2'}; oozieExternalService({b: '3', c:'4'}, 'http://oozie')";
        OozieExternalService s = (OozieExternalService) runJSObject(js);
        Assert.assertEquals("http://oozie", s.getOozieURL());
        ObjectNode props = new ObjectMapper().createObjectNode();
        props.put("a", "1");
        props.put("b", "3");
        props.put("c", "4");
        Assert.assertEquals(props, s.getProperties(new SlotID(new WorkflowID("foo"), ScheduledTime.now())));
    }

    @Test
    public void testOoziePropertiesFunction() throws Exception {
        String js = "var CELOS_DEFAULT_OOZIE_PROPERTIES = { a: '${year}' }; oozieExternalService(function(slot){ return { b: '${month}', c: new String(slot.getScheduledTime().minusYears(1).year()) }; }, 'http://oozie')";
        OozieExternalService s = (OozieExternalService) runJSObject(js);
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
                "oozieExternalService(function(slot)" +
                "   { " +
                "       return { " +
                "           b: '${month}', " +
                "           c: slot.getScheduledTime().minusYears(1).year()" +
                "   }; " +
                "}, 'http://oozie')";
        OozieExternalService s = (OozieExternalService) runJSObject(js);
        Properties props = new Properties();
        props.put("a", "2014");
        props.put("b", "03");
        props.put("c", "2013");
        ScheduledTime t = new ScheduledTime("2014-03-01T00:00Z");
        Assert.assertEquals(props, s.setupDefaultProperties(s.getProperties(new SlotID(new WorkflowID("foo"), t)), t));
    }
    
    // CommandExternalService
    
    @Test(expected=Exception.class)
    public void testCESCommandRequired() throws Exception {
        runJS("commandExternalService()");
    }
    
    @Test
    public void testCESUsesCommand() throws Exception {
        CommandExternalService s = (CommandExternalService) runJS("commandExternalService('shutdown -h now')");
        Assert.assertEquals("shutdown -h now", s.getRawCommand());
    }
    
    private Object runJS(String js) throws Exception {
        WorkflowConfigurationParser parser = new WorkflowConfigurationParser(new File("unused"));
        // Evaluate JS function call
        Object result = parser.evaluateReader(new StringReader(js), "string", 1);
        // Turn result JSON into Java string
        String resultString = (String) NativeJSON.stringify(parser.getContext(), new Global(), result, null, null);
        // Parse JSON string and create object instance
        return creator.createInstance(mapper.readTree(resultString));
    }
    
    // FIXME: temporary hack, to be replaced when all utility functions return real objects, not JSON
    private Object runJSObject(String js) throws Exception {
        WorkflowConfigurationParser parser = new WorkflowConfigurationParser(new File("unused"));
        // Evaluate JS function call
        NativeJavaObject result = (NativeJavaObject) parser.evaluateReader(new StringReader(js), "string", 1);
        return result.unwrap();
    }
        
}
