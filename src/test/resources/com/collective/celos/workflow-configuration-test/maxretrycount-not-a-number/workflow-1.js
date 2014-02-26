{
    "id": "workflow-1",
    "schedule": hourlySchedule(),
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
    "maxRetryCount": "foo"
}
