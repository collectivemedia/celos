#!/usr/bin/env bash
SERVER_PARAMS="-c ssh -u celos-ci -i scripts/test/conf/inventory-server -e @scripts/test/conf/integration-params.json -e @scripts/conf/common-params-server.json"
UI_PARAMS="    -c ssh -u celos-ci -i scripts/test/conf/inventory-ui     -e @scripts/test/conf/integration-params.json -e @scripts/conf/common-params-ui.json"
set -e
set -x
ansible-playbook scripts/prod/celos-check.yaml ${SERVER_PARAMS}
ansible-playbook scripts/prod/celos-check.yaml ${UI_PARAMS}
ansible-playbook scripts/prod/celos-stop.yaml ${SERVER_PARAMS}
ansible-playbook scripts/prod/celos-stop.yaml ${UI_PARAMS}
! ansible-playbook scripts/prod/celos-check.yaml ${SERVER_PARAMS}
! ansible-playbook scripts/prod/celos-check.yaml ${UI_PARAMS}
ansible-playbook scripts/prod/celos-start.yaml ${SERVER_PARAMS}
ansible-playbook scripts/prod/celos-start.yaml ${UI_PARAMS}
ansible-playbook scripts/prod/celos-check.yaml ${SERVER_PARAMS}
ansible-playbook scripts/prod/celos-check.yaml ${UI_PARAMS}
