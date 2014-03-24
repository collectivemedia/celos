# Running Celos

Celos runs under Tomcat6 on `celos001.ny7.collective-media.net`.

The usual `/etc/init.d/tomcat6 {start|stop|restart}` commands are used
for starting and stopping it.

Logs: `/var/log/celos/celos.log`

Same logs are also in logstash: http://logstash001.ny7.collective-media.net

Configuration files: `/etc/celos/workflows`

State database: `/var/lib/celos/db`

It runs as user: `celos`
