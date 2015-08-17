#!/usr/bin/env bash
set -x
set -e
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1
ansible-playbook -i scripts/test/inventory -u celos-ci scripts/celos-deploy.yaml -e service_version=${GIT_COMMIT} -e "@scripts/celos-params.json"
ansible-playbook -i scripts/test/inventory -u celos-ci scripts/test/celos_test.yaml -e service_version=${GIT_COMMIT} -e "@scripts/celos-params.json"
