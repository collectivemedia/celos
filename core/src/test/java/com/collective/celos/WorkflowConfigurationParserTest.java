package com.collective.celos;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.SortedSet;

import com.collective.celos.api.Schedule;
import com.collective.celos.api.ScheduledTime;
import com.collective.celos.api.Trigger;
import com.collective.celos.api.Util;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class WorkflowConfigurationParserTest {

    @Test
    public void emptyDirCreatesEmptyWorkflowConfiguration() throws Exception {
        WorkflowConfiguration cfg = parseDir("empty");
        Assert.assertEquals(0, cfg.getWorkflows().size());
    }

    @Test(expected=Exception.class)
    public void failsOnIllFormattedFile() throws Exception {
        parseFile("ill-formatted");
    }
    
    public static class ScheduleWithoutPropertiesConstructor implements Schedule {
        @Override
        public SortedSet<ScheduledTime> getScheduledTimes(ScheduledTime start, ScheduledTime end) {
            return null;
        }
    }

    @Test
    public void everyObjectMustHavePropertiesConstructor() throws Exception {
        try {
            parseFile("no-properties-constructor");
        } catch(Exception e) {
            Assert.assertTrue(e.getMessage().contains("Constructor with ObjectNode argument not found for com.collective.celos.WorkflowConfigurationParserTest$ScheduleWithoutPropertiesConstructor"));
        }
    }
    
    public static class RemembersProperties {
        private ObjectNode properties;
        protected RemembersProperties(ObjectNode props) {
            this.properties = Util.requireNonNull(props);
        }
        public ObjectNode getProperties() {
            return properties;
        }
    }

    public static class TestSchedule extends RemembersProperties implements Schedule {
        public TestSchedule(ObjectNode properties) { super(properties); }
        @Override
        public SortedSet<ScheduledTime> getScheduledTimes(ScheduledTime start, ScheduledTime end) {
            return null;
        }
    }
    
    public static class TestSchedulingStrategy extends RemembersProperties implements SchedulingStrategy {
        public TestSchedulingStrategy(ObjectNode properties) { super(properties); }
        @Override
        public List<SlotState> getSchedulingCandidates(List<SlotState> states) {
            return null;
        }
    }

    public static class TestExternalService extends RemembersProperties implements ExternalService {
        public TestExternalService(ObjectNode properties) { super(properties); }
        @Override
        public String submit(ScheduledTime t) throws ExternalServiceException {
            return null;
        }
        @Override
        public void start(String externalID) throws ExternalServiceException {
        }
        @Override
        public ExternalStatus getStatus(String externalWorkflowID) throws ExternalServiceException {
            return null;
        }
    }
        
    public static class TestTrigger extends RemembersProperties implements Trigger {
        public TestTrigger(ObjectNode properties) { super(properties); }
        @Override
        public boolean isDataAvailable(ScheduledTime now, ScheduledTime t) throws Exception {
            return false;
        }
    }

    @Test
    public void propertiesAreCorrectlySet() throws Exception {
        WorkflowConfiguration cfg = parseDir("properties-test");
        
        Workflow wf1 = cfg.findWorkflow(new WorkflowID("workflow-1"));
        Assert.assertEquals("workflow-1", wf1.getID().toString());
        verifyWorkflowProperties(wf1);
        Assert.assertEquals(55, wf1.getMaxRetryCount());
        Assert.assertEquals(Workflow.DEFAULT_START_TIME, wf1.getStartTime());
        
        Workflow wf2 = cfg.findWorkflow(new WorkflowID("workflow-2"));
        Assert.assertEquals("workflow-2", wf2.getID().toString());
        verifyWorkflowProperties(wf2);
        Assert.assertEquals(66, wf2.getMaxRetryCount());
        Assert.assertEquals(new ScheduledTime("2014-03-10T12:34:56.789Z"), wf2.getStartTime());
    }

    private void verifyWorkflowProperties(Workflow wf) {
        TestSchedule schedule = (TestSchedule) wf.getSchedule();
        ObjectNode scheduleProperties = Util.newObjectNode();
        scheduleProperties.put("a", "1");
        scheduleProperties.put("b", "2");
        Assert.assertEquals(scheduleProperties, schedule.getProperties());
        
        TestSchedulingStrategy schedulingStrategy = (TestSchedulingStrategy) wf.getSchedulingStrategy();
        Assert.assertEquals(Util.newObjectNode(), schedulingStrategy.getProperties());
        
        TestExternalService externalService = (TestExternalService) wf.getExternalService();
        ObjectNode externalServiceProperties = Util.newObjectNode();
        externalServiceProperties.put("yippie", "yeah");
        Assert.assertEquals(externalServiceProperties, externalService.getProperties());
        
        TestTrigger trigger = (TestTrigger) wf.getTrigger();
        ObjectNode triggerProperties = Util.newObjectNode();
        triggerProperties.put("foo", "bar");
        Assert.assertEquals(triggerProperties, trigger.getProperties());
    }

    @Test
    public void propertiesForCronAreCorrectlySet() throws Exception {
        Workflow wf = parseDir("cron-task-test").findWorkflow(new WorkflowID("workflow-1"));
        Assert.assertEquals("workflow-1", wf.getID().toString());

        CronSchedule schedule = (CronSchedule) wf.getSchedule();
        Assert.assertEquals(schedule.getCronExpression(), "0 12 * * * ?");
    }


    @Test
    public void canFindWorkflow() throws Exception {
        WorkflowConfiguration cfg = parseDir("properties-test");
        Workflow wf1 = cfg.findWorkflow(new WorkflowID("workflow-1"));
        Assert.assertEquals(55, wf1.getMaxRetryCount());
        Assert.assertNull(cfg.findWorkflow(new WorkflowID("foobar")));
    }
    
    @Test(expected=Exception.class)
    public void classMustExist() throws Exception {
        parseFile("class-not-found");
    }
    
    @Test(expected=Exception.class)
    public void typeMustExist() throws Exception {
        parseFile("type-missing");
    }
    
    @Test(expected=Exception.class)
    public void typeMustBeAString() throws Exception {
        parseFile("type-not-a-string");
    }
    
    @Test(expected=Exception.class)
    public void idMustExist() throws Exception {
        parseFile("id-missing");
    }
    
    @Test(expected=Exception.class)
    public void idMustBeAString() throws Exception {
        parseFile("id-not-a-string");
    }
    
    @Test(expected=Exception.class)
    public void maxRetryCountMustBeSet() throws Exception {
        parseFile("maxretrycount-missing");
    }
    
    @Test(expected=Exception.class)
    public void maxRetryCountMustBeANumber() throws Exception {
        parseFile("maxretrycount-not-a-number");
    }
    
    @Test
    public void doesntFailOnSingleWorkflowError() throws Exception {
        WorkflowConfiguration cfg = parseDir("single-error");
        Assert.assertEquals(2, cfg.getWorkflows().size());
        Assert.assertNotNull(cfg.findWorkflow(new WorkflowID("workflow-1")));
        Assert.assertNotNull(cfg.findWorkflow(new WorkflowID("workflow-3")));
    }
    
    @Test
    public void testJSUtilityFunctions() throws Exception {
        parseFile("js-utility-functions-test");
    }
    
    @Test
    public void doesntAllowDuplicateIDs() throws Exception {
        // Directory contains 2 workflows, but one will be dropped because of duplicate ID.
        WorkflowConfiguration cfg = parseDir("duplicate-ids");
        Assert.assertEquals(1, cfg.getWorkflows().size());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void startTimeMustBeString() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode workflowNode = mapper.createObjectNode();
        workflowNode.put(WorkflowConfigurationParser.START_TIME_PROP, 12);
        File dir = getConfigurationDir("empty");
        new WorkflowConfigurationParser().parseConfiguration(dir).getStartTimeFromJSON(workflowNode);
    }
    
    @Test
    public void scopesAreSeparate() throws Exception {
        parseNamedFile("separate-scopes", "workflow-1");
        parseNamedFile("separate-scopes", "workflow-2");
    }
    
    public static void parseFile(String label) throws Exception {
        parseNamedFile(label, "workflow-1");
    }

    private static void parseNamedFile(String label, String workflowName) throws URISyntaxException,
            Exception {
        File dir = getConfigurationDir(label);
        File workflow = new File(dir, workflowName + "." + WorkflowConfigurationParser.WORKFLOW_FILE_EXTENSION);
        new WorkflowConfigurationParser().parseConfiguration(dir).parseFile(workflow);
    }
    
    public static WorkflowConfiguration parseDir(String label) throws Exception {
        File dir = getConfigurationDir(label);
        return new WorkflowConfigurationParser().parseConfiguration(dir).getWorkflowConfiguration();
    }

    public static File getConfigurationDir(String label) throws URISyntaxException {
        String path = "com/collective/celos/workflow-configuration-test/" + label;
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        return new File(resource.toURI());
    }
    
}
