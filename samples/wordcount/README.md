# Celos demo

This example was created to give an idea what typical Celos workflow can look like..
Required steps are following

## Build celos 
You can build Celos with its tools by running
````
scripts\build-celos
```` 

## Prepare Celos server environment

There are several requirements that should be met in order to get Celos successfully run:

Create following directories:
* where Celos will store it's workflows
* where Celos will look for defaults files
* where Celos will store it's database
* where Celos wiil write logs to


Place all required defaults file to defaults dir

(describe what is a defaults file?)

## Run Celos server

Typical Celos command line looks like this:
````
java -classpath 'celos-server.jar:{hadoop_conf_directory}' com.collective.celos.server.Main --defaults {defaults_directory} --workflows {workflows_directory} --logs {logs_directory} --db {database_directory} --port {port} --autoschedule {millis}
````

Where

* `hadoop_conf_directory` is a directory where hadoop-site.xml and core-site.xml files are located. Usually it's /etc/hadoop/conf if your machine has hadoop installed
* `defaults_directory`, `workflows_directory`, `logs_directory`, `database_directory` directories that were created previously
* `port` the port where Celos server should operate on
* `autoschedule` if specified, Celos server will trigger Scheduler with this period

## Build wordcount example 

Build wordcount jar with 

````
./gradlew jar
````

## Prepare deploy directory

You can prepare deploy directory by calling ./gradlew prepareDeployDir..
Basically, script creates following file structure

````
build/
  celos-deploy/
    hdfs/                       # this directory will be enrirely placed to cluster HDFS
      lib/
        wordcount-0.1.jar       # here you place your jars
      workflow.xml              # here you place your workflow.xml
    workflow.js                 # here you place celos.js file, which will be put to Celos workflow dir
````


## Prepare target file

## Deploy workflow 
with celos-ci 
java -jar celos-ci-fat.jar --deployDir /home/akonopko/work/celos/samples/wordcount/build/celos_deploy --mode DEPLOY --workflowName wordcount --target /home/akonopko/work/celos/samples/wordcount/target_test.json --hdfsRoot /user/akonopko/app

## Place input data to be read by wordcount 

## Check that output data was generated

## Undeploy workflow with celos-ci
java -jar celos-ci-fat.jar --deployDir /home/akonopko/work/celos/samples/wordcount/build/celos_deploy --mode UNDEPLOY --workflowName wordcount --target /home/akonopko/work/celos/samples/wordcount/target_test.json --hdfsRoot /user/akonopko/app
