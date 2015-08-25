#!/usr/bin/env bash
SERVER_PARAMS="-u celos-ci -i scripts/test/conf/inventory-server -e @scripts/test/conf/integration-params.json -e @scripts/conf/common-params-server.json -e service_version=${GIT_COMMIT}"
UI_PARAMS="    -u celos-ci -i scripts/test/conf/inventory-ui     -e @scripts/test/conf/integration-params.json -e @scripts/conf/common-params-ui.json     -e service_version=${GIT_COMMIT}"
set -x
set -e
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1
scripts/build-celos
ansible-playbook scripts/prod/kinit.yaml ${SERVER_PARAMS}
ansible-playbook scripts/celos-deploy.yaml ${SERVER_PARAMS}
ansible-playbook scripts/celos-deploy.yaml ${UI_PARAMS}
./scripts/test/stop-start-test.sh
ansible-playbook scripts/test/celos_test.yaml ${SERVER_PARAMS}
ansible-playbook scripts/test/ui-test.yaml ${UI_PARAMS}
ansible-playbook scripts/celos-purge.yaml ${SERVER_PARAMS}
ansible-playbook scripts/celos-purge.yaml ${UI_PARAMS}
