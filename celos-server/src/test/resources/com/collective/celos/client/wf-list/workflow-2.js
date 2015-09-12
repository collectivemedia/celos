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
celos.defineWorkflow({
    "id": "workflow-2",
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.hdfsCheckTrigger("/", "file:///"),
    "url": "http://collective.com",
    "contacts": [{ name: "John Doe", email: "john.doe@collective.com"}],
    "externalService": mockExternalService(),
    "maxRetryCount": 0
});

celos.defineWorkflow({
    "id": "workflow-Iñtërnâtiônàlizætiøn",
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.hdfsCheckTrigger("/", "file:///"),
    "externalService": mockExternalServiceFail(),
    "maxRetryCount": 0
});

celos.defineWorkflow({
    "id": "workflow-4",
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": onDateTrigger("2000-12-01T00:00Z"),
    "externalService": mockExternalService(),
    "maxRetryCount": 0
});

function onDateTrigger(date) {
    return new Packages.com.collective.celos.OnDateTrigger(date);
}

function mockExternalService() {
    return new Packages.com.collective.celos.MockExternalService(new Packages.com.collective.celos.MockExternalService.MockExternalStatusSuccess());
}

function mockExternalServiceFail() {
    return new Packages.com.collective.celos.MockExternalService(new Packages.com.collective.celos.MockExternalService.MockExternalStatusFailure());
}
