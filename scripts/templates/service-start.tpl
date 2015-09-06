#!/usr/bin/env bash
CELOS_VERSION={{ service_version }} CELOS_JETTY_TMP={{ jetty_tmp_dir }} exec java 2>&1 -cp "{{ dest_jar_path }}:/etc/hadoop/conf" {{ main_class }} --port {{ service_port }} {{ service_args }}
