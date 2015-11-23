#!/usr/bin/env bash
export ANSIBLE_SSH_ARGS=""
SERVER_PARAMS="-c ssh -u celos-ci -i scripts/test/conf/inventory-server -e @scripts/test/conf/integration-params.json -e @scripts/test/conf/server-params.json -e service_version=${GIT_COMMIT}"
UI_PARAMS="    -c ssh -u celos-ci -i scripts/test/conf/inventory-ui     -e @scripts/test/conf/integration-params.json -e @scripts/conf/common-params-ui.json     -e service_version=${GIT_COMMIT}"
set -x
set -e
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1
scripts/build.sh
ansible-playbook scripts/prod/kinit.yaml ${SERVER_PARAMS}
ansible-playbook scripts/celos-deploy.yaml ${SERVER_PARAMS}
ansible-playbook scripts/celos-deploy.yaml ${UI_PARAMS}
./scripts/test/stop-start-test.sh
ansible-playbook scripts/test/celos_test.yaml ${SERVER_PARAMS}
ansible-playbook scripts/test/ui-test.yaml ${UI_PARAMS}
ansible-playbook scripts/celos-purge.yaml ${SERVER_PARAMS}
ansible-playbook scripts/celos-purge.yaml ${UI_PARAMS}
