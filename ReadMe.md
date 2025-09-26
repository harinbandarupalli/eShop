# eShop Full-Stack Application

This project contains the frontend, backend, and database migrations for the eShop application, fully containerized with Docker.

## Full-Stack Setup with Docker Compose

This project uses Docker and Docker Compose to create a complete, multi-service development environment. With a single command, you can build and launch the entire application stack, including the frontend, backend, and a PostgreSQL database with all migrations applied.

### Prerequisites

- Docker
- Docker Compose

### How to Launch the Entire Application

1.  **Build and Start All Services:**

    Open a terminal in the root directory of the project and run the following command. The `--build` flag ensures that Docker rebuilds your application images if there are any code changes.

    ```sh
    docker-compose up --build
    ```

    This command orchestrates the following startup sequence:
    1.  **PostgreSQL Database:** Starts a `postgres:16` container.
    2.  **Flyway Migrations:** Waits for the database to be healthy, then builds and runs a container that executes all database migrations.
    3.  **Backend Service:** Waits for the migrations to complete successfully, then builds and starts the Spring Boot backend container.
    4.  **Frontend Service:** Builds and starts the Angular frontend container, which is served by Nginx.

2.  **Accessing the Services:**

    Once all services are running, you can access them at the following locations:

    -   **Frontend Application:** [http://localhost:4200](http://localhost:4200)
    -   **Backend API:** [http://localhost:8080](http://localhost:8080)
    -   **PostgreSQL Database:**
        -   **Host:** `localhost`
        -   **Port:** `5432`
        -   **Database:** `eshop`
        -   **User:** `local_user`
        -   **Password:** `local_password`

### Stopping the Environment

-   To stop all the running containers, press `Ctrl+C` in the terminal where `docker-compose up` is running.
-   To stop and completely remove the containers, networks, and volumes, run:

    ```sh
    docker-compose down
    ```

## Advanced Usage: Running Specific Services

Docker Compose allows you to run specific services and their dependencies, which is useful for different development scenarios.

### Scenario 1: Run Only the Database and Migrations

Ideal for database work or connecting a local tool.

```sh
docker-compose up flyway --build
```

**What it does:** Starts the `postgres` service and then runs the `flyway` migrations. No backend or frontend services will be started.

### Scenario 2: Run the Backend and Database

Perfect for backend API development without the frontend.

```sh
docker-compose up backend --build
```

**What it does:** Starts `postgres`, runs `flyway` migrations, and then starts the `backend` service.

### Scenario 3: Run the Full Stack

This is the default behavior and launches every service.

```sh
docker-compose up --build
```
