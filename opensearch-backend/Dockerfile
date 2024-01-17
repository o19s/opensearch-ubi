FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY ./target/opensearch-backend-0.0.1-SNAPSHOT.jar /tmp/
ENTRYPOINT ["java", "-jar", "/opensearch-backend-0.0.1-SNAPSHOT.jar"]
