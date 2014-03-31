package com.collective.celos;

import java.io.File;
import java.io.StringReader;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.tools.shell.Global;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JavaScriptFunctionsTest {

    private final JSONInstanceCreator creator = new JSONInstanceCreator();
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Test
    public void testHourlySchedule() throws Exception {
        HourlySchedule s = (HourlySchedule) runJS("hourlySchedule()");
    }

    @Test
    public void testMinutelySchedule() throws Exception {
        MinutelySchedule s = (MinutelySchedule) runJS("minutelySchedule()");
    }

    @Test
    public void testCronSchedule() throws Exception {
        CronSchedule s = (CronSchedule) runJS("cronSchedule('* 15 * * * ?')");
        Assert.assertEquals("* 15 * * * ?", s.getCronExpression());
    }
    
    @Test
    public void testSerialSchedulingStrategy() throws Exception {
        SerialSchedulingStrategy s = (SerialSchedulingStrategy) runJS("serialSchedulingStrategy()");
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
        OozieExternalService s = (OozieExternalService) runJS("oozieExternalService({bla:'hello'}, 'http://foo')");
        Assert.assertEquals("http://foo", s.getOozieURL());
        ObjectNode props = new ObjectMapper().createObjectNode();
        props.put(OozieExternalService.OOZIE_URL_PROP, "http://foo");
        props.put("bla", "hello");
        Assert.assertEquals(props, s.getProperties());
    }
    
    @Test(expected=Exception.class)
    public void testOozieURLRequired() throws Exception {
        runJS("oozieExternalService({bla:'hello'})");
    }

    @Test
    public void testOozieURLUsesDefault() throws Exception {
        String js = "var CELOS_DEFAULT_OOZIE = 'http://oooooozie'; oozieExternalService({bla:'hello'})";
        OozieExternalService s = (OozieExternalService) runJS(js);
        Assert.assertEquals("http://oooooozie", s.getOozieURL());
    }

    @Test
    public void testUsesOozieDefaultProperties() throws Exception {
        String js = "var CELOS_DEFAULT_OOZIE_PROPERTIES = {a:'1', b:'2'}; oozieExternalService({b: '3', c:'4'}, 'http://oozie')";
        OozieExternalService s = (OozieExternalService) runJS(js);
        Assert.assertEquals("http://oozie", s.getOozieURL());
        ObjectNode props = new ObjectMapper().createObjectNode();
        props.put(OozieExternalService.OOZIE_URL_PROP, "http://oozie");
        props.put("a", "1");
        props.put("b", "3");
        props.put("c", "4");
        Assert.assertEquals(props, s.getProperties());
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
    
}
