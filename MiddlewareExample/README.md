# Middleware de Notificaciones Multi-Canal

Sistema que actúa como intermediario entre productores de eventos (E-commerce, Banco, App Móvil) y canales de notificación externos (Email, SMS, Push), implementado con **Java 17 + Spring Boot 3**.

---

## Arquitectura

### C4 Nivel 1 — Contexto

```
E-commerce  ──┐
              ├──► Middleware de Notificaciones ──► Email  (SendGrid / SMTP)
Banco       ──┤              │                 ──► SMS    (Twilio / AWS SNS)
              │              │                 ──► Push   (Firebase / APNs)
App Móvil   ──┘              │
                             ▼
                       Usuario Final
```

### C4 Nivel 3 — Pipeline interno

```
POST /api/events
       │
       ▼
NotificationController     ← @RestController · Spring Web MVC
       │
       ▼
EventValidatorService      ← @Service · Spring Security · JWT (JJWT) · JSR-380
       │
       ▼
MessageRouterService       ← @Service · Strategy Pattern · tabla de enrutamiento
       │
       ▼
MessageTransformerService  ← @Component · Jackson ObjectMapper · formato por canal
       │
       ▼
DispatcherService          ← @Service · @Async · ThreadPoolTaskExecutor
    ├──► EmailChannelSender   (SendGrid / SMTP)
    ├──► SmsChannelSender     (Twilio / AWS SNS)
    └──► PushChannelSender    (Firebase / APNs)

[transversal]
AuditLogAspect             ← @Aspect · AOP · SLF4J/Logback · Micrometer
```

---

## Stack Tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.2.5 |
| API REST | Spring Web MVC |
| Seguridad | Spring Security + JJWT 0.12.5 |
| Validación | Bean Validation (JSR-380) |
| Asincronismo | `@Async` + `ThreadPoolTaskExecutor` |
| HTTP Client | Spring WebFlux (WebClient) |
| AOP | Spring AOP |
| Métricas | Micrometer + Prometheus |
| Observabilidad | SLF4J + Logback |
| Build | Maven 3.9.6 |

---

## Estructura del Proyecto

```
MiddlewareExample/
├── pom.xml
├── mvnw                                        ← Maven Wrapper
├── .mvn/wrapper/maven-wrapper.properties
└── src/
    ├── main/
    │   ├── java/com/example/middleware/
    │   │   ├── MiddlewareApplication.java
    │   │   ├── controller/
    │   │   │   └── NotificationController.java
    │   │   ├── service/
    │   │   │   ├── EventValidatorService.java
    │   │   │   ├── MessageRouterService.java
    │   │   │   ├── MessageTransformerService.java
    │   │   │   └── DispatcherService.java
    │   │   ├── channel/
    │   │   │   ├── ChannelSender.java           ← Interface
    │   │   │   ├── EmailChannelSender.java
    │   │   │   ├── SmsChannelSender.java
    │   │   │   └── PushChannelSender.java
    │   │   ├── aspect/
    │   │   │   └── AuditLogAspect.java
    │   │   ├── model/
    │   │   │   ├── NotificationEvent.java
    │   │   │   ├── NotificationChannel.java
    │   │   │   ├── TransformedMessage.java
    │   │   │   └── ApiResponse.java
    │   │   ├── config/
    │   │   │   ├── AsyncConfig.java
    │   │   │   └── SecurityConfig.java
    │   │   └── util/
    │   │       └── JwtTokenGenerator.java
    │   └── resources/
    │       └── application.yml
    └── test/
        └── java/com/example/middleware/
            └── MiddlewareApplicationTests.java
```

---

## Requisitos Previos

- Java 17 o superior
- Maven (o usar el wrapper incluido `./mvnw`)

Verificar Java:
```bash
java -version
```

---

## Instalación y Ejecución

### 1. Clonar / Acceder al proyecto

```bash
cd MiddlewareExample
```

### 2. Compilar

```bash
./mvnw compile
# o con Maven global:
mvn compile
```

### 3. Ejecutar tests

```bash
./mvnw test
```

### 4. Levantar la aplicación

```bash
./mvnw spring-boot:run
```

La aplicación queda disponible en: `http://localhost:8080`

