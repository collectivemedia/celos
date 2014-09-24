importDefaults("collective");

addWorkflow({
    "id": "wordcount",
    "maxRetryCount": 0,
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger(hdfsPath("/input/wordcount/${year}-${month}-${day}T${hour}00.txt")),
    "externalService": oozieExternalService(function (slotId) {
        return {
            "oozie.wf.application.path": hdfsPath("/user/celos/app/wordcount/workflow.xml"),
            "inputDir": hdfsPath("/input/wordcount"),
            "outputDir": hdfsPath("/output/wordcount")
        }
    })
});
