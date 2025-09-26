# eShop Database Migrations

This directory contains the Maven project for managing the eShop database schema using Flyway.

## Running Migrations from the Command Line

You can run the database migrations directly from the command line using the Maven Flyway plugin. The project is configured with profiles to make it easy to target different environments.

### Prerequisites

- Java 17 or higher
- Apache Maven

### Commands

Make sure you are in the `DB` directory before running these commands.

**1. Run Local Migrations**

This is the default profile. It targets the local PostgreSQL database as configured in `src/main/resources/application-local.properties`.

```sh
mvn flyway:migrate
```

**2. Run Production Migrations**

This command uses the `prod` profile, which targets the production database configured in `src/main/resources/application-prod.properties`.

```sh
mvn flyway:migrate -P prod
```

**3. Clean the Database (for Development Only)**

This command will drop all objects in the configured schema. This is useful for development when you want to start with a fresh, empty database. **Do not run this in production.**

- **Clean local:**
  ```sh
  mvn flyway:clean
  ```

- **Clean prod (use with extreme caution):**
  ```sh
  mvn flyway:clean -P prod
  ```

### How It Works

The `pom.xml` file contains profiles for `local` and `prod`. When you run the `mvn flyway:migrate` command, you can use the `-P` flag to specify which profile to use. Each profile points to a different properties file that contains the database connection details and other necessary configurations.
