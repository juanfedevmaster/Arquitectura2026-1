# Dependencias Mínimas para un Proyecto Spring Boot

Las siguientes dependencias representan las conexiones mínimas que se deben configurar al iniciar un proyecto Spring Boot con acceso a base de datos PostgreSQL, documentación de API y soporte de testing.

---

## 1. Spring Web (`spring-boot-starter-webmvc`)
Habilita el framework Spring MVC para construir APIs RESTful y aplicaciones web. Incluye un servidor embebido (Tomcat por defecto).

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>
```

---

## 2. Spring Data JPA (`spring-boot-starter-data-jpa`)
Proporciona integración con JPA/Hibernate para el manejo de persistencia y acceso a base de datos mediante ORM.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

---

## 3. PostgreSQL Driver (`postgresql`)
Driver JDBC necesario para establecer la conexión con una base de datos PostgreSQL.

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## 4. Lombok (`lombok`)
Biblioteca que reduce el código boilerplate mediante anotaciones, generando automáticamente getters, setters, constructores, builders, entre otros.

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

---

## 5. Springdoc OpenAPI (`springdoc-openapi-starter-webmvc-ui`)
Genera automáticamente la documentación de la API en formato OpenAPI 3.0 y expone una interfaz visual Swagger UI.

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

---

## 6. Validation (`spring-boot-starter-validation`)
Integra Bean Validation (Jakarta Validation) para validar datos de entrada en DTOs y entidades mediante anotaciones como `@NotNull`, `@Size`, `@Email`, entre otras.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

## 7. Spring Boot DevTools (`spring-boot-devtools`)
Herramienta de desarrollo que habilita el reinicio automático de la aplicación y el live reload ante cambios en el código fuente.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

---

## 8. Spring Boot Test (`spring-boot-starter-test`)
Provee el conjunto de herramientas para pruebas unitarias e de integración, incluyendo JUnit 5, Mockito y Spring Test.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## Resumen

| Dependencia | Scope | Propósito |
|---|---|---|
| `spring-boot-starter-webmvc` | compile | API REST / Spring MVC |
| `spring-boot-starter-data-jpa` | compile | ORM / Persistencia |
| `postgresql` | runtime | Conexión a PostgreSQL |
| `lombok` | compile (optional) | Reducción de boilerplate |
| `springdoc-openapi-starter-webmvc-ui` | compile | Swagger UI / OpenAPI 3.0 |
| `spring-boot-starter-validation` | compile | Validación de datos |
| `spring-boot-devtools` | runtime (optional) | Live reload en desarrollo |
| `spring-boot-starter-test` | test | Pruebas unitarias e integración |
