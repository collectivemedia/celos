#!/usr/bin/env bash
set -x
set -e
ansible-playbook scripts/prod/celos-step.yaml -u celos -i scripts/conf/inventory-prod-server -e "@scripts/conf/prod-params.json" -e "@scripts/conf/common-params-server.json"
