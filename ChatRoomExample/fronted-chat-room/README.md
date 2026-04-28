# ChatRoom Frontend

UI web para un chat en tiempo real usando **SockJS** + **STOMP** sobre WebSockets, construida con **HTML5**, **Bootstrap 5**, **CSS** y **JavaScript** vanilla.

---

## Requisitos previos

| Herramienta | Versión mínima |
|---|---|
| Node.js | 18+ |
| npm | 9+ |
| Backend ChatRoom | corriendo en `http://localhost:8080` |

---

## Estructura del proyecto

```
fronted-chat-room/
├── index.html          # Pantalla de entrada y pantalla de chat
├── package.json        # Dependencias npm
├── css/
│   └── style.css       # Estilos personalizados
└── js/
    └── chat.js         # Lógica STOMP / WebSocket y DOM
```

---

## Instalación y ejecución

```bash
# 1. Ir al directorio del proyecto
cd fronted-chat-room

# 2. Instalar dependencias
npm install

# 3. Iniciar servidor de desarrollo (puerto 3000)
npm start
```

Abrir el navegador en **http://localhost:3000**.

> El backend debe estar corriendo en `http://localhost:8080` antes de usar el chat.

---

## Dependencias

| Paquete | Versión | Uso |
|---|---|---|
| `sockjs-client` | ^1.6.1 | Transporte WebSocket con fallback HTTP |
| `@stomp/stompjs` | ^7.0.0 | Protocolo STOMP sobre el socket |
| `serve` (dev) | ^14.2.4 | Servidor estático para desarrollo |

Las librerías SockJS y STOMP también se cargan desde CDN en `index.html` para uso directo sin bundler:

```html
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@7/bundles/stomp.umd.min.js"></script>
```

---

## Configuración del backend

El endpoint WebSocket está definido en `js/chat.js`:

```js
const WS_URL = 'http://localhost:8080/ws';
```

Cambia este valor si el backend corre en un host o puerto diferente.

---

## Flujo de comunicación

```
UI                                    Backend
│                                        │
│── SockJS connect ─────────────────────>│  http://localhost:8080/ws
│                                        │
│── PUBLISH /app/chat/{roomId}/join ────>│  Anunciar ingreso
│<─ SUBSCRIBE /topic/chat/{roomId} ──────│  Recibir mensajes (JOIN/CHAT/LEAVE)
│<─ SUBSCRIBE /topic/chat/{roomId}/typing│  Recibir indicador de escritura
│                                        │
│── PUBLISH /app/chat/{roomId}/typing ──>│  Notificar que está escribiendo
│── PUBLISH /app/chat/{roomId}/send ────>│  Enviar mensaje
│── PUBLISH /app/chat/{roomId}/leave ───>│  Anunciar salida
│                                        │
│── disconnect ─────────────────────────>│
```

### Destinos STOMP

| Acción | Destino | Dirección |
|---|---|---|
| Unirse a sala | `/app/chat/{roomId}/join` | Publicar |
| Enviar mensaje | `/app/chat/{roomId}/send` | Publicar |
| Indicador de escritura | `/app/chat/{roomId}/typing` | Publicar |
| Salir de sala | `/app/chat/{roomId}/leave` | Publicar |
| Recibir mensajes | `/topic/chat/{roomId}` | Suscribirse |
| Recibir typing | `/topic/chat/{roomId}/typing` | Suscribirse |

### Formato del payload (JSON)

**Mensajes de sala** (`JOIN`, `CHAT`, `LEAVE`):
```json
{
  "sender":  "nombreDeUsuario",
  "roomId":  "idDeSala",
  "content": "texto del mensaje",
  "type":    "JOIN | CHAT | LEAVE"
}
```
El campo `content` solo es requerido para mensajes de tipo `CHAT`.

**Indicador de escritura** (`TYPING`, `STOP_TYPING`):
```json
{
  "type":   "TYPING | STOP_TYPING",
  "sender": "nombreDeUsuario"
}
```

---

## Indicador de escritura ("está escribiendo…")

Cuando un usuario escribe en el input se publica automáticamente `TYPING` al servidor. Si deja de escribir durante **2 segundos**, se publica `STOP_TYPING`. Al enviar un mensaje también se cancela el indicador de inmediato.

```
Usuario A escribe  ──> PUBLISH /typing { type: "TYPING",      sender: "A" }
2 s sin actividad  ──> PUBLISH /typing { type: "STOP_TYPING", sender: "A" }
```

Los demás participantes reciben estos eventos vía `/topic/chat/{roomId}/typing` y ven el texto animado **"X está escribiendo…"** encima del input.

| Condición | Texto mostrado |
|---|---|
| 1 persona escribiendo | `Juan está escribiendo` |
| 2 personas escribiendo | `Juan y Laura están escribiendo` |
| 3+ personas escribiendo | `Varios participantes están escribiendo` |

---

## Lista de participantes

La lista "En la sala" combina dos mecanismos para estar siempre actualizada:

| Mecanismo | Cuándo actúa |
|---|---|
| Tracking local inmediato | Al conectarse, al recibir JOIN/LEAVE vía WebSocket |
| Sincronización REST | Al conectarse (+600 ms), al recibir JOIN/LEAVE (+300 ms), cada 8 s |

El endpoint REST consultado es:

```
GET http://localhost:8080/api/rooms/{roomId}/participants
```

Acepta como respuesta `string[]` o `object[]` con campo `username` o `name`. Si la petición falla (por ejemplo, por CORS no configurado), la lista local sigue siendo válida.

### Requisito CORS en el backend

Para que la llamada REST funcione desde `http://localhost:3000`, el backend Spring Boot debe permitir ese origen. Opción mínima con anotación:

```java
@CrossOrigin(origins = "http://localhost:3000")
@GetMapping("/api/rooms/{roomId}/participants")
public ResponseEntity<?> getParticipants(@PathVariable String roomId) { ... }
```

Opción global recomendada:

```java
@Bean
public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000")
                    .allowedMethods("GET", "POST", "OPTIONS")
                    .allowedHeaders("*");
        }
    };
}

---

## Pantallas

### Pantalla de entrada

- Campo **nombre de usuario** (máx. 30 caracteres, solo letras, números, espacios, guiones y caracteres acentuados).
- Campo **ID de sala** (máx. 30 caracteres, solo letras, números y guiones — sin espacios).
- Validación en cliente antes de conectar.

### Pantalla de chat

| Elemento | Descripción |
|---|---|
| Barra superior | Sala activa, nombre del usuario y badge de estado de conexión |
| Área de mensajes | Burbujas propias (azul, derecha) y ajenas (blanca, izquierda) |
| Mensajes de sistema | Notificaciones de JOIN / LEAVE centradas en gris |
| Indicador de escritura | Texto animado con puntos rebotantes encima del input |
| Sidebar de usuarios | Lista de miembros en línea con avatares de color determinista |
| Input de mensaje | Envío con `Enter` o botón, contador de caracteres (máx. 500) |
| Botón "Salir" | Publica LEAVE, desconecta y regresa al inicio |

---

## Seguridad

- Todo el contenido de mensajes y nombres se escapa con la función `esc()` antes de insertarse en el DOM, previniendo inyección XSS.
- Las entradas en la pantalla de login se validan con expresiones regulares antes de enviar.
- El evento `beforeunload` notifica al servidor cuando el usuario cierra la pestaña.

---

## Reconexión automática

El cliente STOMP está configurado con `reconnectDelay: 5000` ms. Si la conexión se pierde, intentará reconectarse automáticamente cada 5 segundos y actualizará el badge de estado en la UI.
