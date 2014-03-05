function addWorkflow(json) {
    celosWorkflowConfigurationParser.addWorkflowFromJSONString(JSON.stringify(json));
}

function hourlySchedule() {
    return {
        "type": "com.collective.celos.HourlySchedule"
    }
}
