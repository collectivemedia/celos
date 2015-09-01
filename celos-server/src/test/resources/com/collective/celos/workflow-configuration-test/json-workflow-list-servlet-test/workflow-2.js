celos.addWorkflow({
    "id": "workflow-2",
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.hdfsCheckTrigger("foo", "file:///"),
    "externalService": celos.oozieExternalService({}, "oj01/oozie"),
    "maxRetryCount": 0
});
