celos.addWorkflow({
    "id": "workflow-1",
    "schedule": celos.cronSchedule("0 12 * * * ?"),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.hdfsCheckTrigger("foo", "file:///"),
    "externalService": celos.oozieExternalService({}, "oj01/oozie"),
    "maxRetryCount": 0
});
