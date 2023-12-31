# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

COPY auth-datamodel ./auth-datamodel
COPY auth-datamodel-gcp ./auth-datamodel-gcp
COPY auth-datamodel-memory ./auth-datamodel-memory
COPY auth-server-manage ./auth-server-manage
COPY auth-server-token ./auth-server-token
COPY auth-sign ./auth-sign
COPY auth-sign-gcp ./auth-sign-gcp
COPY auth-sign-local ./auth-sign-local
COPY auth-util ./auth-util

COPY pom.xml .
# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/auth-server-token/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", " -Dspring.profiles.active=datamodel-gcp,sign-gcp"]

