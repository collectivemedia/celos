addWorkflow({
    "id": "workflow-1",
    "schedule": cronSchedule("0 12 * * * ?"),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger("foo", "file:///"),
    "externalService": {
        "type": "com.collective.celos.OozieExternalService",
        "properties": {
            "celos.oozie.url": "oj01/oozie"
        }
    },
    "maxRetryCount": 0
});
