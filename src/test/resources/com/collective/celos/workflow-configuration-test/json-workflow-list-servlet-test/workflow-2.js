addWorkflow({
    "id": "workflow-2",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger("foo", "file:///"),
    "externalService": oozieExternalService({}, "oj01/oozie"),
    "maxRetryCount": 0
});
