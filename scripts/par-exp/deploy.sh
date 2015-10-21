#!/usr/bin/env bash
set -x
set -e
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1
export ANSIBLE_SSH_ARGS=""
scripts/build.sh
ansible-playbook scripts/celos-deploy.yaml -c ssh -u celos-ci -i scripts/par-exp/conf/inventory-server -e "@scripts/par-exp/conf/celos-params.json" -e "@scripts/conf/common-params-server.json" -e service_version=${GIT_COMMIT}
ansible-playbook scripts/celos-deploy.yaml -c ssh -u celos-ci -i scripts/par-exp/conf/inventory-ui     -e "@scripts/par-exp/conf/celos-params.json" -e "@scripts/conf/common-params-ui.json" -e service_version=${GIT_COMMIT}

