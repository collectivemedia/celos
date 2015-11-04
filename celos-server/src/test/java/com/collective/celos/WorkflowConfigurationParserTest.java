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

import com.collective.celos.trigger.Trigger;
import com.collective.celos.trigger.TriggerStatus;
import com.google.common.collect.ImmutableMap;

import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.NativeJavaObject;

import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.SortedSet;

public class WorkflowConfigurationParserTest {

    @Test
    public void emptyDirCreatesEmptyWorkflowConfiguration() throws Exception {
        WorkflowConfiguration cfg = parseDir("empty");
        Assert.assertEquals(0, cfg.getWorkflows().size());
    }

    @Test
    public void failsOnIllFormattedFile() throws Exception {
        expectMessage("ill-formatted", "missing ; before statement");
    }

    public static class TestSchedule implements Schedule {
        @Override
        public SortedSet<ScheduledTime> getScheduledTimes(Scheduler scheduler, ScheduledTime start, ScheduledTime end) {
            return null;
        }
    }
    
    public static class TestSchedulingStrategy implements SchedulingStrategy {
        @Override
        public List<SlotState> getSchedulingCandidates(List<SlotState> states) {
            return null;
        }
    }

    public static class TestExternalService implements ExternalService {
        @Override
        public String submit(SlotID id) throws ExternalServiceException {
            return null;
        }
        @Override
        public void start(SlotID id, String externalID) throws ExternalServiceException {
        }
        @Override
        public ExternalStatus getStatus(SlotID slotId, String externalWorkflowID) throws ExternalServiceException {
            return null;
        }

        @Override
        public void kill(SlotID id, String externalID) throws ExternalServiceException {

        }
    }
        
    public static class TestTrigger extends Trigger {

        @Override
        public TriggerStatus getTriggerStatus(StateDatabaseConnection connection, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
            return makeTriggerStatus(false, "TestTrigger");
        }

    }

    @Test
    public void propertiesAreCorrectlySet() throws Exception {
        WorkflowConfiguration cfg = parseFile("properties-test");
        
        Workflow wf1 = cfg.findWorkflow(new WorkflowID("workflow-1"));
        Assert.assertEquals("workflow-1", wf1.getID().toString());
        Assert.assertEquals(55, wf1.getMaxRetryCount());
        Assert.assertEquals(Workflow.DEFAULT_START_TIME, wf1.getStartTime());
        Assert.assertEquals(Workflow.DEFAULT_WAIT_TIMEOUT_SECONDS, wf1.getWaitTimeoutSeconds());
        
        Workflow wf2 = cfg.findWorkflow(new WorkflowID("workflow-2"));
        Assert.assertEquals("workflow-2", wf2.getID().toString());
        Assert.assertEquals(66, wf2.getMaxRetryCount());
        Assert.assertEquals(new ScheduledTime("2014-03-10T12:34:56.789Z"), wf2.getStartTime());
        Assert.assertEquals(23, wf2.getWaitTimeoutSeconds());
    }

    @Test
    public void workflowInfoWF1() throws Exception {
        WorkflowConfiguration cfg = parseFile("workflow-info-test");
        WorkflowInfo workflowInfo = cfg.findWorkflow(new WorkflowID("workflow-1")).getWorkflowInfo();

        Assert.assertNull(workflowInfo.getUrl());
        Assert.assertTrue(workflowInfo.getContacts().isEmpty());
    }

    @Test
    public void workflowInfoWF2() throws Exception {
        WorkflowConfiguration cfg = parseFile("workflow-info-test");

        WorkflowInfo workflowInfo = cfg.findWorkflow(new WorkflowID("workflow-2")).getWorkflowInfo();
        Assert.assertEquals(new URL("http://collective.com/workflow"), workflowInfo.getUrl());
        Assert.assertEquals(1, workflowInfo.getContacts().size());
        Assert.assertEquals("John Doe", workflowInfo.getContacts().get(0).getName());
        Assert.assertEquals(URI.create("john.doe@collective.com"), workflowInfo.getContacts().get(0).getEmail());
    }

