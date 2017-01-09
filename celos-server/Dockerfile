FROM anapsix/alpine-java:8_jdk_unlimited

ARG jarfile=celos-server.jar

RUN mkdir -p /opt/celos \
    && mkdir -p /etc/celos \
    && mkdir -p /etc/celos/state-db \
    && mkdir -p /etc/celos/workflows \
    && mkdir -p /etc/celos/defaults

COPY $jarfile /opt/celos/celos-server.jar

VOLUME /etc/celos

ENV PORT=80

EXPOSE 80

ENTRYPOINT ["sh", "-c"]

CMD ["java -jar /opt/celos/celos-server.jar --autoSchedule 60 --db /etc/celos/state-db --workflows \
     /etc/celos/workflows --defaults /etc/celos/defaults --port $PORT --stdout"]
