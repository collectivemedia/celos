importDefaults("collective");

celos.addWorkflow({
    "id": "file-copy",
    "maxRetryCount": 0,
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.hdfsCheckTrigger(celos.hdfsPath("/test/input/file-copy/${year}-${month}-${day}T${hour}00.txt")),
    "externalService": celos.oozieExternalService({
        "oozie.wf.application.path": celos.hdfsPath("/user/celos/app/file-copy/workflow.xml"),
        "inputDir": celos.hdfsPath("hdfs:/test/input/file-copy"),
        "outputDir": celos.hdfsPath("hdfs:/test/output/file-copy")
    })

});
