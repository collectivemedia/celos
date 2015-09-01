# Celos Workflow Scheduler

* Configurable — It’s your job to make it usable.

* Elegant — The only use case is making me feel smart.

* Lightweight — I don’t understand the use-cases the alternatives solve.

* Opinionated — I don’t believe that your use case exists.

* Simple — It solves my use case.

*(from the [Devil's Dictionary of Programming](http://programmingisterrible.com/post/65781074112/devils-dictionary-of-programming))*

## Overview

Celos is a tool for running, testing, and maintaining Hadoop
data applications that is designed to be simple and flexible.

### Concepts

Celos uses a small number of concepts:

A **workflow** is a recurring job for some specific purpose, and has a
unique identifier, e.g. `my-workflow`.  A workflow's "meat" is an
[Oozie `workflow.xml` file](http://oozie.apache.org/docs/3.2.0-incubating/WorkflowFunctionalSpec.html)
that describes the tasks to perform. 

A **slot** is a single invocation of a workflow at a particular time
and is identified by the workflow ID and time,
e.g. `my-workflow@2013-03-07T20:00Z`.

A **schedule** determines the points in time at which a workflow
should run, i.e. its slots.  A typical schedule would be "every hour",
but Celos supports arbitrary `cron`-like schedules so you could also
run a workflow, say, every 12 minutes between 9am and 11am on Mondays.

Before a given slot is run, Celos checks a **trigger** to make sure
the data that is needed by that slot is available.  A typical trigger
would be a check that a given `_SUCCESS` file or hourly directory in
HDFS exists, but triggers can also be more complex - for example, it's
possible to wait for multiple files.

A **scheduling strategy** picks the order in which slots are run.  The
usual *serial scheduling strategy* simply always picks the oldest slot
and runs it, but it would also be possible to define scheduling
strategies that pick the newest slot, or a random slot, or a number of
slots in parallel.

An **external service** is responsible for actually running workflows.
Currently only Oozie is supported, but Celos is extensible to submit
jobs to other services in the future (e.g. directly to Hadoop/YARN).

### Defining Workflows

Workflows are defined using **JavaScript**.

Here's a sample JavaScript file that defines a single workflow,
`wordcount`.  

`addWorkflow(...)` is the Celos API call that registers a workflow.

The workflow runs every hour (`hourlySchedule()`) and executes slots
one after another, oldest first (`serialSchedulingStrategy()`).

Every hourly slot waits for a file in HDFS following the pattern
`/user/celos/samples/wordcount/input/${year}-${month}-${day}T${hour}00.txt`.
The `hdfsCheckTrigger(...)` is used to specify this.

```javascript
addWorkflow({

    "id": "wordcount",

    "maxRetryCount": 0,

    "startTime": "2014-03-10T00:00Z",

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
```

The `oozieExternalService(...)` specifies which Oozie workflow file to
use (`/user/celos/samples/wordcount/workflow/workflow.xml`), and also
supplies some properties (`inputDir`, `outputDir`) to the workflow.

The `workflow.xml` looks as follows:

```xml
<?xml version="1.0"?>
<workflow-app name="wordcount@${year}-${month}-${day}T${hour}:00Z" xmlns="uri:oozie:workflow:0.4">
  <start to="main"/>
  <action name="main">
    <java>
        <job-tracker>${jobTracker}</job-tracker>
        <name-node>${nameNode}</name-node>
        <configuration>
            <property>
               <name>mapred.job.queue.name</name>
               <value>default</value>
            </property>
        </configuration>
        <main-class>com.collective.celos.examples.wordcount.WordCount</main-class>
        <arg>${inputDir}/${year}-${month}-${day}T${hour}00.txt</arg>
        <arg>${outputDir}/${year}-${month}-${day}T${hour}00</arg>
    </java>
    <ok to="end"/>
    <error to="kill"/>
  </action>
  <kill name="kill">
    <message>${wf:errorCode("failed")}</message>
  </kill>
  <end name="end"/>
</workflow-app>
```

Celos automatically supplies the date-based properties like `year`,
`month`, `day`, etc.

### Complex workflow definitions

A Celos JavaScript file doesn't have to contain only a single workflow
-- any number of `addWorkflow(...)` calls can appear in a file.

This makes it possible to group together related workflows.  Shared
functionality can be factored out into JavaScript utility functions,
and all features of JavaScript (conditionals, loops, etc) can be used.

For example,
[`dorado.js`](https://github.com/collectivemedia/dorado-flume/blob/16e2e59164d961c00236ebfae20d00c48650968e/dorado.js)
defines six related workflows that only differ in minor details
(e.g. name of data center).

### Testing of workflows

Celos comes with an AWS-based [virtual test cluster](/provisioner)
containing Oozie, HDFS, Hive and other Hadoop tools.

Workflows can be automatically submitted to this test cluster from
their build process for integration testing.  The [Wordcount
sample](samples/wordcount) shows how to do this.

### Where to go from here

Check out the [samples](/samples), [reference materials](#reference),
and start defining your own workflows with Celos. It's fun!

## Installing and Running

### Prerequisites

* JDK 1.7 or higher
* Buildr 1.4.12 or higher

### Unit testing and packaging

* `buildr test` runs the unit test suite.
* `buildr package` packages the WAR file under `target/`.

### Integration testing

This assumes you have the [test cluster](provisioner/README.md) running.

* `./scripts/cluster-deploy.sh` deploys the current state of your repo to the cluster.
* `./scripts/cluster-test.sh` runs the integration tests against the cluster.

# Reference

## Triggers

### alwaysTrigger

Syntax: `alwaysTrigger()`

The simplest kind of trigger: it always signals data availability.

To use when you simply want to run a workflow at every scheduled time.

#### Example

<pre>
...
"trigger": alwaysTrigger()
...
</pre>

### hdfsCheckTrigger

Syntax: `hdfsCheckTrigger(path, [fs])`

Waits for the existence of a file or directory in HDFS.

#### Parameters

* `path` -- the path in HDFS to check the existence of

* `fs` -- the HDFS filesystem namenode.  If the argument is not
  supplied, the value of the `CELOS_DEFAULT_HDFS` global will be used.

#### Example

<pre>
...
"trigger": hdfsCheckTrigger(
    "/foo/bar/${year}-${month}-${day}/${hour}/file.txt",
    "hdfs://nameservice1"
)
...
</pre>

With `CELOS_DEFAULT_HDFS` set:

<pre>
...
var CELOS_DEFAULT_HDFS = "hdfs://nameservice1";
...
"trigger": hdfsCheckTrigger("/foo/bar/${year}-${month}-${day}/${hour}/file.txt")
...
</pre>

#### Variables

The `path` can contain the variables `${year}`, `${month}`, `${day}`,
`${hour}`, `${minute}`, and `${second}`, which are zero-padded.

### andTrigger

Syntax: `andTrigger(triggers...)`

Combines multiple triggers and waits for all of them (logical AND).

Returns true if the list of triggers is empty.

#### Parameters

* `triggers` -- any number of other triggers are passed as parameters

#### Example

<pre>
...
// Wait for HDFS paths /foo, /bar, and /quux
"trigger": andTrigger(
   hdfsCheckTrigger("/foo", ...),
   hdfsCheckTrigger("/bar", ...),
   hdfsCheckTrigger("/quux", ...)
)
...
</pre>

### orTrigger

Syntax: `orTrigger(triggers...)`

Combines multiple triggers and waits for either one of them (logical OR).

Returns false if the list of triggers is empty.

#### Parameters

* `triggers` -- any number of other triggers are passed as parameters

#### Example

<pre>
...
// Wait for either of the HDFS paths /foo, /bar, and /quux
"trigger": orTrigger(
   hdfsCheckTrigger("/foo", ...),
   hdfsCheckTrigger("/bar", ...),
   hdfsCheckTrigger("/quux", ...)
)
...
</pre>

### notTrigger

Syntax: `notTrigger(trigger)`

Negates another trigger (logical NOT).

#### Parameters

* `trigger` -- another trigger

#### Example

<pre>
...
// Triggers if the file /foo doesn't exist
"trigger": notTrigger(hdfsCheckTrigger("/foo"))
</pre>

### delayTrigger

Syntax: `delayTrigger(seconds)`

A trigger that signals data availability for a given scheduled time
only if it is a configurable number of seconds past the current time.

In combination with an `andTrigger()`, this allows to delay the firing
of another trigger, for example to clean up data after a day.

#### Parameters

* `seconds` -- the number of seconds to delay data availability

#### Example

The following example shows a trigger that only fires if the given
HDFS path is available, and the current time is one day after 
the workflow's scheduled time.

```javascript
var oneDay = 60 * 60 * 24;
andTrigger(delayTrigger(oneDay), hdfsCheckTrigger("/${year}/${month}/${day}/..."))
```

The `delayTrigger()` should always be placed as the first argument of
an `andTrigger()` so that the relatively more expensive
`hdfsCheckTrigger()` won't even be executed.

### offsetTrigger

Syntax: `offsetTrigger(seconds, otherTrigger)`

A trigger that invokes another trigger with the scheduled time offset
by a number of seconds.

This can be used to trigger on data in the future or the past,
relative to the workflow slot's scheduled time.

#### Parameters

* `seconds` -- the number of seconds to offset the other trigger

* `otherTrigger` -- the trigger whose scheduled time should be offset

#### Example

```javascript
var oneDay = 60 * 60 * 24;
offsetTrigger(oneDay, hdfsCheckTrigger("/${year}/${month}/${day}/..."))
```

If this trigger is called for the scheduled time 2014-01-10T00:00Z it
will check for data availability at 2014-01-11T00:00Z.

### successTrigger

Syntax: `successTrigger(workflowName)`

A trigger that signals data availability for a given scheduled time
only if another workflow has successfully run for that time.

This makes it possible to describe dependencies among workflows.

#### Parameters

* `workflowName` -- the name of the workflow to wait for

#### Example

This trigger triggers for a given time if the workflow `foo-workflow`
was successful for that time:

```javascript
successTrigger("foo-workflow")
```

## Schedules

### hourlySchedule

Syntax: `hourlySchedule()`

Schedules a workflow to run every hour.

#### Example

<pre>
...
"schedule": hourlySchedule()
...
</pre>

### minutelySchedule

Syntax: `minutelySchedule()`

Schedules a workflow to run every minute.

#### Example

<pre>
...
"schedule": minutelySchedule()
...
</pre>

### cronSchedule

Syntax: `cronSchedule(cronExpression)`

Schedules a workflow to run via a `cron`-like expression.

#### Parameters

* `cronExpression` -- The [cron expression](http://www.quartz-scheduler.org/documentation/quartz-1.x/tutorials/crontrigger)

#### Example

Run a workflow at second 0, minute 15 of every hour of every day of the year.

<pre>
...
"schedule": cronSchedule("0 15 * * * ?")
...
</pre>

### dependentSchedule

Syntax: `dependentSchedule(otherWorkflowID)`

Schedules a workflow to run with the same schedule as another workflow.  Effectively, this clones the other workflow's schedule.

#### Parameters

* `otherWorkflowID` -- The ID of the other workflow as a string.

#### Example

Run the workflow with the same schedule as workflow "foo".

<pre>
...
"schedule": dependentSchedule("foo")
...
</pre>


## Scheduling Strategies

### serialSchedulingStrategy

Syntax: `serialSchedulingStrategy()`

Runs the oldest ready slot first, and ensures there's only a single
slot running at any time.

#### Example

<pre>
...
"schedulingStrategy": serialSchedulingStrategy()
...
</pre>


## External Services

### oozieExternalService

Syntax: `oozieExternalService(workflowProperties, [oozieURL])`

Submits jobs to Oozie.

#### Parameters

* `workflowProperties` -- The properties to pass to the Oozie workflow.

* `oozieURL` -- The Oozie API URL.  If the argument is not supplied,
  the value of the `CELOS_DEFAULT_OOZIE` global will be used.

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

With `CELOS_DEFAULT_OOZIE` set:

<pre>
...
var CELOS_DEFAULT_OOZIE = "http://nn:11000/oozie";
...
"externalService": oozieExternalService(
    {
        "user.name": "celos",
        "oozie.wf.application.path": "/user/celos/samples/wordcount/workflow/workflow.xml",
        "inputDir": "/user/celos/samples/wordcount/input",
        "outputDir": "/user/celos/samples/wordcount/output"
    }
)
...
</pre>

#### Variables

The property values can contain the variables `${year}`, `${month}`,
`${day}`, `${hour}`, `${minute}`, and `${second}`, which are
zero-padded.

#### Oozie Workflow Properties

Celos automatically sets the variables (see above) as Oozie workflow
properties, so they can also be used in the workflow XML file.

Additionally, it sets the `celosWorkflowName` property to a string
containing the workflow name and a timestamp, which is useful as
workflow name in the XML file.

#### Oozie Workflow Defaults

If `CELOS_DEFAULT_OOZIE_PROPERTIES` is a JavaScript object, its
contents will be merged into the properties as defaults.

Example:

<pre>
var CELOS_DEFAULT_OOZIE_PROPERTIES = {
    "user.name": "peter"
};
</pre>

Now every Oozie workflow will run as user "peter".

#### Fine-grained property control

Instead of an object, the first argument of `oozieExternalService` can
also be a function.  It will be called with the slot ID of a workflow
slot, and should return an object containing properties for that slot.

For example, this sets the Oozie property `someProperty` to last year:

```
function myOozieProperties(slotID) {
    return {
        someProperty: new String(slotID.getScheduledTime().minusYears(1).year())
    }
}
oozieExternalService(myOozieProperties);
```

##### Additional functions that can be used in `oozieExternalService` function decription

You can check if HDFS file exists

```
hdfsCheck("hdfs://mypath/myfile")
```

Also Date properties substitution can be done. To do that you should provide corresponding ScheduleTime


```
hdfsCheck("hdfs://mypath/${year}-${month}-${day}-file", slotID.getScheduledTime())
```

If you need to ask specific FileSystem you can provide it as a third parameter: 

```
hdfsCheck("/mypath/${year}-${month}-${day}-file", slotID.getScheduledTime(), "hdfs://mypath")
```


## Workflow properties

### maxRetryCount

Type: number, optional

Determines number of times a slot should be automatically retried when
it fails.

Defaults to 0, so slots will not be automatically retried by default.

#### Example

<pre>
...
"maxRetryCount": 15
...
</pre>

### startTime

Type: string in ISO 8601 UTC date format, optional

When the workflow should start.

#### Example

<pre>
...
"startTime": "2014-03-10T00:00Z",
...
</pre>

### waitTimeoutSeconds

Type: number, optional

The number of seconds a workflow should stay waiting until it times out.

If unspecified, a workflow will wait forever without timing out.

#### Example

<pre>
...
"waitTimeoutSeconds": 60 * 60 // time out after one hour
...
</pre>

## Defaults

Files containing defaults can be placed in `/etc/celos/defaults` and
loaded with the `importDefaults(label)` function.

For example, if `/etc/celos/defaults/foo.js` contains

<pre>
var FOO = 23;
</pre>

then a workflow can load it like this:

<pre>
importDefaults("foo");
// FOO now available
</pre>

## Deploying workflows

Workflow JS files are put into `/etc/celos/workflows` on the host
`celos001.ny7.collective-media.net`.

Logs are stored under `/var/log/celos`.

## HTTP API

### Step scheduler -- `POST /celos/scheduler`

Performs a step of the scheduler, typically called from `cron` once a minute.

Optional parameters:

* `time` -- if supplied, process slots in the sliding window before that time (as opposed to current time).  Used for manually processing slots in the past outside the current sliding window.

* `ids` (comma separated list of workflow IDs) -- if supplied, process only the workflows in the list.

### List installed workflows -- `GET /celos/workflow-list`

Returns the IDs of all active workflows.

#### Example

<pre>
curl http://celos001.ny7.collective-media.net:8080/celos/workflow-list
==>
{
  "ids" : [ "workflow-1", "workflow-2", "workflow-3" ]
}
</pre>

### Get workflow information -- `GET /celos/workflow?id=...`

Returns the list of the given workflow's slots.

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

### Get workflow file -- `GET /celos/workflow-file?id=...`

Returns the JS file that defined the given workflow.

#### Example

<pre>
curl http://celos001.ny7.collective-media.net:8080/celos/workflow-file?id=workflow-1
==>
addWorkflow({
    "id": "workflow-1",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger("foo", "file:///"),
    "externalService": oozieExternalService({}, "oj01/oozie"),
    "maxRetryCount": 0
});
</pre>

### Get slot information -- `GET /celos/slot-state?id=...&time=...`

Returns the state of the given slot.

#### Example

<pre>
curl http://celos001.ny7.collective-media.net:8080/celos/slot-state?id=workflow-1&time=2014-02-10T04:00Z
==>
{
  "status" : "SUCCESS",
  "externalID" : "0001828-140209102019171-oozie-oozi-W",
  "retryCount" : 0
}
</pre>

### Rerun slot -- `POST /celos/rerun?id=...&time=...` 

Reruns a specific slot.

#### Example

<pre>
curl -X POST http://celos001.ny7.collective-media.net:8080/celos/rerun?id=workflow-1&time=2014-02-10T20:00Z
</pre>

## Tips & Tricks

### Rerunning old workflow slots

Celos only processes slots within a sliding window (currently set to 7
days).  Slots outside the sliding window are ignored.

To rerun slots outside the sliding window, you need to manually
trigger the scheduler servlet for the window the slot lies in.

#### Example

This example shows how to rerun a slot `workflow-1@2014-02-10T20:00Z`.

<pre>
CELOS=http://celos001.ny7.collective-media.net:8080/celos

# Mark workflow-1@2014-02-10T20:00Z to be rerun in scheduler state database
curl -X POST "$CELOS/rerun?id=workflow-1&time=2014-02-10T20:00Z"

# Trigger scheduler to process the slot
# Note that we use sliding window starting 1 hour after the workflow
watch -n 10 curl -X POST "$CELOS/scheduler?ids=workflow-1&time=2014-02-10T21:00Z"

# While watch is running, check slot status with this command
curl "${CELOS}/workflow-slots?id=workflow-1&time=2014-02-10T21:00Z"
</pre>
