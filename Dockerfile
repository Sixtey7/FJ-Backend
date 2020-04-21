FROM bellsoft/liberica-openjdk-alpine:11

ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0"

COPY ./build /deployments

CMD ["java", "-jar", "/deployments/fj-service-1.0.0-SNAPSHOT-runner.jar"]
