celos.defineWorkflow({
    "id": "workflow-1",
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.hdfsCheckTrigger("foo", "file:///"),
    "externalService": celos.oozieExternalService({}, "oj01/oozie"),
    "maxRetryCount": 0
});
