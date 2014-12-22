addWorkflow({
    "id": "workflow-1",
    "schedule": cronSchedule("0 12 * * * ?"),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger("foo", "file:///"),
    "externalService": oozieExternalService({}, "oj01/oozie"),
    "maxRetryCount": 0
});
