addWorkflow({
    "id": "workflow-1",
    "schedule": {
        "type": "ThisClassDoesNotExist",
        "properties": {
            "a": "1",
            "b": "2"
        }
    },
    "schedulingStrategy": {
        "type": "com.collective.celos.WorkflowConfigurationParserTest$TestSchedulingStrategy"
    },
    "trigger": {
        "type": "com.collective.celos.WorkflowConfigurationParserTest$TestTrigger",
        "properties": {
            "foo": "bar"
        }
    },
    "externalService": {
        "type": "com.collective.celos.WorkflowConfigurationParserTest$TestExternalService",
        "properties": {
            "yippie": "yeah"
        }
    },
    "maxRetryCount": 0
});
