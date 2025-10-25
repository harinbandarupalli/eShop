# Use an official Maven image to get a Java runtime and Maven
FROM maven:3.8.5-openjdk-17

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml first to leverage Docker layer caching
COPY pom.xml .

# Explicitly resolve and download plugin dependencies (like the JDBC driver for Flyway)
RUN mvn dependency:resolve-plugins

# Download all project dependencies. This layer is only rebuilt if pom.xml changes.
RUN mvn dependency:go-offline

# Copy the rest of the project files (the SQL migrations)
COPY . /app

# Run the Flyway migration using the 'docker' profile
CMD ["mvn", "flyway:migrate", "-P", "docker"]
