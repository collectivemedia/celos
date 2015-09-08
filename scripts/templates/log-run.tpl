#!/usr/bin/env bash
exec chpst -u {{ service_user }} svlogd -tt {{ stdout_log_path }}
