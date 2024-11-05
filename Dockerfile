
FROM openjdk:23-slim
WORKDIR /app
COPY target/recipes-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
