#!/bin/bash
set -e
set -x

# Deploy currently checked-out local source on test cluster

ansible-playbook -i provisioner/tmp/inventory -u celos provisioner/celos_deploy.yaml
