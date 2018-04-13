FROM openjdk:8-jdk-alpine
COPY target/radio-services-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8080/tcp
ENTRYPOINT ["java", "-jar", "/app.jar"]