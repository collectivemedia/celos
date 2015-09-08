importPackage(Packages.com.collective.celos);

celos.defineWorkflow({
    "id": "workflow-1",
    "schedule": new WorkflowConfigurationParserTest$TestSchedule(),
    "schedulingStrategy": new WorkflowConfigurationParserTest$TestSchedulingStrategy(),
    "trigger": new WorkflowConfigurationParserTest$TestTrigger(),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService(),
    "maxRetryCount": 55
});

celos.defineWorkflow({
    "id": "workflow-2",
    "schedule": new WorkflowConfigurationParserTest$TestSchedule(),
    "schedulingStrategy": new WorkflowConfigurationParserTest$TestSchedulingStrategy(),
    "trigger": new WorkflowConfigurationParserTest$TestTrigger(),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService(),
    "maxRetryCount": 66,
    "startTime": "2014-03-10T12:34:56.789Z",
    "waitTimeoutSeconds": 23
});
