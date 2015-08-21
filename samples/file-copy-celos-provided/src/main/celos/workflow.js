importDefaults("collective");

addWorkflow({
    "id": "file-copy",
    "maxRetryCount": 0,
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger("/user/celos-ci/celos.selftest/input/file-copy/${year}-${month}-${day}T${hour}00.txt"),
    "externalService": oozieExternalService({
        "oozie.wf.application.path": "/user/celos-ci/celos.selftest/user/celos/app/file-copy/workflow.xml",
        "inputDir": "hdfs:/user/celos-ci/celos.selftest/input/file-copy",
        "outputDir": "hdfs:/user/celos-ci/celos.selftest/output/file-copy"
    })

});
