FROM maven:3.9.9-amazoncorretto-23 AS builder

WORKDIR /workspace

COPY ./ /workspace

RUN mvn clean install -DskipTests

FROM openjdk:23 AS final
LABEL Description="Api gateway"

COPY --from=builder /workspace/target/api-gateway.jar /workspace/api-gateway.jar

WORKDIR /workspace
CMD java $JAVA_OPTS -jar ./api-gateway.jar