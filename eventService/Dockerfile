FROM maven:3.9.9-amazoncorretto-23 AS builder

WORKDIR /workspace
VOLUME ~/.m2:/root/.m2

COPY ./ /workspace

RUN mvn install -DskipTests

FROM openjdk:23 AS final
LABEL Description="Server for High Load Project"

COPY --from=builder /workspace/target/events-0.0.1-SNAPSHOT.jar /workspace/server.jar
WORKDIR /workspace
CMD java $JAVA_OPTS -jar ./server.jar