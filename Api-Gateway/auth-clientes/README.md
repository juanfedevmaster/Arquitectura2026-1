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

---

### Option A — Full Docker (PostgreSQL + Spring Boot app)

Build and start both services in containers:

```bash
docker compose up --build
```

Run in background (detached):

```bash
docker compose up --build -d
```

Rebuild only the app image (without recreating postgres):

```bash
docker compose up --build app
```

Follow app logs:

```bash
docker compose logs -f app
```

The API will be available at `http://localhost:8080`.

---

### Option B — Local app + Docker PostgreSQL

**1. Start only PostgreSQL**
```bash
docker compose up -d postgres
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

**3. Run the application locally**
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

### Option C — Local app + local PostgreSQL (no Docker)

Requires PostgreSQL installed and running on port `5432`. Create the database first:

```sql
CREATE DATABASE auth_db;
```

Then run the application:

```bash
./mvnw spring-boot:run
```

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
# Stop the application (local): Ctrl+C

# Stop and remove all containers (postgres + app)
docker compose down

# Stop and also delete the database volume
docker compose down -v

# Stop only the postgres container
docker compose stop postgres
```

---

## Option D — Kubernetes (Docker Desktop)

The `k8s/` directory contains all the manifests needed to deploy the full stack to the Kubernetes cluster bundled with Docker Desktop.

### Structure

```
k8s/
  secret.yml    # DB credentials and JWT secret
  postgres.yml  # PVC + Deployment + ClusterIP Service for PostgreSQL
  app.yml       # Deployment + LoadBalancer Service for the Spring Boot app
```

### Prerequisites

- Docker Desktop with Kubernetes enabled  
  _(Settings → Kubernetes → Enable Kubernetes)_
- `kubectl` available in the terminal  
  _(Docker Desktop installs it automatically)_

Verify the cluster is running:

```bash
kubectl cluster-info
```

### Image registry

Newer versions of Docker Desktop run Kubernetes with **containerd**, which has its own image store separate from Docker's. Because of this, images built locally with `docker build` are not automatically visible to Kubernetes. You must make the image available through a registry before deploying.

Choose one of the two options below.

---

#### Registry option 1 — Local registry (recommended for development)

No internet or account required. Runs a registry container inside Docker Desktop.

> **Note:** macOS reserves port 5000 for ControlCenter, so the registry runs on port **5001**.

**1. Start the local registry (only once)**
```bash
# If a previous registry container exists, remove it first
docker rm registry

docker run -d -p 5001:5000 --restart=always --name registry registry:2
```

**2. Build, tag and push the image**
```bash
docker build -t auth-clientes:latest .
docker tag auth-clientes:latest localhost:5001/auth-clientes:latest
docker push localhost:5001/auth-clientes:latest
```

**3. Update `k8s/app.yml`** — change the image and pull policy:
```yaml
image: localhost:5001/auth-clientes:latest
imagePullPolicy: IfNotPresent
```

---

### Deploy

**1. Apply the manifests**

```bash
kubectl apply -f k8s/secret.yml
kubectl apply -f k8s/postgres.yml
kubectl apply -f k8s/app.yml
```

**3. Watch pods come up**

```bash
kubectl get pods -w
```

Expected output once ready:

```
NAME                              READY   STATUS    RESTARTS
auth-clientes-xxxx-xxxx           1/1     Running   0
postgres-xxxx-xxxx                1/1     Running   0
```

**4. Check services**

```bash
kubectl get services
```

The `auth-clientes` service will show `localhost` as `EXTERNAL-IP` (Docker Desktop LoadBalancer):

```
NAME            TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)
auth-clientes   LoadBalancer   10.x.x.x       localhost     8080:xxxxx/TCP
postgres        ClusterIP      10.x.x.x       <none>        5432/TCP
```

The API will be available at `http://localhost:8080`.

### Useful kubectl commands

```bash
# View logs of the app
kubectl logs -l app=auth-clientes -f

# View logs of postgres
kubectl logs -l app=postgres -f

# Describe a pod (useful for debugging startup issues)
kubectl describe pod -l app=auth-clientes

# Restart the app deployment (e.g. after rebuilding the image)
kubectl rollout restart deployment/auth-clientes

# Scale the app (horizontal)
kubectl scale deployment/auth-clientes --replicas=3
```

### Teardown

```bash
# Remove the app and postgres deployments (keeps the Secret and PVC)
kubectl delete -f k8s/app.yml
kubectl delete -f k8s/postgres.yml

# Remove everything including credentials and stored data
kubectl delete -f k8s/
```

> **Note:** deleting `k8s/postgres.yml` removes the Deployment and Service but the `PersistentVolumeClaim` (`postgres-pvc`) is deleted separately when you run `kubectl delete -f k8s/` or `kubectl delete pvc postgres-pvc`. This protects your data from accidental deletion.
