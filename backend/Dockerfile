# Stage 1: Build the application
FROM bellsoft/liberica-openjdk-debian:21 AS build

# Install Maven
RUN apt-get update && apt-get install -y maven

WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the application source code
COPY src ./src

# Package the application
RUN mvn package -DskipTests

# Stage 2: Create the final, lightweight image
FROM bellsoft/liberica-openjdk-debian:21

WORKDIR /app

# Copy the executable JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
