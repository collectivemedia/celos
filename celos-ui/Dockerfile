FROM anapsix/alpine-java:8_jdk_unlimited

ARG jarfile=celos-ui.jar

RUN mkdir -p /opt/celos \
    && mkdir -p /etc/celos

COPY $jarfile /opt/celos/celos-ui.jar

COPY ui-config.json /etc/celos/ui.json

ENV CELOS_URL=http://celos \
    PORT=80

VOLUME /etc/celos

EXPOSE 8080

ENTRYPOINT ["bash", "-c"]

CMD ["java -jar /opt/celos/celos-ui.jar --port $PORT --celos $CELOS_URL --config /etc/celos/ui.json"]
