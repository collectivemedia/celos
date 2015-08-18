#!/usr/bin/env bash
set -x
set -e
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1
ansible-playbook scripts/celos-deploy.yaml -u celos-ci -i scripts/conf/inventory-prod-server -e service_version=${GIT_COMMIT} -e "@scripts/conf/celos-params-all.json" -e "@scripts/conf/common-params-server.json"
ansible-playbook scripts/celos-deploy.yaml -u celos-ci -i scripts/conf/inventory-prod-ui -e service_version=${GIT_COMMIT} -e "@scripts/conf/celos-params-all.json" -e "@scripts/conf/common-params-ui.json"
