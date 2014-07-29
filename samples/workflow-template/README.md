This is a template for a Celos workflow.

It includes:

* [scripts/cd](scripts/cd) -- a deployment script that will install
  the workflow JS file on the Celos host, and copy the Oozie workflow
  XML file to HDFS.

* [src/main/oozie/workflow.xml](src/main/oozie/workflow.xml) -- an
  Oozie workflow XML file (that doesn't do anything).

* [src/main/celos/workflow.js](src/main/celos/workflow.js) -- a
  Celos workflow JS file (that doesn't do anything).

To get started:

* Edit [scripts/cd](scripts/cd) and change the `WORKFLOW_NAME`
  variable to the name of your workflow.

* Add your Oozie workflow to
  [src/main/oozie/workflow.xml](src/main/oozie/workflow.xml).

* Run `./scripts/cd` (from your development machine) to install
  everything.

* Manually test your Oozie workflow using the Oozie command line tools
  on oj001.

* ... to be continued

g