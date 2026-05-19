# 🐘 PostgreSQL con Docker

Guía rápida para correr PostgreSQL usando Docker y Docker Compose.

---

## Requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop) instalado y corriendo

---

## Opción 1: Correr con `docker run`

```bash
docker run --name mi-postgres \
  -e POSTGRES_PASSWORD=mipassword \
  -e POSTGRES_USER=miusuario \
  -e POSTGRES_DB=mibasededatos \
  -p 5432:5432 \
  -d postgres
```

### Verificar que está corriendo

```bash
docker ps
```

### Conectarse a la base de datos

```bash
docker exec -it mi-postgres psql -U miusuario -d mibasededatos
```

---

## Opción 2: Usar Docker Compose (recomendado)

Crea un archivo `docker-compose.yml` en tu proyecto:

```yaml
services:
  postgres:
    image: postgres:latest
    container_name: mi-postgres
    environment:
      POSTGRES_USER: miusuario
      POSTGRES_PASSWORD: mipassword
      POSTGRES_DB: mibasededatos
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### Comandos útiles

| Acción | Comando |
|--------|---------|
| Iniciar | `docker-compose up -d` |
| Detener | `docker-compose down` |
| Ver logs | `docker-compose logs -f` |
| Conectarse | `docker exec -it mi-postgres psql -U miusuario -d mibasededatos` |

---

## Variables de entorno

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `POSTGRES_USER` | Nombre del usuario | `postgres` |
| `POSTGRES_PASSWORD` | Contraseña (**obligatorio**) | — |
| `POSTGRES_DB` | Nombre de la base de datos | Igual que `POSTGRES_USER` |

---

## Notas importantes

- El **volumen** `postgres_data` asegura que tus datos persistan aunque el contenedor se reinicie o elimine.
- El puerto `5432` es el puerto por defecto de PostgreSQL. Puedes cambiarlo modificando la parte izquierda del mapeo: `"5433:5432"`.
- Para conectarte desde una app externa (ej. TablePlus, DBeaver), usa `localhost:5432` con las credenciales definidas.