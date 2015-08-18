#!/usr/bin/env bash
set -e
set -x
ansible-playbook scripts/celos-purge.yaml -u celos-ci -i scripts/conf/inventory-testing-server -e "@scripts/conf/testing-params.json" -e "@scripts/conf/common-params-server.json"
ansible-playbook scripts/celos-purge.yaml -u celos-ci -i scripts/conf/inventory-testing-ui -e "@scripts/conf/testing-params.json" -e "@scripts/conf/common-params-ui.json"
