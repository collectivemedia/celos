/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
importPackage(Packages.com.collective.celos);

celos.defineWorkflow({
    "id": "workflow-1",
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.alwaysTrigger(),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService()
});

celos.defineWorkflow({
    "id": "workflow-2",
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.successTrigger("workflow-1"),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService()
});


celos.defineWorkflow({
    "id": "workflow-3",
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.andTrigger(celos.successTrigger("workflow-1"), celos.successTrigger("workflow-2")),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService()
});

celos.defineWorkflow({
    "id": "workflow-4",
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.alwaysTrigger(),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService()
});


var hour_sec = 60*60;

celos.defineWorkflow({
    "id": "workflow-5",
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.orTrigger(celos.notTrigger(celos.successTrigger("workflow-1")), celos.offsetTrigger(hour_sec, celos.successTrigger("workflow-4"))),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService()
});

celos.defineWorkflow({
    "id": "workflow-6-daily",
    "schedule": celos.cronSchedule("0 0 0 * * ?"),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.notTrigger(celos.alwaysTrigger()),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService()
});

celos.defineWorkflow({
    "id": "workflow-7-depend-daily",
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.successTrigger("workflow-6-daily"),
    "externalService": new WorkflowConfigurationParserTest$TestExternalService()
});