---

## API

### `POST /api/events`

Recibe un evento de cualquier sistema productor.

**Headers:**

| Header | Requerido | Descripción |
|---|---|---|
| `Authorization` | Sí | `Bearer <JWT>` |
| `Content-Type` | Sí | `application/json` |

**Body (JSON):**

```json
{
  "source":  "ecommerce",
  "type":    "purchase",
  "userId":  "user123",
  "payload": {
    "amount": 99.99,
    "product": "Laptop"
  }
}
```

| Campo | Tipo | Requerido | Descripción |
|---|---|---|---|
| `source` | String | Sí | `ecommerce` \| `bank` \| `mobile` |
| `type` | String | Sí | `purchase` \| `transfer` \| `login` |
| `userId` | String | Sí | Identificador del usuario |
| `payload` | Object | Sí | Datos adicionales del evento |

**Respuesta exitosa (202 Accepted):**

```json
{
  "success": true,
  "message": "Evento procesado. Canales notificados: [EMAIL, PUSH]",
  "timestamp": "2026-05-04T22:00:00Z"
}
```

**Respuesta error de autenticación (401 Unauthorized):**

```json
{
  "status": 401,
  "error": "Se requiere header Authorization: Bearer <token>"
}
```

---

## Tabla de Enrutamiento

| Source | Type | Canales |
|---|---|---|
| `ecommerce` | `purchase` | EMAIL + PUSH |
| `bank` | `transfer` | EMAIL + SMS |
| `mobile` | `login` | PUSH + SMS |
| *cualquier otro* | *cualquier otro* | EMAIL (por defecto) |

---

## Generación de Tokens JWT (Desarrollo)

La utilidad `JwtTokenGenerator` genera tokens válidos para los tres sistemas:

```bash
./mvnw exec:java -Dexec.mainClass="com.example.middleware.util.JwtTokenGenerator"
```

Salida esperada:

```
=== ecommerce-system ===
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

=== bank-system ===
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

=== mobile-app ===
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Ejemplo con curl

**Evento 1 — E-commerce: compra realizada**
```bash
curl -s -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJlY29tbWVyY2Utc3lzdGVtIiwic291cmNlIjoiZWNvbW1lcmNlLXN5c3RlbSIsImlhdCI6MTc3Nzk1MzQyNSwiZXhwIjoxNzc4MDM5ODI1fQ.0TKNTAdzXVYvssxrP9AJzvOlPC8yFJB4HPPkQR4EHB5hYkBvRsho0Wnm0oszHBkG" \
  -d '{
    "source": "ecommerce",
    "type": "purchase",
    "userId": "user123",
    "payload": {"amount": 99.99, "product": "Laptop"}
  }'
```
→ Canales notificados: **EMAIL + PUSH**

---

**Evento 2 — Banco: transferencia bancaria**
```bash
curl -s -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJiYW5rLXN5c3RlbSIsInNvdXJjZSI6ImJhbmstc3lzdGVtIiwiaWF0IjoxNzc3OTUzNDI1LCJleHAiOjE3NzgwMzk4MjV9.S-I-WULqLXHUkRDjhtdNtRG8I6iS6CJFjeaQiNXLeKMtl-mvPu_BmInhP-F-91t8" \
  -d '{
    "source": "bank",
    "type": "transfer",
    "userId": "user456",
    "payload": {"amount": 500.0, "destination": "ACC-789"}
  }'
```
→ Canales notificados: **EMAIL + SMS**

---

**Evento 3 — App Móvil: inicio de sesión nuevo**
```bash
curl -s -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJtb2JpbGUtYXBwIiwic291cmNlIjoibW9iaWxlLWFwcCIsImlhdCI6MTc3Nzk1MzQyNSwiZXhwIjoxNzc4MDM5ODI1fQ.KH-EamcOnXeJfXmDiAy9htprbqOSdeRW0vcUI-4v5yMMTw5iCMUcUIV41pa7NnQv" \
  -d '{
    "source": "mobile",
    "type": "login",
    "userId": "user789",
    "payload": {"location": "Bogotá", "ip": "192.168.1.1"}
  }'
