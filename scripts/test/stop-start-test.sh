#!/usr/bin/env bash
SERVER_PARAMS="-c ssh -u celos-ci -i scripts/inventory/integration-server -e @scripts/params/common-server.json"
UI_PARAMS="    -c ssh -u celos-ci -i scripts/inventory/integration-ui     -e @scripts/params/common-ui.json"
set -e
set -x
export ANSIBLE_SSH_ARGS=""
ansible-playbook scripts/playbooks/celos-check.yaml ${SERVER_PARAMS}
ansible-playbook scripts/playbooks/celos-check.yaml ${UI_PARAMS}
ansible-playbook scripts/playbooks/celos-stop.yaml ${SERVER_PARAMS}
ansible-playbook scripts/playbooks/celos-stop.yaml ${UI_PARAMS}
! ansible-playbook scripts/playbooks/celos-check.yaml ${SERVER_PARAMS}
! ansible-playbook scripts/playbooks/celos-check.yaml ${UI_PARAMS}
ansible-playbook scripts/playbooks/celos-start.yaml ${SERVER_PARAMS}
ansible-playbook scripts/playbooks/celos-start.yaml ${UI_PARAMS}
ansible-playbook scripts/playbooks/celos-check.yaml ${SERVER_PARAMS}
ansible-playbook scripts/playbooks/celos-check.yaml ${UI_PARAMS}
