# Arquitectura2026-1
Este repositorio esta enfocado a tener todo el código hecho para la asignatura de Arquitectura 2026. Servira como base para la asignatura durante los próximos semestres.

## 1. Primer Ejemplo: San Gabriel Hospital – Authentication & Notifications Architecture

Este proyecto presenta una **arquitectura basada en microservicios y eventos** para gestionar el proceso de **autenticación de usuarios y generación de notificaciones** en el sistema del **Hospital San Gabriel**.

La arquitectura permite desacoplar los componentes del sistema mediante el uso de **eventos y mensajería**, facilitando la escalabilidad, el mantenimiento y la evolución del sistema.

El flujo principal inicia cuando un usuario intenta autenticarse en la plataforma. Si la autenticación es exitosa, se genera un evento que posteriormente es procesado por el sistema de notificaciones, el cual puede enviar mensajes en tiempo real al cliente o correos electrónicos.

## Arquitectura San Gabriel

```mermaid
flowchart LR

UI[San Gabriel Hospital UI]

AUTH["Authentication Component\n(Web API)"]

USERS[(Users DB\nPostgreSQL)]

EVENT[(Event Bus / Messaging)]

NOTIF["Notifications Component\n(Web API)"]

NOTIFDB[(Notifications DB\nPostgreSQL)]

WEBSOCKET["Event Push UI\n(WebSocket)"]

EMAILSDK["AWS SES SDK\n(Simple Email Service)"]

EMAIL[Email]

UI -- "HTTP POST\nLogin(email, password)" --> AUTH

AUTH --> USERS

AUTH -- "Event\n(email,statusLogin)" --> EVENT

EVENT -- "Topic: Notifications" --> NOTIF

NOTIF --> NOTIFDB

NOTIF --> WEBSOCKET

WEBSOCKET -- "WebSocket :8796" --> UI

NOTIF --> EMAILSDK

EMAILSDK --> EMAIL
```
