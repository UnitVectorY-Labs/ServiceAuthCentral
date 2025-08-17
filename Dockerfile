# Stage 1: Build the application
FROM maven:3-amazoncorretto-24 AS build

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
FROM gcr.io/distroless/java17-debian12
WORKDIR /app
COPY --from=build /app/build/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
