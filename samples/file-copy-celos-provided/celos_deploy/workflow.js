importDefaults("collective");

addWorkflow({
    "id": "file-copy",
    "maxRetryCount": 0,
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger("/user/akonopko/celos.selftest/input/file-copy/${year}-${month}-${day}T${hour}00.txt"),
    "externalService": oozieExternalService({
        "oozie.wf.application.path": "/user/akonopko/celos.selftest/user/celos/app/file-copy/workflow.xml",
        "inputDir": "hdfs:/user/akonopko/celos.selftest/input/file-copy",
        "outputDir": "hdfs:/user/akonopko/celos.selftest/output/file-copy"
    })

});
