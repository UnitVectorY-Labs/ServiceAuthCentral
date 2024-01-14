# Stage 1: Build the application
FROM maven:3.9-amazoncorretto-17 AS build

WORKDIR /app

COPY auth-common ./auth-common
COPY auth-datamodel ./auth-datamodel
COPY auth-datamodel-gcp ./auth-datamodel-gcp
COPY auth-datamodel-couchbase ./auth-datamodel-couchbase
COPY auth-datamodel-memory ./auth-datamodel-memory
COPY auth-server-manage ./auth-server-manage
COPY auth-server-token ./auth-server-token
COPY auth-sign ./auth-sign
COPY auth-sign-gcp ./auth-sign-gcp
COPY auth-sign-local ./auth-sign-local
COPY auth-verify ./auth-verify
COPY auth-verify-auth0 ./auth-verify-auth0
COPY auth-util ./auth-util
# Copy the necessary files to compile the app
COPY auth-* pom.xml ./

# Build the application
RUN mvn clean package -DskipTests -ntp

# Stage 2: Run the application
FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
COPY --from=build /app/auth-server-token/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

