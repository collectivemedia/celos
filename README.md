# Celos Workflow Scheduler

* **C**onfigurable — It’s your job to make it usable.

* **E**legant — The only use case is making me feel smart.

* **L**ightweight — I don’t understand the use-cases the alternatives solve.

* **O**pinionated — I don’t believe that your use case exists.

* **S**imple — It solves my use case.

*(from the [Devil's Dictionary of Programming](http://programmingisterrible.com/post/65781074112/devils-dictionary-of-programming))*

## Prerequisites

* JDK 1.6 or higher

* Buildr 1.4.12 or higher

## Unit testing and packaging

* `buildr test` runs the unit test suite.

* `buildr package` packages the WAR file under `target/`.

## Integration testing

This assumes you have the [test cluster](provisioner/README.md) running.

* `./scripts/cluster-deploy.sh` deploys the current state of your repo to the cluster.

* `./scripts/cluster-test.sh` runs the integration tests against the cluster.
