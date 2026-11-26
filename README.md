# Estante

Estante is a personal library web application. This repository contains an Angular frontend and a Java 21 / Spring Boot API backed by PostgreSQL. The backend includes PostgreSQL, Flyway migrations, category and book endpoints, Swagger UI, and a shared API error format.

## Run the backend

From the `backend` directory, run:

```sh
docker compose up --build
```

Compose starts PostgreSQL, waits for its health check, builds the API image, and starts the API. Flyway applies database migrations when the API starts.

- API base URL: <http://localhost:8081>
- Swagger UI: <http://localhost:8081/swagger-ui.html>
- OpenAPI document: <http://localhost:8081/api-docs>
- Categories: `GET /api/categories`
- Books: `GET /api/books`, `POST /api/books`

Create a book with JSON fields `title`, `author`, `genre` (a category name), and optional `notes`.

Stop the services with `docker compose down`. To also delete the local database volume and its data, use `docker compose down -v`.

## Run the frontend

The Angular app contains the library shelf, add-book form, and profile page. To run it separately, install its dependencies and start the dev server:

```sh
cd frontend
npm install
npm start
```

Open <http://localhost:4200>.

The shelf and add-book form use the backend API. Profile details are still saved only in this browser; the password field is not connected to account or authentication features.

## Project structure

The frontend groups screens by feature under `frontend/src/app/features`. Shared application services, route guards, and HTTP interceptors live under `frontend/src/app/core`, and global styles are organized by feature under `frontend/src/styles`.

The backend keeps related code together as feature modules (`book`, `account`, `author`, and `category`). Within each module, controllers handle HTTP requests, services hold business rules, and repositories handle database access. Cross-cutting security, error handling, and database migrations have their own packages or resource folders.

## API errors

API errors use a shared JSON shape with a UTC timestamp, HTTP status, Portuguese message, and a `fields` array for validation errors. Unexpected server errors return a generic message; internal exception details are not exposed to clients.

## Local database configuration

Compose provides local development credentials for the database. The API also reads `SERVER_PORT`, `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD` from the environment so deployments can provide their own values.
