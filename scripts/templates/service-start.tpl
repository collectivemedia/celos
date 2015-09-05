#!/usr/bin/env bash
CELOS_VERSION={{ service_version }} exec java 2>&1 -Dorg.eclipse.jetty.util.log.class=org.eclipse.jetty.util.log.StdErrLog -D{classref}.LEVEL=DEBUG -cp "{{ dest_jar_path }}:/etc/hadoop/conf" {{ main_class }} --port {{ service_port }} {{ service_args }}
