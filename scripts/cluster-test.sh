#!/bin/bash
set -e
set -x

# Run integration tests against currently checkout local source on test cluster.

ansible-playbook -i provisioner/tmp/inventory -u ubuntu provisioner/celos_test.yaml
