importPackage(Packages.com.collective.celos);

addWorkflow({
    "id": "workflow-4",
    "schedule": new WorkflowConfigurationParserTest$TestSchedule(),
    "schedulingStrategy": new WorkflowConfigurationParserTest$TestSchedulingStrategy(),
    "trigger": successTrigger("workflow-2"),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService(),
    "maxRetryCount": 66,
    "startTime": "2014-03-10T12:34:56.789Z"
});


addWorkflow({
    "id": "workflow-2",
    "schedule": new WorkflowConfigurationParserTest$TestSchedule(),
    "schedulingStrategy": new WorkflowConfigurationParserTest$TestSchedulingStrategy(),
    "trigger": successTrigger("workflow-1"),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService(),
    "maxRetryCount": 66,
    "startTime": "2014-03-10T12:34:56.789Z"
});

addWorkflow({
    "id": "workflow-3",
    "schedule": new WorkflowConfigurationParserTest$TestSchedule(),
    "schedulingStrategy": new WorkflowConfigurationParserTest$TestSchedulingStrategy(),
    "trigger": successTrigger("workflow-2"),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService(),
    "maxRetryCount": 66,
    "startTime": "2014-03-10T12:34:56.789Z"
});


addWorkflow({
    "id": "workflow-5",
    "schedule": new WorkflowConfigurationParserTest$TestSchedule(),
    "schedulingStrategy": new WorkflowConfigurationParserTest$TestSchedulingStrategy(),
    "trigger": andTrigger(successTrigger("workflow-3"), successTrigger("workflow-4")),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService(),
    "maxRetryCount": 66,
    "startTime": "2014-03-10T12:34:56.789Z"
});

addWorkflow({
    "id": "workflow-1",
    "schedule": new WorkflowConfigurationParserTest$TestSchedule(),
    "schedulingStrategy": new WorkflowConfigurationParserTest$TestSchedulingStrategy(),
    "trigger": new WorkflowConfigurationParserTest$TestTrigger(),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService(),
    "maxRetryCount": 55
});
