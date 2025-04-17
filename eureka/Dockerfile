FROM maven:3.9.9-amazoncorretto-23 AS builder

WORKDIR /workspace

COPY ./ /workspace

RUN mvn clean install -DskipTests

FROM openjdk:23 AS final
LABEL Description="Eureka"

COPY --from=builder /workspace/target/eureka.jar /workspace/eureka.jar

WORKDIR /workspace
CMD java $JAVA_OPTS -jar ./eureka.jar