addWorkflow({
    "id": "workflow-2",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger("/", "file:///"),
    "url": "http://collective.com",
    "contacts": [{ name: "John Doe", email: "john.doe@collective.com"}],
    "externalService": mockExternalService(),
    "maxRetryCount": 0
});

addWorkflow({
    "id": "workflow-Iñtërnâtiônàlizætiøn",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger("/", "file:///"),
    "externalService": mockExternalServiceFail(),
    "maxRetryCount": 0
});

addWorkflow({
    "id": "workflow-4",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": onDateTrigger("2000-12-01T00:00Z"),
    "externalService": mockExternalService(),
    "maxRetryCount": 0
});

function onDateTrigger(date) {
    return new Packages.com.collective.celos.OnDateTrigger(date);
}

function mockExternalService() {
    return new Packages.com.collective.celos.MockExternalService(new Packages.com.collective.celos.MockExternalService.MockExternalStatusSuccess());
}

function mockExternalServiceFail() {
    return new Packages.com.collective.celos.MockExternalService(new Packages.com.collective.celos.MockExternalService.MockExternalStatusFailure());
}
