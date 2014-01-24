# Celos as a MapReduce testing framework example

This workflow shows how to use Celos as a fixture-based workflow
testing framework.

The workflow is the classic MapReduce word count, implemented as the
Java class [WordCount.java](src/main/java/com/collective/celos/examples/wordcount/WordCount.java),
and called as a Oozie Java action from the [workflow.xml](workflow/workflow.xml).

The [input/](input) and [output/](output) directories contain the
test inputs and the expected outputs, respectively.

The [Buildfile](Buildfile)'s `cluster_test` task:

* automatically builds and uploads the workflow to the [virtual test cluster](../../provisioner),

* runs the workflow using Celos for all inputs,

* and finally compares the workflow's outputs produced in HDFS against
  the expected output fixtures, reporting any discrepancies.

## Trying it out

* Set the `CELOS_HOME` environment variable to point to your Celos repository, e.g. `/home/you/celos`.

* Make sure you have the [virtual test cluster](../../provisioner) running.

* Use `./scripts/cluster-deploy.sh` to deploy Celos to the cluster.

* Run `buildr cluster_test` to run the test.
