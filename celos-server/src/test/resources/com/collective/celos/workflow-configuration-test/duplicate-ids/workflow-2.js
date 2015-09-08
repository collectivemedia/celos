celos.defineWorkflow({
    "id": "this-is-a-duplicate",
    "schedule": new WorkflowConfigurationParserTest$TestSchedule(),
    "schedulingStrategy": new WorkflowConfigurationParserTest$TestSchedulingStrategy(),
    "trigger": new WorkflowConfigurationParserTest$TestTrigger(),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService(),
    "maxRetryCount": 0
});
