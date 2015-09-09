#!/usr/bin/env bash
exec curl --fail "http://localhost:{{ service_port }}/version" &> /dev/null
