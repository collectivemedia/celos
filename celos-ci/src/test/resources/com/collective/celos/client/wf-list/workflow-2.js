addWorkflow({
    "id": "GrandCentral-02-pythia",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger("/", "file:///"),
    "externalService": mockExternalServiceFail(),
    "maxRetryCount": 0
});

addWorkflow({
    "id": "GrandCentral-03-hive",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": onDateTrigger("2000-12-01T00:00Z"),
    "externalService": mockExternalService(),
    "maxRetryCount": 0
});


addWorkflow({
    "id": "flume-ready-dal",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": onDateTrigger("2000-12-01T00:00Z"),
    "externalService": mockExternalService(),
    "maxRetryCount": 0
});

addWorkflow({
    "id": "flume-ready-dc",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": onDateTrigger("2000-12-01T00:00Z"),
    "externalService": mockExternalService(),
    "maxRetryCount": 0
});

addWorkflow({
    "id": "flume-ready-dc3",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": onDateTrigger("2000-12-01T00:00Z"),
    "externalService": mockExternalService(),
    "maxRetryCount": 0
});
addWorkflow({
    "id": "flume-ready-lax1",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": onDateTrigger("2000-12-01T00:00Z"),
    "externalService": mockExternalService(),
    "maxRetryCount": 0
});
addWorkflow({
    "id": "flume-ready-nym1",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": onDateTrigger("2000-12-01T00:00Z"),
    "externalService": mockExternalService(),
    "maxRetryCount": 0
});
addWorkflow({
    "id": "flume-ready-sea",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": onDateTrigger("2000-12-01T00:00Z"),
    "externalService": mockExternalService(),
    "maxRetryCount": 0
});
addWorkflow({
    "id": "flume-ready-sv4",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": onDateTrigger("2000-12-01T00:00Z"),
    "externalService": mockExternalService(),
    "maxRetryCount": 0
});
addWorkflow({
    "id": "flume-tmp-file-closer",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": onDateTrigger("2000-12-01T00:00Z"),
    "externalService": mockExternalService(),
    "maxRetryCount": 0
});


function onDateTrigger(date) {
    return new Packages.com.collective.celos.ci.mode.test.client.OnDateTrigger(date);
}

function mockExternalService() {
    return new Packages.com.collective.celos.MockExternalService(new Packages.com.collective.celos.MockExternalService.MockExternalStatusSuccess());
}

function mockExternalServiceFail() {
    return new Packages.com.collective.celos.MockExternalService(new Packages.com.collective.celos.MockExternalService.MockExternalStatusFailure());
}
