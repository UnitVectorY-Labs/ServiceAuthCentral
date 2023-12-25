# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY token ./token
COPY manage ./manage
COPY manage ./datamodel
COPY manage ./gcp
COPY pom.xml .
# Build the application
RUN mvn clean package -DskipTests -pl token

# Stage 2: Run the application
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/token/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