    @Test
    public void workflowInfoWFNoName() throws Exception {
        WorkflowConfiguration cfg = parseFile("workflow-info-test");

        WorkflowInfo workflowInfo = cfg.findWorkflow(new WorkflowID("workflow-3")).getWorkflowInfo();
        Assert.assertEquals(new URL("http://collective.com/workflow"), workflowInfo.getUrl());
        Assert.assertEquals(1, workflowInfo.getContacts().size());
        Assert.assertNull(workflowInfo.getContacts().get(0).getName());
        Assert.assertEquals(URI.create("john.doe@collective.com"), workflowInfo.getContacts().get(0).getEmail());
    }

    @Test
    public void workflowInfoNoEmail() throws Exception {
        WorkflowConfiguration cfg = parseFile("workflow-info-test");

        WorkflowInfo workflowInfo = cfg.findWorkflow(new WorkflowID("workflow-4")).getWorkflowInfo();
        Assert.assertEquals(new URL("http://collective.com/workflow"), workflowInfo.getUrl());
        Assert.assertEquals(1, workflowInfo.getContacts().size());
        Assert.assertEquals("John Doe", workflowInfo.getContacts().get(0).getName());
        Assert.assertNull(workflowInfo.getContacts().get(0).getEmail());
    }

    @Test
    public void workflowInfoNoContacts() throws Exception {
        WorkflowConfiguration cfg = parseFile("workflow-info-test");

        WorkflowInfo workflowInfo = cfg.findWorkflow(new WorkflowID("workflow-5")).getWorkflowInfo();
        Assert.assertEquals(new URL("http://collective.com/workflow"), workflowInfo.getUrl());
        Assert.assertTrue(workflowInfo.getContacts().isEmpty());
    }

    @Test
    public void canFindWorkflow() throws Exception {
        WorkflowConfiguration cfg = parseDir("properties-test");
        Workflow wf1 = cfg.findWorkflow(new WorkflowID("workflow-1"));
        Assert.assertEquals(55, wf1.getMaxRetryCount());
        Assert.assertNull(cfg.findWorkflow(new WorkflowID("foobar")));
    }
    
    @Test
    public void idMustExist() throws Exception {
        expectMessage("id-missing", "Workflow ID must be a string");
    }

    @Test
    public void idMustBeAString() throws Exception {
        expectMessage("id-not-a-string", "Workflow ID must be a string");
    }
    
    @Test
    public void maxRetryCountUsesZeroIfNotSet() throws Exception {
        Workflow wf = parseDir("maxretrycount-missing").findWorkflow(new WorkflowID("workflow-1"));
        Assert.assertEquals(0, wf.getMaxRetryCount());
    }
    
    @Test
    public void maxRetryCountMustBeANumber() throws Exception {
        expectMessage("maxretrycount-not-a-number", "Cannot convert foo to java.lang.Integer");
    }
    
    @Test
    public void doesntFailOnSingleWorkflowError() throws Exception {
        WorkflowConfiguration cfg = parseDir("single-error");
        Assert.assertEquals(2, cfg.getWorkflows().size());
        Assert.assertNotNull(cfg.findWorkflow(new WorkflowID("workflow-1")));
        Assert.assertNotNull(cfg.findWorkflow(new WorkflowID("workflow-3")));
    }
    
    @Test
    public void doesntAllowDuplicateIDs() throws Exception {
        // Directory contains 2 workflows, but one will be dropped because of duplicate ID.
        WorkflowConfiguration cfg = parseFile("duplicate-ids");
        Assert.assertEquals(1, cfg.getWorkflows().size());
    }
    
    @Test
    public void scopesAreSeparate() throws Exception {
        parseNamedFile("separate-scopes", "workflow-1");
        parseNamedFile("separate-scopes", "workflow-2");
    }
    
    @Test
    public void defaultsWork() throws Exception {
        parseNamedFile("uses-defaults", "workflow-1");
    }

    @Test
    public void evaluatesAdditionalVar() throws Exception {
        StateDatabaseConnection conn = new MemoryStateDatabase().openConnection();
        WorkflowConfigurationParser parser = new WorkflowConfigurationParser(new File("unused"), ImmutableMap.of("var1", "val1"), conn);
        // Evaluate JS function call
        Object jsResult = parser.evaluateReader(new StringReader("var1"), "string");
        Assert.assertEquals(jsResult, "val1");
    }

