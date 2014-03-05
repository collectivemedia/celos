# Celos Workflow Scheduler

* **C**onfigurable — It’s your job to make it usable.

* **E**legant — The only use case is making me feel smart.

* **L**ightweight — I don’t understand the use-cases the alternatives solve.

* **O**pinionated — I don’t believe that your use case exists.

* **S**imple — It solves my use case.

*(from the [Devil's Dictionary of Programming](http://programmingisterrible.com/post/65781074112/devils-dictionary-of-programming))*

## Prerequisites

* JDK 1.7 or higher

* Buildr 1.4.12 or higher

## Unit testing and packaging

* `buildr test` runs the unit test suite.

* `buildr package` packages the WAR file under `target/`.

## Integration testing

This assumes you have the [test cluster](provisioner/README.md) running.

* `./scripts/cluster-deploy.sh` deploys the current state of your repo to the cluster.

* `./scripts/cluster-test.sh` runs the integration tests against the cluster.

## Defining Workflows

A workflow consists of:

* A *trigger* which determines data availability.

* A *schedule* which determines the points in time at which the workflow should run.

* A *scheduling strategy* which determines which of the points in time should be run first.

* An *external service* which is responsible for actually running the
  service (currently Oozie is the only supported external service).

A sample workflow configuration looks like this:

<pre>
addWorkflow({

    "id": "wordcount",

    "maxRetryCount": 0,

    "schedule": hourlySchedule(),

    "schedulingStrategy": serialSchedulingStrategy(),

    "trigger": hdfsCheckTrigger(
        "/user/celos/samples/wordcount/input/${year}-${month}-${day}T${hour}00.txt",
        "hdfs://nn"
    ),

    "externalService": oozieExternalService(
        {
            "nameNode": "hdfs://nn",
            "jobTracker": "hdfs://nn",
            "user.name": "celos",
            "oozie.wf.application.path": "/user/celos/samples/wordcount/workflow/workflow.xml",
            "inputDir": "/user/celos/samples/wordcount/input",
            "outputDir": "/user/celos/samples/wordcount/output"
        },
        "http://nn:11000/oozie"
    )

});
</pre>

## Triggers

### alwaysTrigger()

The simplest kind of trigger: it always signals data availability.

To use when you simply want to run a workflow at every scheduled time.

#### Example

<pre>
...
"trigger": alwaysTrigger()
...
</pre>

### hdfsCheckTrigger(path, fs)

Waits for the existence of a file or directory in HDFS.

#### Parameters

* `path` -- the path in HDFS to check the existence of

* `fs` -- the HDFS filesystem namenode

#### Example

<pre>
...
"trigger": hdfsCheckTrigger(
    "/foo/bar/${year}-${month}-${day}/${hour}/file.txt"
    "hdfs://nameservice1",
)
...
</pre>

## Schedules

### hourlySchedule()

Schedules a workflow to run every hour.

#### Example

<pre>
...
"schedule": hourlySchedule()
...
</pre>

### minutelySchedule()

Schedules a workflow to run every minute.

#### Example

<pre>
...
"schedule": minutelySchedule()
...
</pre>

### cronSchedule(cronExpression)

Schedules a workflow to run via a `cron`-like expression.

#### Parameters

* `cronExpression` -- The cron expression. Syntax: http://quartz-scheduler.org/api/2.0.0/org/quartz/CronExpression.html

#### Example

Run a workflow at minute 15 of every hour of every day of the year.

<pre>
...
"schedule": cronSchedule("15 * * * * ?")
...
</pre>

## Scheduling Strategies

### serialSchedulingStrategy()

Runs the oldest ready slot first, and ensures there's only a single
slot running at any time.

#### Example

<pre>
...
"schedulingStrategy": serialSchedulingStrategy()
...
</pre>


## External Services

### oozieExternalService(workflowProperties, oozieURL)

Submits jobs to Oozie.

#### Parameters

* `workflowProperties` --- The properties to pass to the Oozie workflow.

* `oozieURL` --- The Oozie API URL.

#### Example

<pre>
...
"externalService": oozieExternalService(
    {
        "user.name": "celos",
        "oozie.wf.application.path": "/user/celos/samples/wordcount/workflow/workflow.xml",
        "inputDir": "/user/celos/samples/wordcount/input",
        "outputDir": "/user/celos/samples/wordcount/output"
    },
    "http://nn:11000/oozie"
)
...
</pre>

## Workflow properties

### `maxRetryCount` (number)

Determines number of times a slot should be automatically retried when
it fails.

#### Example

<pre>
...
"maxRetryCount": 15
...
</pre>

## Deploying workflows

Workflow JS files are put into `/etc/celos/workflows` on the host
`celos001.ny7.collective-media.net`.

Logs are stored under `/var/log/celos`.

## HTTP API

### List installed workflows -- `GET /celos/workflow-list`

#### Example

<pre>
curl http://celos001.ny7.collective-media.net:8080/celos/workflow-list
==>
{
  "ids" : [ "workflow-1", "workflow-2", "workflow-3" ]
}
</pre>

### Get workflow information -- `GET /celos/workflow?id=...`

#### Example

<pre>
curl http://celos001.ny7.collective-media.net:8080/celos/workflow?id=workflow-1
==>
{
  "2014-02-10T04:00:00.000Z" : {
    "status" : "SUCCESS",
    "externalID" : "0001828-140209102019171-oozie-oozi-W",
    "retryCount" : 0
  },
  "2014-02-10T05:00:00.000Z" : {
    "status" : "SUCCESS",
    "externalID" : "0001830-140209102019171-oozie-oozi-W",
    "retryCount" : 0
  },
  "2014-02-10T06:00:00.000Z" : {
    "status" : "SUCCESS",
    "externalID" : "0001823-140209102019171-oozie-oozi-W",
    "retryCount" : 0
  },
  ...
}
</pre>

### Rerun slot -- `POST /celos/rerun?id=...&time=...` 

#### Example

<pre>
curl -X POST http://celos001.ny7.collective-media.net:8080/celos/rerun?id=workflow-1&time=2014-02-10T20:00Z
</pre>
