#!/usr/bin/env bash
set -x
set -e
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1
ansible-playbook scripts/celos-deploy.yaml -u celos-ci -i scripts/conf/inventory-testing-server -e service_version=${GIT_COMMIT} -e "@scripts/conf/integration-params.json" -e "@scripts/conf/common-params-server.json"
ansible-playbook scripts/test/celos_test.yaml -u celos-ci -i scripts/conf/inventory-testing-server -e service_version=${GIT_COMMIT} -e "@scripts/conf/integration-params.json" -e "@scripts/conf/common-params-server.json"
ansible-playbook scripts/celos-purge.yaml -u celos-ci -i scripts/conf/inventory-testing-server -e "@scripts/conf/integration-params.json" -e "@scripts/conf/common-params-server.json"