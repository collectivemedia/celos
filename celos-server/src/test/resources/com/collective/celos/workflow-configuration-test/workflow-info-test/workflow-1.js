importPackage(Packages.com.collective.celos);

addWorkflow({
    "id": "workflow-1",
    "schedule": new WorkflowConfigurationParserTest$TestSchedule(),
    "schedulingStrategy": new WorkflowConfigurationParserTest$TestSchedulingStrategy(),
    "trigger": new WorkflowConfigurationParserTest$TestTrigger(),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService(),
    "maxRetryCount": 55
});

addWorkflow({
    "id": "workflow-2",
    "schedule": new WorkflowConfigurationParserTest$TestSchedule(),
    "schedulingStrategy": new WorkflowConfigurationParserTest$TestSchedulingStrategy(),
    "trigger": new WorkflowConfigurationParserTest$TestTrigger(),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService(),
    "maxRetryCount": 66,
    "url": "http://collective.com/workflow",
    "contacts": [{"name": "John Doe", "email": "john.doe@collective.com"}],
    "startTime": "2014-03-10T12:34:56.789Z"
});

addWorkflow({
    "id": "workflow-3",
    "schedule": new WorkflowConfigurationParserTest$TestSchedule(),
    "schedulingStrategy": new WorkflowConfigurationParserTest$TestSchedulingStrategy(),
    "trigger": new WorkflowConfigurationParserTest$TestTrigger(),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService(),
    "maxRetryCount": 66,
    "url": "http://collective.com/workflow",
    "contacts": [{"email": "john.doe@collective.com"}],
    "startTime": "2014-03-10T12:34:56.789Z"
});

addWorkflow({
    "id": "workflow-4",
    "schedule": new WorkflowConfigurationParserTest$TestSchedule(),
    "schedulingStrategy": new WorkflowConfigurationParserTest$TestSchedulingStrategy(),
    "trigger": new WorkflowConfigurationParserTest$TestTrigger(),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService(),
    "maxRetryCount": 66,
    "url": "http://collective.com/workflow",
    "contacts": [{"name": "John Doe"}],
    "startTime": "2014-03-10T12:34:56.789Z"
});

addWorkflow({
    "id": "workflow-5",
    "schedule": new WorkflowConfigurationParserTest$TestSchedule(),
    "schedulingStrategy": new WorkflowConfigurationParserTest$TestSchedulingStrategy(),
    "trigger": new WorkflowConfigurationParserTest$TestTrigger(),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService(),
    "maxRetryCount": 66,
    "url": "http://collective.com/workflow",
    "startTime": "2014-03-10T12:34:56.789Z"
});
