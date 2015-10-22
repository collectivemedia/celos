#!/usr/bin/env bash
set -x
set -e
export ANSIBLE_SSH_ARGS=""
ansible-playbook ./scripts/prod/celos-start.yaml -u celos-ci -c ssh -i scripts/par-exp/conf/inventory-server -e "@scripts/par-exp/conf/celos-params.json"
