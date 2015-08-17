#!/usr/bin/env bash
exec chpst -u {{ service_user }} {{ start_script_path }}
