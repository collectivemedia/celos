# Celos-CI demo

Celos-CI makes it quick and easy to add fixture-based tests to Celos workflows.

Celos-CI isn't production-ready yet, but we'll show what it can do.

Our example is a MapReduce word count workflow.

## Testing a workflow with Celos-CI

To test a workflow with Celos-CI, you need to do the following steps:

### Use the `hdfsPath` utility function

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

### Add test cases

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
        input/                  # contains test inputs
          plain/                # "plain" means the files are copied to HDFS as-is
            foo/
              test-input.txt
        output/                 # contains expected outputs
          plain/
            bar/
              test-output.txt
````

### Add Celos-CI to the build process

Like with the earlier Celos-CD, the build file should put together a deployment directory, containing the Celos, Oozie, and Java artifacts.

After that, Celos-CI is invoked as a Java main class, with the needed code coming from our local Maven repo.

[build.gradle](build.gradle) shows the needed Gradle tasks for that (around 15 lines of code).

# Running Celos-CI example


This will:

* Upload the input fixtures to HDFS

* Run a local Celos instance that talks to Oozie and HDFS and runs the word count workflow

* Compare the output fixtures against the results produced by the workflow

````
./gradlew runCelosCiTests
Temp dir for Celos is /tmp/celos2075014395725024584
HDFS prefix is: /user/akonopko/test/wordcount/c9d4be7e-25d5-4e5c-9cd7-662373555d51
Running test case test-1
Touching Scheduler on 2013-12-20T16:00:01Z...
There is workflow running: wordcount at 2013-12-20T16:00:01Z
Touching Scheduler on 2013-12-20T16:00:01Z...
There is workflow running: wordcount at 2013-12-20T16:00:01Z
Touching Scheduler on 2013-12-20T16:00:01Z...
There is workflow running: wordcount at 2013-12-20T16:00:01Z
Touching Scheduler on 2013-12-20T16:00:01Z...
Touching Scheduler on 2013-12-20T17:00:01Z...
There is workflow running: wordcount at 2013-12-20T17:00:01Z
Touching Scheduler on 2013-12-20T17:00:01Z...
There is workflow running: wordcount at 2013-12-20T17:00:01Z
Touching Scheduler on 2013-12-20T17:00:01Z...
There is workflow running: wordcount at 2013-12-20T17:00:01Z
Touching Scheduler on 2013-12-20T17:00:01Z...
Touching Scheduler on 2013-12-20T18:00:01Z...
There is workflow running: wordcount at 2013-12-20T18:00:01Z
Touching Scheduler on 2013-12-20T18:00:01Z...
There is workflow running: wordcount at 2013-12-20T18:00:01Z
Touching Scheduler on 2013-12-20T18:00:01Z...
There is workflow running: wordcount at 2013-12-20T18:00:01Z
Touching Scheduler on 2013-12-20T18:00:01Z...
There is workflow running: wordcount at 2013-12-20T18:00:01Z
Touching Scheduler on 2013-12-20T18:00:01Z...
Comparing file:/home/akonopko/work/celos2/samples/wordcount/src/test/celos-ci/test-1/output/plain/output/wordcount/2013-12-20T1700/part-00000 /user/akonopko/test/wordcount/c9d4be7e-25d5-4e5c-9cd7-662373555d51/output/wordcount/2013-12-20T1700/part-00000
Comparing file:/home/akonopko/work/celos2/samples/wordcount/src/test/celos-ci/test-1/output/plain/output/wordcount/2013-12-20T1700/_SUCCESS /user/akonopko/test/wordcount/c9d4be7e-25d5-4e5c-9cd7-662373555d51/output/wordcount/2013-12-20T1700/_SUCCESS
Comparing file:/home/akonopko/work/celos2/samples/wordcount/src/test/celos-ci/test-1/output/plain/output/wordcount/2013-12-20T1600/part-00000 /user/akonopko/test/wordcount/c9d4be7e-25d5-4e5c-9cd7-662373555d51/output/wordcount/2013-12-20T1600/part-00000
Comparing file:/home/akonopko/work/celos2/samples/wordcount/src/test/celos-ci/test-1/output/plain/output/wordcount/2013-12-20T1600/_SUCCESS /user/akonopko/test/wordcount/c9d4be7e-25d5-4e5c-9cd7-662373555d51/output/wordcount/2013-12-20T1600/_SUCCESS
Comparing file:/home/akonopko/work/celos2/samples/wordcount/src/test/celos-ci/test-1/output/plain/output/wordcount/2013-12-20T1800/part-00000 /user/akonopko/test/wordcount/c9d4be7e-25d5-4e5c-9cd7-662373555d51/output/wordcount/2013-12-20T1800/part-00000
Comparing file:/home/akonopko/work/celos2/samples/wordcount/src/test/celos-ci/test-1/output/plain/output/wordcount/2013-12-20T1800/_SUCCESS /user/akonopko/test/wordcount/c9d4be7e-25d5-4e5c-9cd7-662373555d51/output/wordcount/2013-12-20T1800/_SUCCESS
Output data fits fixtures
Stopping Celos
:runCelosCiTests UP-TO-DATE
 
BUILD SUCCESSFUL
 
Total time: 5 mins 22.322 secs
````

# Status

The current version of Celos-CI can be used to test workflows, but we're still working on making it production-ready.

Right now it can test workflows that use HDFS inputs and outputs, but we're planning to support Hive, SFTP, and more fixtures in the future.
