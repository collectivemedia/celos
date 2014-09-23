# celos-cd

## Celos Continuous Workflow Deployment Utility

### Prepare deployment directory

To deploy a workflow called `foo` with `celos-cd` create a deployment
script hooked up to CI that creates a deployment directory like the
following (the name of the deploy dir doesn't matter):

```
celos_deploy/
  workflow.js
  hdfs/
    workflow.xml
    lib/
      my-jar.jar
```

The workflow file should always be called `workflow.js`.  It will be
copied to `/etc/celos/workflows/foo.js` on the Celos host.

The `hdfs` directory will be copied to `/user/celos/app/foo` on HDFS.
Use this as your `oozie.wf.application.path`, i.e. put the Oozie
`workflow.xml` in there, and also any JARs in the `hdfs/lib` dir.

You can put any other files in the deployment directory, too.  The
whole deployment directory will be copied to
`/home/celos/celos-cd/app/foo` on the Celos host.

### Call celos-cd

```
java -jar celos-cd-0.1.jar --celosWorkflowDirUri sftp://celos001/home/akonopko 
       --pathToWorkflow .deploy-dir --workflowName celoscd --mode DEPLOY 
       --hdfsSite /etc/hadoop/conf/hdfs-site.xml --coreSite /etc/hadoop/conf/core-site.xml
```
or
```
hadoop jar celos-cd-0.1.jar --celosWorkflowDirUri sftp://celos001/home/akonopko 
       --pathToWorkflow .deploy-dir --workflowName celoscd --mode DEPLOY 
```


### Self-testing

```
Usage: self-test celos-server workflow-dir-on-celos workflow-name username
```
For example
```
./self-test celos001 /home/akonopko celoscd akonopko
```

This creates dummy workflow and checks would it be processed 
correctly or not
