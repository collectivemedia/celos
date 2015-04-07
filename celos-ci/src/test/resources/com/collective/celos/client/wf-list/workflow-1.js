addWorkflow({
    "id": "GrandCentral-01-harmony",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger("/", "file:///"),
    "externalService": oozieExternalService({}, "oj01/oozie"),
    "maxRetryCount": 0
});