    @Test
    public void testTakesDefaultUsername() throws Exception {

        URL resource = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/defaults-oozie-props");
        File defaults = new File(resource.toURI());

        StateDatabaseConnection conn = new MemoryStateDatabase().openConnection();
        WorkflowConfigurationParser parser = new WorkflowConfigurationParser(defaults, ImmutableMap.of("var1", "val1"), conn);

        String func = "function (slotId) {" +
                "        return {" +
                "            \"oozie.wf.application.path\": \"/workflow.xml\"," +
                "            \"inputDir\": \"/input\"," +
                "            \"outputDir\": \"/output\"" +
                "        }" +
                "    }";

        String str = "importDefaults(\"test\"); celos.makePropertiesGen(" + func + "); ";
        NativeJavaObject jsResult = (NativeJavaObject) parser.evaluateReader(new StringReader(str), "string");
        PropertiesGenerator generator = (PropertiesGenerator) jsResult.unwrap();
        Assert.assertEquals(generator.getProperties(null).get("user.name").asText(), "default");
    }

    @Test
    public void testTakesChangesUsername() throws Exception {

        URL resource = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/defaults-oozie-props");
        File defaults = new File(resource.toURI());

        StateDatabaseConnection conn = new MemoryStateDatabase().openConnection();
        WorkflowConfigurationParser parser = new WorkflowConfigurationParser(defaults, ImmutableMap.of("CELOS_USER_JS_VAR", "nameIsChanged"), conn);

        String func = "function (slotId) {" +
                "        return {" +
                "            \"oozie.wf.application.path\": \"/workflow.xml\"," +
                "            \"inputDir\": \"/input\"," +
                "            \"outputDir\": \"/output\"" +
                "        }" +
                "    }";

        String str = "importDefaults(\"test\"); celos.makePropertiesGen(" + func + "); ";
        NativeJavaObject jsResult = (NativeJavaObject) parser.evaluateReader(new StringReader(str), "string");
        PropertiesGenerator generator = (PropertiesGenerator) jsResult.unwrap();
        Assert.assertEquals(generator.getProperties(null).get("user.name").asText(), "nameIsChanged");
    }


    @Test
    public void doesntEvaluateAdditionalVar() throws Exception {
        StateDatabaseConnection conn = new MemoryStateDatabase().openConnection();
        WorkflowConfigurationParser parser = new WorkflowConfigurationParser(new File("unused"), ImmutableMap.<String, String>of(), conn);
        try {
            parser.evaluateReader(new StringReader("var1"), "string");
        } catch (Exception e) {
            if (e.getMessage().contains("\"var1\" is not defined")) {
                return;
            }
        }
        Assert.fail();
    }

    public static WorkflowConfiguration parseFile(String label) throws Exception {
        return parseNamedFile(label, "workflow-1");
    }

    private static WorkflowConfiguration parseNamedFile(String label, String workflowName) throws URISyntaxException,
            Exception {
        File dir = getConfigurationDir(label);
        File defaults = getDefaultsDir();
        File workflow = new File(dir, workflowName + "." + WorkflowConfigurationParser.WORKFLOW_FILE_EXTENSION);
        StateDatabaseConnection conn = new MemoryStateDatabase().openConnection();
        WorkflowConfigurationParser workflowConfigurationParser = new WorkflowConfigurationParser(defaults, ImmutableMap.<String, String>of(), conn);
        workflowConfigurationParser.parseFile(workflow);
        return workflowConfigurationParser.getWorkflowConfiguration();
    }
    
    public static WorkflowConfiguration parseDir(String label) throws Exception {
        File dir = getConfigurationDir(label);
        File defaults = getDefaultsDir();
        StateDatabaseConnection conn = new MemoryStateDatabase().openConnection();
        return new WorkflowConfigurationParser(defaults, ImmutableMap.<String, String>of(), conn).parseConfiguration(dir).getWorkflowConfiguration();
    }

    public static File getConfigurationDir(String label) throws URISyntaxException {
        String path = "com/collective/celos/workflow-configuration-test/" + label;
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        return new File(resource.toURI());
    }
    
    public static File getDefaultsDir() throws URISyntaxException {
        String path = "com/collective/celos/defaults";
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        return new File(resource.toURI());
    }

    private void expectMessage(String label, String message) throws AssertionError {
        try {
            parseFile(label);
        } catch(Exception e) {
            if (e.getMessage().contains(message)) {
                return;
            }
        }
        throw new AssertionError();
    }
    

}
