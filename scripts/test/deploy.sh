#!/usr/bin/env bash
set -e
set -x
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1
scripts/build-celos
ansible-playbook scripts/celos-deploy.yaml -u celos-ci -i scripts/test/conf/inventory-server -e "@scripts/test/conf/integration-params.json" -e "@scripts/conf/common-params-server.json" -e service_version=${GIT_COMMIT}
ansible-playbook scripts/celos-deploy.yaml -u celos-ci -i scripts/test/conf/inventory-ui -e "@scripts/test/conf/integration-params.json" -e "@scripts/conf/common-params-ui.json" -e service_version=${GIT_COMMIT}
