#!/usr/bin/env bash
set -x
set -e
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1
scripts/build-celos
ansible-playbook scripts/prod/kinit.yaml      -u celos-ci -i scripts/test/conf/inventory-server -e "@scripts/test/conf/integration-params.json" -e "@scripts/conf/common-params-server.json"
ansible-playbook scripts/celos-deploy.yaml    -u celos-ci -i scripts/test/conf/inventory-server -e "@scripts/test/conf/integration-params.json" -e "@scripts/conf/common-params-server.json" -e service_version=${GIT_COMMIT}
ansible-playbook scripts/celos-deploy.yaml    -u celos-ci -i scripts/test/conf/inventory-ui     -e "@scripts/test/conf/integration-params.json" -e "@scripts/conf/common-params-ui.json"     -e service_version=${GIT_COMMIT}
ansible-playbook scripts/test/stop-start.yaml -u celos-ci -i scripts/test/conf/inventory-server -e "@scripts/test/conf/integration-params.json" -e "@scripts/conf/common-params-server.json"
ansible-playbook scripts/test/stop-start.yaml -u celos-ci -i scripts/test/conf/inventory-ui     -e "@scripts/test/conf/integration-params.json" -e "@scripts/conf/common-params-ui.json"
ansible-playbook scripts/test/celos_test.yaml -u celos-ci -i scripts/test/conf/inventory-server -e "@scripts/test/conf/integration-params.json" -e "@scripts/conf/common-params-server.json" -e service_version=${GIT_COMMIT}
ansible-playbook scripts/test/ui-test.yaml    -u celos-ci -i scripts/test/conf/inventory-ui     -e "@scripts/test/conf/integration-params.json" -e "@scripts/conf/common-params-ui.json"     -e service_version=${GIT_COMMIT}
ansible-playbook scripts/celos-purge.yaml     -u celos-ci -i scripts/test/conf/inventory-server -e "@scripts/test/conf/integration-params.json" -e "@scripts/conf/common-params-server.json"
ansible-playbook scripts/celos-purge.yaml     -u celos-ci -i scripts/test/conf/inventory-ui     -e "@scripts/test/conf/integration-params.json" -e "@scripts/conf/common-params-ui.json"
