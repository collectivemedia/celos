# Celos demo

This example was created to give an idea what typical Celos workflow can look like  
Required steps are following

## Build celos 
You can build Celos with its tools by running
````
scripts\build-celos
```` 

## Prepare Celos server environment

There are several requirements that should be met in order to get Celos successfully run:

Create following directories:
* `workflows` directory, where Celos will store it's workflows
* `defaults` directory, where Celos will look for defaults files
* `db` directory, where Celos will store it's database
* `logs` directory, where Celos wiil write logs to


Place all required defaults file to defaults dir

(describe what is a defaults file?)

## Run Celos server

Typical Celos command line looks like this:
````
java -classpath 'celos-server.jar:{hadoop_conf_directory}' com.collective.celos.server.Main --defaults {defaults_directory} --workflows {workflows_directory} --logs {logs_directory} --db {database_directory} --port {port} --autoschedule {millis}
````

Where

* `hadoop_conf_directory` is a directory where hadoop-site.xml and core-site.xml files are located. Usually it's `/etc/hadoop/conf` if your machine has hadoop installed
* `defaults_directory`, `workflows_directory`, `logs_directory`, `database_directory` directories that were created previously
* `port` the port where Celos server should operate on
* `autoschedule` if specified, Celos server will trigger Scheduler with this period

## Build wordcount example 

Build wordcount jar with 

````
./gradlew jar
````

## Prepare deploy directory

You can prepare deploy directory by calling ./gradlew prepareDeployDir  
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

Target file is a JSON file, that describes Celos-CI "target": cluster HDFS and Celos Server paths
Typical target.json looks like this: 

````
{
    "hadoop.hdfs-site.xml": "sftp://remote.net/etc/hadoop/conf/hdfs-site.xml", # Hadoop hdfs-site.xml file URI
    "hadoop.core-site.xml": "sftp://remote.net/etc/hadoop/conf/core-site.xml", # Hadoop core-site.xml file URI
    "defaults.dir.uri": "/home/user/celos/defaults",                           # Path to Celos defaults dir
    "workflows.dir.uri": "/home/user/celos/workflows"                          # Path to Celos workflows dir
}
````
Each path can be either relative path or sftp URI


## Deploy workflow 

You can easily deploy worklfow with celos-ci DEPLOY MODE, which is called like this:
````
java -jar celos-ci-fat.jar --deployDir {celos_deploy} --mode DEPLOY --workflowName wordcount --target {target.json} --hdfsRoot {hdfsRoot}
````

Where

* `celos_deploy` is a path to previously prepared celos deploy dir
* `target.json` is a path (relative or SFTP URI) to JSON target file
* `hdfsRoot` if specified, Celos-CI will use provided `hdfsRoot` value to place all workflow data. Defaults to `/user/celos/app`


## Place input data to be read by wordcount 

## Check that output data was generated

## Undeploy workflow with celos-ci
java -jar celos-ci-fat.jar --deployDir /home/akonopko/work/celos/samples/wordcount/build/celos_deploy --mode UNDEPLOY --workflowName wordcount --target /home/akonopko/work/celos/samples/wordcount/target_test.json --hdfsRoot /user/akonopko/app
