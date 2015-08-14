#!/usr/bin/env bash -xe

GIT_COMMIT=${GIT_COMMIT:-undefined}
ansible-playbook -i scripts/test/inventory -u obaskakov scripts/celos-deploy.yaml -e version=${GIT_COMMIT}
ansible-playbook -i scripts/test/inventory -u obaskakov scripts/test/celos_test.yaml -e version=${GIT_COMMIT}
