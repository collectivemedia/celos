importDefaults("collective");

addWorkflow({
    "id": "file-copy",
    "maxRetryCount": 0,
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger(hdfsPath("/test/input/file-copy/${year}-${month}-${day}T${hour}00.txt")),
    "externalService": oozieExternalService({
        "oozie.wf.application.path": hdfsPath("/user/celos/app/file-copy/workflow.xml"),
        "inputDir": hdfsPath("hdfs:/test/input/file-copy"),
        "outputDir": hdfsPath("hdfs:/test/output/file-copy")
    })

});
