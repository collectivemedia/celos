# Production Deployment Utilities

This directory contains Ansible scripts for setting up the `celos001`
production host as well as deploying Celos to it.

* `deploy.sh` deploys the currently checked out Celos to production.

* `provision.sh` sets up the production host with all required software.

## requiretty

It is required to manually add the following to /etc/sudoers:

`Defaults:celos !requiretty`
