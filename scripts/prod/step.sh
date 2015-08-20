#!/usr/bin/env bash
set -x
set -e
ansible-playbook scripts/prod/kinit.yaml -u celos -i scripts/prod/conf/inventory-server -e "@scripts/prod/conf/celos-params.json" -e "@scripts/conf/common-params-server.json"
ansible-playbook scripts/prod/celos-step.yaml -u celos -i scripts/prod/conf/inventory-server -e "@scripts/prod/conf/celos-params.json" -e "@scripts/conf/common-params-server.json"
