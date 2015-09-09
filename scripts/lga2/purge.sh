#!/usr/bin/env bash
set -e
set -x
ansible-playbook scripts/celos-purge.yaml -u celos-ci -i scripts/test/conf/inventory-server -e "@scripts/test/conf/integration-params.json" -e "@scripts/conf/common-params-server.json"
ansible-playbook scripts/celos-purge.yaml -u celos-ci -i scripts/test/conf/inventory-ui -e "@scripts/test/conf/integration-params.json" -e "@scripts/conf/common-params-ui.json"
