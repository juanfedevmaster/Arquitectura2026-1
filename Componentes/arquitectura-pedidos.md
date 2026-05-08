# Arquitectura Orientada a Componentes — Sistema de Gestión de Pedidos en Línea

---

## C1 — Diagrama de Contexto del Sistema

Muestra el sistema en su entorno: quiénes lo usan y con qué sistemas externos se integra.

```mermaid
C4Context
    title C1 - Contexto del Sistema: Gestión de Pedidos en Línea

    Person(cliente, "Cliente", "Usuario que navega, compra y realiza seguimiento de pedidos.")
    Person(admin, "Administrador", "Gestiona productos, inventario y reportes de ventas.")

    System(sistema, "Sistema de Gestión de Pedidos", "Plataforma web que permite comprar productos, gestionar pedidos y administrar el catálogo.")

    System_Ext(pasarela, "Pasarela de Pago", "Servicio externo (Stripe / PayPal) para procesar transacciones.")
    System_Ext(correo, "Servicio de Correo", "Servicio externo (SendGrid / SMTP) para enviar notificaciones.")

    Rel(cliente, sistema, "Busca productos, agrega al carrito, realiza pedidos", "HTTPS")
    Rel(admin, sistema, "Gestiona catálogo, inventario y reportes", "HTTPS")
    Rel(sistema, pasarela, "Procesa pagos", "HTTPS/API REST")
    Rel(sistema, correo, "Envía confirmaciones y alertas", "HTTPS/API REST")
```

---

## C2 — Diagrama de Contenedores

Muestra las aplicaciones y bases de datos que componen el sistema y cómo se comunican.

```mermaid
C4Container
    title C2 - Contenedores: Sistema de Gestión de Pedidos en Línea

    Person(cliente, "Cliente")
    Person(admin, "Administrador")

    System_Ext(pasarela, "Pasarela de Pago")
    System_Ext(correo, "Servicio de Correo")

    System_Boundary(sistema, "Sistema de Gestión de Pedidos") {

        Container(spa, "Aplicación Web (SPA)", "React / Angular", "Interfaz de usuario para clientes y administradores.")

        Container(api, "API Backend", "Node.js / Spring Boot", "Orquesta la lógica de negocio y expone endpoints REST.")

        ContainerDb(db_productos, "BD Productos", "PostgreSQL", "Almacena catálogo, precios e imágenes.")
        ContainerDb(db_pedidos, "BD Pedidos", "PostgreSQL", "Almacena órdenes, estados y detalle de compras.")
        ContainerDb(db_usuarios, "BD Usuarios", "PostgreSQL", "Almacena cuentas, sesiones y perfiles.")

        Container(cola, "Cola de Eventos", "RabbitMQ / Kafka", "Desacopla eventos entre componentes (ej. pedido confirmado).")
    }

    Rel(cliente, spa, "Usa", "HTTPS")
    Rel(admin, spa, "Usa", "HTTPS")
    Rel(spa, api, "Consume endpoints", "HTTPS / JSON")
    Rel(api, db_productos, "Lee / Escribe", "SQL")
    Rel(api, db_pedidos, "Lee / Escribe", "SQL")
    Rel(api, db_usuarios, "Lee / Escribe", "SQL")
    Rel(api, pasarela, "Procesa pago", "HTTPS")
    Rel(api, cola, "Publica eventos", "AMQP")
    Rel(cola, correo, "Dispara notificación", "HTTPS")
```

---

## C3 — Diagrama de Componentes (API Backend)

Muestra los componentes internos del contenedor **API Backend** y sus responsabilidades.

```mermaid
C4Component
    title C3 - Componentes: API Backend

    System_Ext(spa, "Aplicación Web (SPA)")
    System_Ext(pasarela, "Pasarela de Pago")
    ContainerDb(db_productos, "BD Productos")
    ContainerDb(db_pedidos, "BD Pedidos")
    ContainerDb(db_usuarios, "BD Usuarios")
    Container(cola, "Cola de Eventos")

    System_Boundary(api, "API Backend") {

        Component(authComp, "AuthComponent", "Módulo / Servicio", "Registro, login y gestión de sesiones con JWT.")

        Component(catalogComp, "ProductCatalogComponent", "Módulo / Servicio", "Listado, búsqueda y detalle de productos.")

        Component(cartComp, "CartComponent", "Módulo / Servicio", "Gestión del carrito: agregar, quitar y calcular totales.")

        Component(orderComp, "OrderComponent", "Módulo / Servicio", "Coordinación del flujo de pedido: crea, valida y confirma órdenes.")

        Component(paymentComp, "PaymentComponent", "Módulo / Servicio", "Abstrae la integración con la pasarela de pago.")

        Component(inventoryComp, "InventoryComponent", "Módulo / Servicio", "Consulta y actualiza el stock de productos.")

        Component(notifComp, "NotificationComponent", "Módulo / Servicio", "Publica eventos de notificación en la cola.")

        Component(reportComp, "ReportComponent", "Módulo / Servicio", "Genera reportes de ventas y métricas para administradores.")
    }

    Rel(spa, authComp, "Login / Registro", "REST")
    Rel(spa, catalogComp, "Consulta productos", "REST")
    Rel(spa, cartComp, "Gestiona carrito", "REST")
    Rel(spa, orderComp, "Crea pedido", "REST")
    Rel(spa, reportComp, "Solicita reportes", "REST")

    Rel(orderComp, paymentComp, "Solicita cobro", "Interfaz interna")
    Rel(orderComp, inventoryComp, "Verifica y descuenta stock", "Interfaz interna")
    Rel(orderComp, notifComp, "Notifica confirmación", "Evento interno")

    Rel(paymentComp, pasarela, "Procesa transacción", "HTTPS")
    Rel(notifComp, cola, "Publica evento", "AMQP")
    Rel(catalogComp, db_productos, "Lee productos", "SQL")
    Rel(inventoryComp, db_productos, "Actualiza stock", "SQL")
    Rel(orderComp, db_pedidos, "Guarda pedido", "SQL")
    Rel(authComp, db_usuarios, "Lee / Escribe usuario", "SQL")
```

---

## Resumen de Componentes

| Componente | Responsabilidad | Dependencias |
|---|---|---|
| `AuthComponent` | Autenticación y sesiones (JWT) | BD Usuarios |
| `ProductCatalogComponent` | Catálogo y búsqueda | BD Productos |
| `CartComponent` | Carrito de compras | ProductCatalogComponent |
| `OrderComponent` | Flujo y coordinación del pedido | PaymentComponent, InventoryComponent, NotificationComponent |
| `PaymentComponent` | Integración con pasarela externa | Pasarela de Pago |
| `InventoryComponent` | Control de stock | BD Productos |
| `NotificationComponent` | Publicación de eventos de alerta | Cola de Eventos |
| `ReportComponent` | Reportes y métricas | BD Pedidos, BD Productos |
