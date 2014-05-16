package com.collective.celos;

import java.io.File;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mozilla.javascript.NativeJavaObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JavaScriptFunctionsTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

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
    public void testCronScheduleRequiresExpr() throws Exception {
        expectMessage("cronSchedule()", "Undefined cron expression");
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
    public void testHDFSCheckFunctionDefaultFSWithDate() throws Exception {

        String root = tempFolder.getRoot().getPath();
        File triggerFile = new File(root, "2013-11-22/1500/_READY");
        triggerFile.getParentFile().mkdirs();
        triggerFile.createNewFile();

        Boolean result = (Boolean) runJSNativeResult("var CELOS_DEFAULT_HDFS = 'file:///'; hdfsCheck('" + root + "/${year}-${month}-${day}/${hour}00/_READY', new ScheduledTime(\"2013-11-22T15:00Z\"))");
        Assert.assertTrue(result);
    }


    @Test
    public void testHDFSCheckFunctionDefaults() throws Exception {

        String root = tempFolder.getRoot().getPath();
        File triggerFile = new File(root, "2013-11-22/1500/_READY");
        triggerFile.getParentFile().mkdirs();
        triggerFile.createNewFile();

        Boolean result = (Boolean) runJSNativeResult("var CELOS_DEFAULT_HDFS = 'file:///'; hdfsCheck('" + root + "/2013-11-22/1500/_READY" + "')");
        Assert.assertTrue(result);
    }

    @Test
    public void testHDFSCheckFunctionDefaultsFalse() throws Exception {

        String root = tempFolder.getRoot().getPath();
        File triggerFile = new File(root, "2013-11-22/1501/_READY");
        triggerFile.getParentFile().mkdirs();
        triggerFile.createNewFile();

        Boolean result = (Boolean) runJSNativeResult("var CELOS_DEFAULT_HDFS = 'file:///'; hdfsCheck('" + root + "/2013-11-22/1500/_READY" + "')");
        Assert.assertFalse(result);
    }

    @Test
    public void testHDFSCheckFunctionTrue() throws Exception {

        String root = tempFolder.getRoot().getPath();
        File triggerFile = new File(root, "2013-11-22/1500/_READY");
        triggerFile.getParentFile().mkdirs();
        triggerFile.createNewFile();

        Boolean result = (Boolean) runJSNativeResult("hdfsCheck('" + root + "/2013-11-22/1500/_READY" + "', ScheduledTime.now(), 'file:///')");
        Assert.assertTrue(result);
    }

    @Test
    public void testHDFSCheckFunctionFalse() throws Exception {

        String root = tempFolder.getRoot().getPath();
        File triggerFile = new File(root, "2013-11-22/1501/_READY");
        triggerFile.getParentFile().mkdirs();
        triggerFile.createNewFile();

        Boolean result = (Boolean) runJSNativeResult("hdfsCheck('" + root + "/2013-11-22/1500/_READY" + "', ScheduledTime.now(), 'file:///')");
        Assert.assertFalse(result);
    }

    @Test
    public void testHDFSCheckTrigger() throws Exception {
        HDFSCheckTrigger t = (HDFSCheckTrigger) runJS("hdfsCheckTrigger('/foo', 'file:///')");
        Assert.assertEquals("/foo", t.getRawPathString());
        Assert.assertEquals("file:///", t.getFsString());
    }
    
    @Test
    public void testHDFSCheckTriggerRequiresPath() {
        expectMessage("hdfsCheckTrigger()", "Undefined path");
    }

    @Test
    public void testHDFSCheckTriggerRequiresFs() {
        expectMessage("hdfsCheckTrigger('/foo')", "Undefined fs");
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
    public void testNotTriggerRequiresSubTrigger() throws Exception {
        expectMessage("notTrigger()", "Undefined sub trigger");
    }
    
    @Test
    public void testDelayTrigger() throws Exception {
        DelayTrigger t = (DelayTrigger) runJS("delayTrigger(25)");
        Assert.assertEquals(25, t.getSeconds());
    }
    
    @Test
    public void testDelayTriggerRequiresSeconds() throws Exception {
        expectMessage("delayTrigger()", "Undefined seconds");
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
    public void testSuccessTriggerRequiresWorkflowName() throws Exception {
        expectMessage("successTrigger()", "Undefined workflow name");
    }

    @Test
    public void testOozieExternalService() throws Exception {
        OozieExternalService s = (OozieExternalService) runJS("oozieExternalService({bla:'hello'}, 'http://foo')");
        Assert.assertEquals("http://foo", s.getOozieURL());
        ObjectNode props = new ObjectMapper().createObjectNode();
        props.put("bla", "hello");
        Assert.assertEquals(props, s.getProperties(new SlotID(new WorkflowID("foo"), ScheduledTime.now())));
    }
    
    @Test
    public void testOozieURLRequired() throws Exception {
        expectMessage("oozieExternalService({bla:'hello'})", "Undefined Oozie URL");
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
        props.put("a", "1");
        props.put("b", "3");
        props.put("c", "4");
        Assert.assertEquals(props, s.getProperties(new SlotID(new WorkflowID("foo"), ScheduledTime.now())));
    }

    @Test
    public void testOoziePropertiesFunction() throws Exception {
        String js = "var CELOS_DEFAULT_OOZIE_PROPERTIES = { a: '${year}' }; oozieExternalService(function(slot){ return { b: '${month}', c: new String(slot.getScheduledTime().minusYears(1).year()) }; }, 'http://oozie')";
        OozieExternalService s = (OozieExternalService) runJS(js);
        Properties props = new Properties();
        props.put("a", "2014");
        props.put("b", "03");
        props.put("c", "2013");
        ScheduledTime t = new ScheduledTime("2014-03-01T00:00Z");
        Assert.assertEquals(props, s.createRunProperties(s.getProperties(new SlotID(new WorkflowID("foo"), t)), Collections.<String, String>emptyMap(), t));
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
        OozieExternalService s = (OozieExternalService) runJS(js);
        Properties props = new Properties();
        props.put("a", "2014");
        props.put("b", "03");
        props.put("c", "2013");
        ScheduledTime t = new ScheduledTime("2014-03-01T00:00Z");
        Assert.assertEquals(props, s.createRunProperties(s.getProperties(new SlotID(new WorkflowID("foo"), t)), Collections.<String, String>emptyMap(), t));
    }
    
    // CommandExternalService
    
    @Test
    public void testCESCommandRequired() throws Exception {
        expectMessage("commandExternalService()", "Undefined command");
    }
    
    @Test
    public void testCESUsesCommand() throws Exception {
        CommandExternalService s = (CommandExternalService) runJS("commandExternalService('shutdown -h now')");
        Assert.assertEquals("shutdown -h now", s.getRawCommand());
    }
    
    private Object runJS(String js) throws Exception {
        WorkflowConfigurationParser parser = new WorkflowConfigurationParser(new File("unused"));
        // Evaluate JS function call
        NativeJavaObject result = (NativeJavaObject) parser.evaluateReader(new StringReader(js), "string", 1);
        return result.unwrap();
    }

    private Object runJSNativeResult(String js) throws Exception {
        WorkflowConfigurationParser parser = new WorkflowConfigurationParser(new File("unused"));
        // Evaluate JS function call
        return parser.evaluateReader(new StringReader(js), "string", 1);
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
