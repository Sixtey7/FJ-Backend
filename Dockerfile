FROM openjdk:11.0.7-jre-slim

ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0"

COPY ./build/fj-service-1.0.0-SNAPSHOT-runner.jar /deployments

CMD ["java", "-jar", "/deployments/fj-service-1.0.0-SNAPSHOT-runner.jar"]