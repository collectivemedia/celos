#!/usr/bin/env bash
set -x
set -e
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1
ansible-playbook -vvv -i scripts/test/inventory -u celos-ci scripts/celos-deploy.yaml -e version=${GIT_COMMIT}
ansible-playbook -vvv -i scripts/test/inventory -u celos-ci scripts/test/workflow_test.yaml --extra-vars "workflow=file-copy sample_time=2013-12-20T20:00Z"
ansible-playbook -vvv -i scripts/test/inventory -u celos-ci scripts/test/celos_test.yaml -e version=${GIT_COMMIT}
