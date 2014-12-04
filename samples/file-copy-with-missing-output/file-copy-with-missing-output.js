importDefaults("collective");

addWorkflow({

    "id": "file-copy-with-missing-output",

    "maxRetryCount": 0,

    "schedule": hourlySchedule(),

    "schedulingStrategy": serialSchedulingStrategy(),

    "trigger": hdfsCheckTrigger(
        "/user/celos/samples/file-copy-with-missing-output/input/${year}-${month}-${day}T${hour}00.txt"
    ),

    "externalService": oozieExternalService(
        {
            "user.name": "celos",
            "oozie.wf.application.path": "/user/celos/samples/file-copy-with-missing-output/workflow/workflow.xml",
            "inputDir": "hdfs:/user/celos/samples/file-copy-with-missing-output/input",
            "outputDir": "hdfs:/user/celos/samples/file-copy-with-missing-output/output"
        }
    )

});
