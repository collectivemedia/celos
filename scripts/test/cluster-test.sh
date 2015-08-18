#!/bin/bash
set -e
set -x

# Run integration tests against currently checkout local source on test cluster.

ansible-playbook -i inventory celos_dirs.yaml
ansible-playbook -i inventory celos_test.yaml

echo You win! All tests OK.
