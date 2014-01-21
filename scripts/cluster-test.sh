#!/bin/bash
set -e
set -x

# Run integration tests against currently checkout local source on test cluster.

ansible-playbook -i provisioner/tmp/inventory -u celos provisioner/celos_test.yaml
ansible-playbook -i provisioner/tmp/inventory -u celos  scripts/test/test-workflow.yaml --extra-vars "workflow=file-copy sample_time=2013-12-20T20:00Z"
! ansible-playbook -i provisioner/tmp/inventory -u celos  scripts/test/test-workflow.yaml --extra-vars "workflow=file-copy-with-bad-output sample_time=2013-12-20T20:00Z"
! ansible-playbook -i provisioner/tmp/inventory -u celos  scripts/test/test-workflow.yaml --extra-vars "workflow=file-copy-with-missing-output sample_time=2013-12-20T20:00Z" 

echo You win! All tests OK.
