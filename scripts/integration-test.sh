#!/usr/bin/env bash
set -x
set -e
GIT_COMMIT=${GIT_COMMIT:-undefined}
ansible-playbook -i scripts/test/inventory -u celos-ci scripts/celos-deploy.yaml -e version=${GIT_COMMIT}
ansible-playbook -i scripts/test/inventory -u celos-ci scripts/test/celos_test.yaml -e version=${GIT_COMMIT}
