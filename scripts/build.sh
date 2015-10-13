#!/usr/bin/env bash
set -x
set -e
CELOS_VERSION=test ./gradlew clean test :celos-server:installApp :celos-ui:installApp celos-ci:fatJar
