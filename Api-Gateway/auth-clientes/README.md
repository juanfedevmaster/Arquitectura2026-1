# Auth Clientes — Authentication API

REST API for user authentication built with **Spring Boot 3.5**, **Spring Security**, **JWT**, **JPA** and **PostgreSQL**.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security 6 + JWT (jjwt 0.12.6) |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL 16 |
| Documentation | SpringDoc OpenAPI (Swagger UI) |

---

## Database Schema

```
roles
  id   BIGSERIAL PK
  name VARCHAR(30) UNIQUE NOT NULL

users
  id       BIGSERIAL PK
  username VARCHAR(50)  UNIQUE NOT NULL
  email    VARCHAR(100) UNIQUE NOT NULL
  password VARCHAR      NOT NULL

user_roles  (join table)
  user_id  FK → users.id
  role_id  FK → roles.id
```

Tables are created automatically by JPA (`ddl-auto: update`) on first startup.  
Roles `ROLE_USER` and `ROLE_ADMIN` are seeded automatically at startup.

---

## Endpoints

Base URL: `http://localhost:8080/api/v1/auth`

| Method | Path | Auth required | Description |
|--------|------|:---:|---|
| `POST` | `/register` | No | Register a new user |
| `POST` | `/login` | No | Login and get JWT token |

### POST `/register`

**Request body:**
```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret123"
}
```

**Response `201 Created`:**
```json
{
  "token": "<jwt>",
  "type": "Bearer",
  "username": "john",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

### POST `/login`

**Request body:**
```json
{
  "username": "john",
  "password": "secret123"
}
```

**Response `200 OK`:**
```json
{
  "token": "<jwt>",
  "type": "Bearer",
  "username": "john",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

### Using the token

Add the header to protected requests:
```
Authorization: Bearer <token>
```

---

## Configuration

Key values in `src/main/resources/application.yml`:

| Property | Value |
|---|---|
| Server port | `8080` |
| PostgreSQL host | `localhost:5433` |
| Database | `auth_db` |
| DB user | `postgres` |
| DB password | `admin` |
| JWT expiration | `86400000` ms (24 h) |

> **Production:** replace `jwt.secret` with a secure random Base64 key (minimum 32 bytes decoded).

---

## Running the Application

### Prerequisites

- Docker Desktop running
- JDK 21
- Maven (or use the included `./mvnw` wrapper)

### Steps

**1. Start PostgreSQL**
```bash
docker compose up -d
```

**2. Verify the container is healthy**
```bash
docker compose ps
```

Expected output:
```
NAME            STATUS
auth_postgres   Up (healthy)
```

**3. Run the application**
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

## Swagger UI

Open in browser after starting the application:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON spec:
```
http://localhost:8080/v3/api-docs
```

---

## Stop

```bash
# Stop the application: Ctrl+C

# Stop and remove the PostgreSQL container
docker compose down

# Stop and also delete the database volume
docker compose down -v
```
