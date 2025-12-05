---
description: How to deploy the FurbitoBET application
---

# Deployment Guide

This guide explains how to deploy the FurbitoBET application using Docker Compose.

## Prerequisites

- Docker
- Docker Compose

## Configuration

The application uses the following environment variables, which are configured in `docker-compose.yml`:

- `DB_URL`: Database connection URL
- `DB_USER`: Database username
- `DB_PASSWORD`: Database password
- `MAIL_USERNAME`: Gmail username for sending emails
- `MAIL_PASSWORD`: Gmail app password

> [!IMPORTANT]
> Ensure you have a `.env` file in the root directory or export the `MAIL_USERNAME` and `MAIL_PASSWORD` variables before running docker-compose, or update the `docker-compose.yml` file directly with your credentials (not recommended for production).

## Steps to Deploy

1.  **Build and Run**
    Run the following command in the root directory of the project:
    ```bash
    // turbo
    docker-compose up --build -d
    ```
    This command will:
    - Build the backend JAR.
    - Build the frontend static files.
    - Start the PostgreSQL database.
    - Start the Backend service.
    - Start the Frontend service (Nginx).
    - Start pgAdmin (Database management tool).

2.  **Access the Application**
    - **Frontend**: [http://localhost](http://localhost)
    - **Backend API**: [http://localhost:8080/api](http://localhost:8080/api)
    - **pgAdmin**: [http://localhost:5050](http://localhost:5050) (Login: `admin@admin.com` / `root`)

## Stopping the Application

To stop the application, run:
```bash
docker-compose down
```
