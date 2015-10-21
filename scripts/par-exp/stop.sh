#!/usr/bin/env bash
set -x
set -e
ansible-playbook ./scripts/prod/celos-stop.yaml -u celos-ci -c ssh -i scripts/par-exp/conf/inventory-server -e "@scripts/prod/conf/celos-params.json"