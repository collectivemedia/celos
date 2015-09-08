importDefaults("collective");

celos.defineWorkflow({
    "id": "wordcount",
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.hdfsCheckTrigger(celos.hdfsPath("/input/wordcount/${year}-${month}-${day}T${hour}00.txt")),
    "externalService": celos.oozieExternalService({
        "oozie.wf.application.path": celos.hdfsPath("/user/celos/app/wordcount/workflow.xml"),
        "inputDir": celos.hdfsPath("/input/wordcount"),
        "outputDir": celos.hdfsPath("/output/wordcount")
    })
});
