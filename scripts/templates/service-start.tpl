#!/usr/bin/env bash
CELOS_VERSION="{{ service_version }}" JAVA_OPTS="{{jvm_opts}}" exec {{ dest_path }}/{{ start_script }} 2>&1 --port {{ service_port }} {{ service_args }}
