celos.importDefaults("wordcount");

var INPUT_PATH = "/user/" + CELOS_USER + "/celos/quickstart/input/${year}-${month}-${day}/${hour}00";
var OUTPUT_PATH = "/user/" + CELOS_USER + "/celos/quickstart/output/${year}-${month}-${day}/${hour}00";

celos.defineWorkflow({
    "id": "wordcount",
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.hdfsCheckTrigger(INPUT_PATH + "/_READY"),
    "externalService": celos.oozieExternalService({
        "oozie.wf.application.path": "/user/" + CELOS_USER + "/celos/quickstart/app/workflow.xml",
        "inputPath": INPUT_PATH,
        "outputPath": OUTPUT_PATH
    })
});
