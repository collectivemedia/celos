package com.collective.celos;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

public class WorkflowConfigurationParserTest {

    @Test
    public void emptyDirCreatesEmptyWorkflowConfiguration() throws Exception {
        WorkflowConfiguration cfg = parseDir("empty");
        Assert.assertEquals(0, cfg.getWorkflows().size());
    }

    @Test(expected=JsonProcessingException.class)
    public void failsOnIllFormattedFile() throws Exception {
        parseDir("ill-formatted");
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
            parseDir("no-properties-constructor");
        } catch(RuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("Constructor with Properties argument not found for com.collective.celos.WorkflowConfigurationParserTest$ScheduleWithoutPropertiesConstructor"));
        }
    }
    
    @Test
    public void propertyValuesMustBeStrings() throws Exception {
        try {
            parseDir("no-string-value");
        } catch(IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Only string values supported"));
        }

    }
    
    public static class RemembersProperties {
        private Properties properties;
        protected RemembersProperties(Properties props) {
            this.properties = Util.requireNonNull(props);
        }
        public Properties getProperties() {
            return properties;
        }
    }

    public static class TestSchedule extends RemembersProperties implements Schedule {
        public TestSchedule(Properties properties) { super(properties); }
        @Override
        public SortedSet<ScheduledTime> getScheduledTimes(ScheduledTime start, ScheduledTime end) {
            return null;
        }
    }
    
    public static class TestSchedulingStrategy extends RemembersProperties implements SchedulingStrategy {
        public TestSchedulingStrategy(Properties properties) { super(properties); }
        @Override
        public List<SlotState> getSchedulingCandidates(List<SlotState> states) {
            return null;
        }
    }

    public static class TestExternalService extends RemembersProperties implements ExternalService {
        public TestExternalService(Properties properties) { super(properties); }
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
        public TestTrigger(Properties properties) { super(properties); }
        @Override
        public boolean isDataAvailable(ScheduledTime t) throws Exception {
            return false;
        }
    }

    @Test
    public void propertiesAreCorrectlySet() throws Exception {
        WorkflowConfiguration cfg = parseDir("properties-test");
        Workflow wf = cfg.getWorkflows().iterator().next();
        
        Assert.assertEquals("workflow-1", wf.getID().toString());
        
        TestSchedule schedule = (TestSchedule) wf.getSchedule();
        Properties scheduleProperties = new Properties();
        scheduleProperties.setProperty("a", "1");
        scheduleProperties.setProperty("b", "2");
        Assert.assertEquals(scheduleProperties, schedule.getProperties());
        
        TestSchedulingStrategy schedulingStrategy = (TestSchedulingStrategy) wf.getSchedulingStrategy();
        Assert.assertEquals(new Properties(), schedulingStrategy.getProperties());
        
        TestExternalService externalService = (TestExternalService) wf.getExternalService();
        Properties externalServiceProperties = new Properties();
        externalServiceProperties.setProperty("yippie", "yeah");
        Assert.assertEquals(externalServiceProperties, externalService.getProperties());
        
        TestTrigger trigger = (TestTrigger) wf.getTrigger();
        Properties triggerProperties = new Properties();
        triggerProperties.setProperty("foo", "bar");
        Assert.assertEquals(triggerProperties, trigger.getProperties());
    }
    
    @Test(expected=ClassNotFoundException.class)
    public void classMustExist() throws Exception {
        parseDir("class-not-found");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void typeMustExist() throws Exception {
        parseDir("type-missing");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void typeMustBeAString() throws Exception {
        parseDir("type-not-a-string");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void idMustExist() throws Exception {
        parseDir("id-missing");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void idMustBeAString() throws Exception {
        parseDir("id-not-a-string");
    }
    
    private WorkflowConfiguration parseDir(String label) throws Exception {
        File dir = getConfigurationDir(label);
        return new WorkflowConfigurationParser().parseConfiguration(dir);
    }

    private File getConfigurationDir(String label) throws URISyntaxException {
        String path = "com/collective/celos/workflow-configuration-test/" + label;
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        return new File(resource.toURI());
    }
    
}
