FROM jdk:1.8

RUN mkdir -p /opt/mqtt-broker/config && mkdir -p /opt/mqtt-broker/persistence

COPY ./target/mqtt-broker-1.5.jar /opt/mqtt-broker/

ENV JAVA_OPTS=""

WORKDIR /opt/mqtt-broker

ENTRYPOINT exec java $JAVA_OPTS -jar mqtt-broker-1.5.jar
