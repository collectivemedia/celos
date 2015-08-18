#!/usr/bin/env bash
set -x
set -e
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1
ansible-playbook -i scripts/test/inventory -u celos-ci scripts/test/celos-dirs.yaml
ansible-playbook -i scripts/test/inventory -u celos-ci scripts/celos-deploy.yaml -e version=${GIT_COMMIT}
ansible-playbook -i scripts/test/inventory -u celos-ci scripts/test/celos_test.yaml -e version=${GIT_COMMIT}
