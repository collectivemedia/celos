#!/usr/bin/env bash
exec curl --fail "http://localhost:{{ service_port }}" &> /dev/null
