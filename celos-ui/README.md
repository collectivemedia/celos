# Concepts

## Target files

A target file describes the cluster Celos CI should run tests on.

Here's the target file for our main cluster:

https://github.com/collectivemedia/celos-targets/blob/master/main.json

By changing the target file that is passed to Celos CI, a workflow can be tested on a different cluster.

## CI Script

A ci script is used to launch Celos CI.  It will usually be a Gradle invokation that specifies the target file to use.

Example from mapreduce wordcount: https://github.com/collectivemedia/celos/blob/master/samples/wordcount/scripts/ci

## Jenkins project

For each test, there will be a Jenkins project that calls the CI script.

Example: https://jenkins.collective-media.net/job/celos-ci-mapreduce-wordcount/configure

Note that it uses kinit to switch to the Celos CI user.

## Build file

As with the previous celos-cd tool, the build file prepares a deployment directory containing both Celos workflow definitions and Oozie HDFS resources and libraries.

https://github.com/collectivemedia/celos/blob/master/samples/wordcount/build.gradle

Also, the build file has a dynamic dependency on the celos-ci.jar, so it will always download the most recent version of it.

## src/main/celos/workflow.js file

Just the usual Celos workflow definition file, but all HDFS paths are wrapped in calls to the hdfsPath() function.

https://github.com/collectivemedia/celos/blob/master/samples/wordcount/src/main/celos/workflow.js

## src/test/celos-ci/test.js file

This file contains test case definitions that will often use other resources/fixtures also stored in src/test/celos-ci.

https://github.com/collectivemedia/celos/blob/master/samples/wordcount/src/test/celos-ci/test.js

Input fixtures: https://github.com/collectivemedia/celos/tree/master/samples/wordcount/src/test/celos-ci/test-1/input

Output fixtures: https://github.com/collectivemedia/celos/tree/master/samples/wordcount/src/test/celos-ci/test-1/output

# Hive wordcount sample

Input table: https://github.com/collectivemedia/celos/blob/master/samples/wordcount_hive/src/test/celos-ci/test-1/input.tsv

Input table schema: https://github.com/collectivemedia/celos/blob/master/samples/wordcount_hive/src/test/celos-ci/test-1/schema/wordcount.avsc

Result table: https://github.com/collectivemedia/celos/blob/master/samples/wordcount_hive/src/test/celos-ci/test-1/result.tsv

SQL: https://github.com/collectivemedia/celos/blob/master/samples/wordcount_hive/src/main/oozie/sql/wordcount.sql

Workflow: https://github.com/collectivemedia/celos/blob/master/samples/wordcount_hive/src/main/celos/workflow.js

Test: https://github.com/collectivemedia/celos/blob/master/samples/wordcount_hive/src/test/celos-ci/test.js
