importDefaults("collective");

celos.defineWorkflow({
    "id": "wordcount",
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.hdfsCheckTrigger(celos.hdfsPath("/input/wordcount/${year}-${month}-${day}-${hour}00/_SUCCESS")),
    "externalService": celos.oozieExternalService({
        "oozie.wf.application.path": celos.hdfsPath("/user/celos/app/wordcount/workflow.xml"),
        "oozie_wf_base_path": celos.hdfsPath("/user/celos/app/wordcount"),
        "db_name": celos.databaseName("wordcountdb")
    })
});
