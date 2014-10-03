# Celos-CI demo

Celos-CI makes it quick and easy to add fixture-based tests to Celos workflows.

Our example is a MapReduce word count workflow.

## Running the Celos-CI demo

Make sure you are `kinit`-ed.

`./scripts/ci` (in this `samples/wordcount`) directory will test the workflow.

If your local username is different from your Kerberos one you need to specifiy your Kerberos username like this:

`username=manuel ./scripts/ci`

## How it works

To test a workflow you need to do the following steps:

* Use the `hdfsPath()` utility function in your `workflow.js`

* Add test cases to `src/test/celos-ci`

* Call Celos-CI from your build process

Let's look at the steps:

### Use the `hdfsPath()` utility function

Here's the [src/main/celos/workflow.js](src/main/celos/workflow.js) for the word count workflow.

Note that all references to HDFS paths use the `hdfsPath` utility function.

````javascript
importDefaults("collective");

addWorkflow({
    "id": "wordcount",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger(hdfsPath("/input/wordcount/${year}-${month}-${day}T${hour}00.txt")),
    "externalService": oozieExternalService({
        "oozie.wf.application.path": hdfsPath("/user/celos/app/wordcount/workflow.xml"),
        "inputDir": hdfsPath("/input/wordcount"),
        "outputDir": hdfsPath("/output/wordcount")
    })
});
````

### Add test cases to `src/test/celos-ci`

A Celos-CI test case describes a set of inputs and the outputs that the workflow is expected to produce for those inputs.

In the word count case, the inputs are text files, and the outputs are their word counts.

For example, there's an [input file containing "Hello World!"](src/test/celos-ci/test-1/input/plain/input/wordcount/2013-12-20T1700.txt) and the [corresponding output file](src/test/celos-ci/test-1/output/plain/output/wordcount/2013-12-20T1700/part-00000), that contains the word counts:

````
Hello	1
World!	1
````

In general, the test cases directory layout looks like this:

````
src/
  test/
    celos-ci/
      test-1/                   # there can be any number of test cases
        test-config.json        # time information for the test scheduler
        input/                  # contains test inputs
          plain/                # "plain" means the files are copied to HDFS as-is
            foo/
              test-input.txt
        output/                 # contains expected outputs
          plain/
            bar/
              test-output.txt
````

### Call Celos-CI from the build process

The build file should put together a deployment directory, containing the Celos, Oozie, and Java artifacts.

After that, Celos-CI is invoked as a Java main class with the celos-ci.jar coming from Maven.

[build.gradle](build.gradle) shows the needed Gradle tasks for that (around 15 lines of code).

## Status

Right now Celos-CI can test workflows that use plain HDFS inputs and outputs, but we're planning to support Hive, SFTP and more fixtures shortly.
