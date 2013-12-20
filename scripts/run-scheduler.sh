#!/bin/bash
set -e
set -x

# Script for integration testing - runs scheduler until no slots
# are READY or RUNNING, i.e. all slots have run to completion.

TIME=$1
WORKFLOW=$2

schedule() {
    curl --fail -X POST "http://localhost:8080/celos/scheduler?time=$TIME"
}

running() {
    curl --fail "http://localhost:8080/celos/workflow?time=$TIME&id=$WORKFLOW" | grep "READY\|RUNNING"
}

schedule
while running
do
    schedule
    sleep 1
done
