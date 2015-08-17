importDefaults("collective");

addWorkflow({

    "id": "file-copy",

    "maxRetryCount": 0,

    "schedule": hourlySchedule(),

    "schedulingStrategy": serialSchedulingStrategy(),

    "trigger": hdfsCheckTrigger(
        "/user/celos-ci/samples/file-copy/input/${year}-${month}-${day}T${hour}00.txt"
    ),

    "externalService": oozieExternalService(
        {
            "user.name": "celos-ci",
            "oozie.wf.application.path": "/user/celos-ci/samples/file-copy/workflow/workflow.xml",
            "inputDir": "hdfs:/user/celos-ci/samples/file-copy/input",
            "outputDir": "hdfs:/user/celos-ci/samples/file-copy/output"
        }
    )

});
