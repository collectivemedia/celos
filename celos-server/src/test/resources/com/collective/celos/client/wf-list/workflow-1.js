celos.addWorkflow({
    "id": "workflow-1",
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.hdfsCheckTrigger("/", "file:///"),
    "externalService": celos.oozieExternalService({}, "oj01/oozie"),
    "maxRetryCount": 0
});
