addWorkflow({
    "id": "workflow-1",
    "schedule": {
        "type": "com.collective.celos.WorkflowConfigurationParserTest$TestSchedule",
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
    "maxRetryCount": 55
});

addWorkflow({
    "id": "workflow-2",
    "schedule": {
        "type": "com.collective.celos.WorkflowConfigurationParserTest$TestSchedule",
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
    "maxRetryCount": 66,
    "startTime": "2014-03-10T12:34:56.789Z"
});
