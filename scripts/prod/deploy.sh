#!/usr/bin/env bash
set -x
set -e
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1
scripts/build-celos
ansible-playbook scripts/celos-deploy.yaml -u celos -i scripts/conf/inventory-prod-server -e service_version=${GIT_COMMIT} -e "@scripts/conf/prod-params.json" -e "@scripts/conf/common-params-server.json"
ansible-playbook scripts/celos-deploy.yaml -u celos -i scripts/conf/inventory-prod-ui -e service_version=${GIT_COMMIT} -e "@scripts/conf/prod-params.json" -e "@scripts/conf/common-params-ui.json"
