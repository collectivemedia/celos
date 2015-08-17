#!/bin/bash
set -e
set -x

# Script for integration testing - runs scheduler until no slots
# are READY or RUNNING, i.e. all slots have run to completion.

SERVICE_URL=$1
TIME=$2
WORKFLOW=$3

schedule() {
    curl --fail -X POST "${SERVICE_URL}/scheduler?time=$TIME&ids=$WORKFLOW"
}

running() {
    curl --fail "${SERVICE_URL}/workflow?time=$TIME&id=$WORKFLOW" | grep "READY\|RUNNING"
}

schedule
while running
do
    schedule
    sleep 1
done
