# Patrones de Diseño

## Enunciado de Patrones de Diseño
Desarrollo de un microservicio de procesamiento de pagos dinámicos con soporte para múltiples pasarelas (Tarjeta, PayPal, Cripto), sistema de descuentos por capas y notificaciones automáticas, basado en patrones de diseño de la Industria.

### Patrones de Diseño a implementar
Identificación de patrones potenciales aplicar en esta WebApi

* Strategy: Para realizar el pago con diferentes tipos de tarjetas.
* Factory Method: La creación de la instanacia a ejectura para el Strategy
* Observer: Se debe implementar para recibir la notificación de que el pago se realizo y luego enviar las notificaciones necesarias.
* Decorator: Añade descuentos dinamicos sin modificar la estrategia de pagos.
* State: Patron basado en estados para el procesamiento de los pagos.

### Patrones de diseño que vienen con Springboot:
Springboot trae implementados algunos patrones por defecto, estos no los modificaremos para nuestro ejercicio

* Singleton: Gestionar las instancias que requerimos inyectar en la aplicación.
* DTO: Aíslar la capa HTTP del modelo de Dominio.

---

## Patrones aplicados

| Patrón | Dónde | Propósito |
|--------|-------|-----------|
| **Strategy** | `strategy/` | Encapsula cada método de pago (Card, PayPal, Crypto) como algoritmo intercambiable |
| **Factory Method** | `PaymentStrategyFactory` | Resuelve y crea la estrategia correcta según el método indicado |
| **Singleton** | Todos los `@Component` / `@Service` | Spring gestiona una única instancia por bean |
| **Decorator** | `decorator/` | Añade descuentos dinámicos sin modificar la estrategia base |
| **Observer** | `observer/` | Notifica por Email, SMS y Auditoría tras procesar el pago |
| **DTO** | `dto/` | Aísla la capa HTTP del modelo de dominio |

---

## Diagrama de Componentes (C4 — Nivel 3)
```mermaid
flowchart TD
    Cliente(["👤 Cliente\nHTTP / JSON"])

    subgraph DTO["Patrón DTO"]
        ReqDTO["PaymentRequestDTO\n«Bean Validation»"]
        ResDTO["PaymentResponseDTO"]
    end

    subgraph Controllers["REST Layer"]
        CTRL["PaymentController\nPOST /api/payments\nGET  /api/payments/methods\nGET  /api/payments/discounts"]
    end

    subgraph Services["Services · Singleton implícito"]
        SVC["PaymentService\n«orquestador»"]
        DISC["DiscountService\ncódigos de descuento"]
    end

    subgraph FACTORY["Patrón Factory Method"]
        FAC["PaymentStrategyFactory\nresuelve estrategia por método"]
    end

    subgraph DECORATOR["Patrón Decorator"]
        BASE["BasePaymentProcessor\n«componente base»"]
        DEC["DiscountDecorator\n«wrapper» aplica %descuento"]
    end

    subgraph STRATEGY["Patrón Strategy"]
        IFACE["&lt;&lt;interface&gt;&gt;\nPaymentStrategy"]
        CARD["CardPaymentStrategy"]
        PP["PayPalPaymentStrategy"]
        CRYPTO["CryptoPaymentStrategy"]
    end

    subgraph OBSERVER["Patrón Observer"]
        PUB["PaymentEventPublisher\n«notifica a todos»"]
        OBS["&lt;&lt;interface&gt;&gt;\nPaymentObserver"]
        EMAIL["EmailNotificationObserver"]
        SMS["SmsNotificationObserver"]
        AUDIT["AuditLogObserver"]
    end

    subgraph DOMAIN["Dominio"]
        PAY["Payment"]
        RES["PaymentResult"]
    end

    %% Flujo HTTP
    Cliente -->|"POST /api/payments"| CTRL
    CTRL -->|"deserializa"| ReqDTO
    ReqDTO -->|"processPayment()"| SVC
    SVC -->|"construye"| ResDTO
    ResDTO -->|"HTTP 201"| Cliente

    %% Servicio → dependencias
    SVC -->|"getDiscount(code)"| DISC
    SVC -->|"getStrategy(method)"| FAC
    SVC -->|"crea"| PAY

    %% Factory → Strategy
    FAC -->|"resuelve"| CARD
    FAC -->|"resuelve"| PP
    FAC -->|"resuelve"| CRYPTO
    CARD & PP & CRYPTO -->|"implements"| IFACE

    %% Decorator chain
    SVC -->|"new"| BASE
    SVC -->|"new wraps base"| DEC
    DEC -->|"delega a"| BASE
    BASE -->|"process(payment)"| IFACE
    IFACE -->|"retorna"| RES

    %% Observer
    SVC -->|"publish(payment, result)"| PUB
    PUB -->|"onPaymentProcessed()"| EMAIL
    PUB -->|"onPaymentProcessed()"| SMS
    PUB -->|"onPaymentProcessed()"| AUDIT
    EMAIL & SMS & AUDIT -->|"implements"| OBS

    %% Estilos
    classDef patron  fill:#4A90D9,stroke:#2C5F8A,color:#fff,font-weight:bold
    classDef service fill:#27AE60,stroke:#1A7A42,color:#fff
    classDef dto     fill:#8E44AD,stroke:#5E2D7A,color:#fff
    classDef domain  fill:#E67E22,stroke:#A04000,color:#fff
    classDef iface   fill:#95A5A6,stroke:#717D7E,color:#fff,font-style:italic
    classDef person  fill:#2C3E50,stroke:#1A252F,color:#fff

    class CARD,PP,CRYPTO,FAC patron
    class BASE,DEC patron
    class EMAIL,SMS,AUDIT,PUB patron
    class SVC,DISC service
    class ReqDTO,ResDTO dto
    class PAY,RES domain
    class IFACE,OBS iface
    class Cliente person
    class CTRL service
```

---

## Flujo principal

```
Cliente
  │  POST /api/payments  { amount, method, currency, discountCode }
  ▼
PaymentController
  │  PaymentRequestDTO  (Bean Validation)
  ▼
PaymentService
  ├─► DiscountService.getDiscount(code)
  ├─► [Factory]   PaymentStrategyFactory  →  Card | PayPal | Crypto
  ├─► [Decorator] BasePaymentProcessor
  │               └── DiscountDecorator (si aplica código)
  ├─► [Observer]  PaymentEventPublisher
  │               ├── EmailNotificationObserver
  │               ├── SmsNotificationObserver
  │               └── AuditLogObserver
  └─► PaymentResponseDTO
  ▼
Cliente  { paymentId, originalAmount, finalAmount, discountPercentage, status, txId }
```

---

## Endpoints

| Método | URL | Descripción |
|--------|-----|-------------|
| `POST` | `/api/payments` | Crear y procesar un pago |
| `GET` | `/api/payments/methods` | Métodos de pago soportados |
| `GET` | `/api/payments/discounts` | Códigos de descuento disponibles |

---