# Stage 1: Build the application
FROM maven:3.9-amazoncorretto-17 AS build

WORKDIR /app

# Copy the necessary files to compile the app
COPY auth-* pom.xml ./

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
COPY --from=build /app/auth-server-token/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