```
→ Canales notificados: **PUSH + SMS**

> **Nota:** Los tokens tienen vigencia de 24h desde su generación. Si expiran, ejecuta `./mvnw exec:java -Dexec.mainClass="com.example.middleware.util.JwtTokenGenerator"` para obtener nuevos.

---

## Modelo de Asincronismo

El pipeline combina procesamiento **síncrono** y **asíncrono** de forma deliberada:

```
HTTP Request Thread (síncrono)
│
├── NotificationController.receiveEvent()   ← síncrono
├── EventValidatorService.validateToken()   ← síncrono
├── MessageRouterService.route()            ← síncrono
├── MessageTransformerService.transform()   ← síncrono
│   └─ responde 202 Accepted al cliente ──────────────────┐
│                                                          │
└── DispatcherService.dispatch()            ← @Async      │
        ├── EmailChannelSender.send()   ← ThreadPool      │
        ├── SmsChannelSender.send()     ← ThreadPool      │
        └── PushChannelSender.send()    ← ThreadPool      │
                                                          ▼
                                              Cliente ya recibió respuesta
```

### ¿Por qué los primeros 4 pasos son síncronos?

Son síncronos **a propósito** — necesitan completarse antes de responder al cliente para poder retornar el estado correcto:

- Si el JWT es inválido → responde `401 Unauthorized` de inmediato
- Si el body es inválido → responde `400 Bad Request` de inmediato
- Si todo es válido → responde `202 Accepted` con los canales resueltos

```json
{"success": true, "message": "Evento procesado. Canales notificados: [EMAIL, PUSH]"}
```

Si la validación fuera asíncrona, el cliente no sabría si el evento fue aceptado o rechazado.

### ¿Por qué el Dispatcher es asíncrono?

Es la parte **lenta** del pipeline — llamar a SendGrid, Twilio o Firebase puede tomar cientos de milisegundos o fallar por red. Con `@Async`:

- El hilo HTTP se libera de inmediato tras el `202 Accepted`
- Los canales se despachan **en paralelo** en el `ThreadPoolTaskExecutor`
- Un canal lento (ej. Firebase) no bloquea a los demás (ej. Email)

### Configuración del ThreadPool (`AsyncConfig`)

| Parámetro | Valor | Descripción |
|---|---|---|
| `corePoolSize` | 5 | Hilos siempre activos |
| `maxPoolSize` | 20 | Máximo bajo carga alta |
| `queueCapacity` | 100 | Cola de tareas pendientes |
| `threadNamePrefix` | `notification-` | Identificable en logs |
| `awaitTerminationSeconds` | 30 | Espera al shutdown para completar despachos |

---

## Monitoreo

| Endpoint | Descripción |
|---|---|
| `GET /actuator/health` | Estado de la aplicación |
| `GET /actuator/prometheus` | Métricas para Prometheus/Grafana |
| `GET /actuator/metrics` | Métricas en formato JSON |

El `AuditLogAspect` expone automáticamente métricas de latencia por componente con el patrón:

```
middleware.<componente>.<metodo>{status="success|error"}
```

---

## Configuración (`application.yml`)

| Propiedad | Descripción | Valor por defecto |
|---|---|---|
| `middleware.jwt.secret` | Clave secreta para firmar/verificar JWT (mínimo 32 chars) | Ver `application.yml` |
| `server.port` | Puerto del servidor | `8080` |

> **Producción:** Externalizar `middleware.jwt.secret` como variable de entorno o secret manager.

---

## Extensión — Agregar un Canal Nuevo

1. Implementar la interfaz `ChannelSender`:

```java
@Component
public class WhatsAppChannelSender implements ChannelSender {

    @Override
    public NotificationChannel getChannel() { return NotificationChannel.WHATSAPP; }

    @Override
    public void send(TransformedMessage message) {
        // llamada a la API de WhatsApp Business
    }
}
```

2. Agregar el valor en el enum `NotificationChannel`:

```java
public enum NotificationChannel { EMAIL, SMS, PUSH, WHATSAPP }
```

3. Agregar reglas en `MessageRouterService` y casos en `MessageTransformerService`.

Spring inyecta automáticamente el nuevo sender en `DispatcherService` sin modificar nada más.
