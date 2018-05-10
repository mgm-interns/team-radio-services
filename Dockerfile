FROM openjdk:8-jdk
COPY target/radio-services-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8081/tcp
ENTRYPOINT ["java", "-jar", "/app.jar"]