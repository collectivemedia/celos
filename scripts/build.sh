#!/usr/bin/env bash
set -x
set -e
CELOS_VERSION=test ./gradlew clean celos-server:fatJar
