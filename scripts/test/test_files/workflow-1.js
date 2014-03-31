addWorkflow({
    "id": "workflow-1",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger("foo", "file:///"),
    "externalService": oozieExternalService({}, "oj01/oozie"),
    "maxRetryCount": 0
});
