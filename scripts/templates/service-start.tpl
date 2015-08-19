#!/usr/bin/env bash
CELOS_VERSION={{ service_version }} exec java -cp "{{ dest_jar_path }}:/etc/hadoop/conf" {{ main_class }} --port {{ service_port }} {{ service_args }}
