#!/usr/bin/env bash
set -x
set -e
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1
ansible-playbook -i scripts/conf/inventory-testing scripts/celos-deploy.yaml -e service_version=${GIT_COMMIT} -e "@scripts/conf/celos-params.json" -e "@scripts/conf/common-params.json"
ansible-playbook -i scripts/conf/inventory-testing scripts/test/celos_test.yaml -e service_version=${GIT_COMMIT} -e "@scripts/conf/celos-params.json" -e "@scripts/conf/common-params.json"
