importDefaults("collective");

addWorkflow({
    "id": "file-copy",
    "maxRetryCount": 0,
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger(hdfsPath("/input/file-copy/${year}-${month}-${day}T${hour}00.txt")),
    "externalService": oozieExternalService({
        "oozie.wf.application.path": hdfsPath("/user/celos/app/file-copy/workflow/workflow.xml"),
        "inputDir": hdfsPath("/input/file-copy"),
        "outputDir": hdfsPath("/output/file-copy")
    })

});
