#!/usr/bin/env bash
set -x
set -e
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1
ansible-playbook scripts/celos-deploy.yaml -i scripts/conf/inventory-testing-server -e service_version=${GIT_COMMIT} -e "@scripts/conf/celos-params-all.json" -e "@scripts/conf/common-params-server.json"
ansible-playbook scripts/celos-deploy.yaml -i scripts/conf/inventory-testing-ui     -e service_version=${GIT_COMMIT} -e "@scripts/conf/celos-params-all.json" -e "@scripts/conf/common-params-ui.json"
ansible-playbook scripts/test/celos_test.yaml -i scripts/conf/inventory-testing-server -e service_version=${GIT_COMMIT} -e "@scripts/conf/celos-params-all.json" -e "@scripts/conf/common-params-server.json"
