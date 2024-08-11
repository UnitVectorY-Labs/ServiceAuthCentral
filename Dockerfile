# Stage 1: Build the application
FROM maven:3.9-amazoncorretto-17 AS build

# Define arguments for Maven profile and JAR file name
# Can pass value of "server-manage" to compile the manage server
ARG MAVEN_PROFILE=server-token

WORKDIR /app

# Copy your project files
COPY . .

# Build the application
RUN mvn clean package -DskipTests -P${MAVEN_PROFILE} -ntp && \
  rm -rf /app/${MAVEN_PROFILE}/target/*-javadoc.jar && \
  mkdir -p /app/build && \
  mv /app/${MAVEN_PROFILE}/target/*.jar /app/build/


# Stage 2: Run the application
FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
COPY --from=build /app/build/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
