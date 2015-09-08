celos.defineWorkflow({
    "id": [ "foo" ],
    "schedule": new WorkflowConfigurationParserTest$TestSchedule(),
    "schedulingStrategy": new WorkflowConfigurationParserTest$TestSchedulingStrategy(),
    "trigger": new WorkflowConfigurationParserTest$TestTrigger(),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService()
});
