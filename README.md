# **📝 ToDo List con Spring Boot**

> API REST de gestión de tareas con autenticación JWT, roles de usuario, validaciones y despliegue en Docker.

---

## 📋 Índice

1. [Descripción del proyecto](#-descripción-del-proyecto)
2. [Estructura del proyecto](#-estructura-del-proyecto)
3. [Tecnologías y dependencias](#-tecnologías-y-dependencias)
4. [Explicación de paquetes y clases](#-explicación-de-paquetes-y-clases)
5. [Anotaciones clave](#-anotaciones-clave-utilizadas)
6. [Endpoints de la API](#-endpoints-de-la-api)
7. [Autenticación y autorización (JWT)](#-autenticación-y-autorización-jwt)
8. [Registro y roles de usuario](#-registro-y-roles-de-usuario)
9. [Gestión de tareas](#-gestión-de-tareas)
10. [Perfiles de configuración: dev y prod](#-perfiles-de-configuración-dev-y-prod)
11. [Despliegue con Docker](#-despliegue-con-docker)
12. [Uso de Docker Compose](#-uso-de-docker-compose)
13. [.gitignore](#-gitignore)
14. [Cómo ejecutar](#-cómo-ejecutar)
15. [Notas finales](#-notas-finales)

---

## 🎯 Descripción del proyecto

Pequeña aplicación "ToDo List" desarrollada con Spring Boot que permite:

* Registro y login de usuarios con JWT.
* Roles (`USER`, `ADMIN`) para proteger operaciones.
* CRUD de tareas asociadas al usuario autenticado.
* Validaciones y DTOs para modelado de datos.
* Despliegue local mediante Docker y Docker Compose.

---

## 📁 Estructura del proyecto

```
com.hotguy.tareas
│
├── config
│   ├── AuthManagerConfig.java       → Bean del AuthenticationManager
│   └── SeguridadConfig.java         → Reglas de seguridad y configuración JWT
│
├── controller
│   ├── AdminController.java         → Endpoints protegidos para gestión de usuarios (ADMIN)
│   ├── ApiPaths.java                → Rutas organizadas por controladores y funciones
│   ├── AuthController.java          → Registro y login, devuelve token JWT
│   ├── TareaController.java         → Endpoints CRUD para tareas del usuario autenticado
│   └── UsuarioController.java       → Perfil de usuario autenticado
│
├── dto
│   ├── AuthRequest.java             → Objeto para login (username y password)
│   ├── TareaRequest.java            → DTO para enviar datos de tareas (crear/editar)
│   └── UsuarioRequest.java          → DTO de usuarios (registro, respuesta, cambios)
│
├── mapper
│   └── UsuarioMapper.java           → Conversor entre Usuario y UsuarioRequest (DTO)
│
├── model
│   ├── Tarea.java                   → Entidad JPA para tareas, validaciones y relación con usuario
│   └── Usuario.java                 → Entidad JPA para usuarios con relación bidireccional a tareas
│
├── repository
│   ├── TareaRepository.java         → Interfaz CRUD para tareas
│   └── UsuarioRepository.java       → Interfaz CRUD para usuarios, incluye búsqueda por username
│
├── security
│   ├── JwtFilter.java               → Filtro que valida el JWT en cada request
│   ├── JwtProperties.java           → Propiedades del JWT desde application.properties
│   └── JwtUtil.java                 → Generación, validación y parsing del token JWT
│
├── service
│   ├── TareaService.java            → Lógica de negocio para crear, listar, editar y borrar tareas
│   └── UsuarioService.java          → Registro de usuarios, login, cambios de rol, perfil
│
└── TareasApplication.java          → Clase principal con `@SpringBootApplication`
```

---

## 📌 Tecnologías y dependencias

* Java 17+ / 21+
* Spring Boot 3.x
* Spring Security
* Spring Data JPA (Hibernate)
* MySQL Connector
* JJWT (io.jsonwebtoken 0.11.5)
* Docker & Docker Compose

---

## 🔍 Explicación de paquetes y clases

### config

* **AuthManagerConfig.java**: expone `AuthenticationManager` como bean.
* **SeguridadConfig.java**: define la cadena de filtros y reglas de acceso (`requestMatchers`, roles).

### controller

* **ApiPaths**: constantes de rutas base para centralizar URIs.
* **AuthController**: endpoints `/api/auth/login` y `/api/auth/registro`.
* **UsuarioController**: `/api/usuarios` (perfil) y `/api/admin/usuarios`, `/api/admin/{id}/rol`.
* **TareaController**: CRUD de tareas y toggle de estado.
* **AdminController**: operaciones administrativas (si aplica).

### dto

* **AuthRequest**: recibe `{ username, password }` para login.
* **TareaRequest**: recibe `{ titulo, descripcion }` para crear/editar.
* **UsuarioRequest**: DTO usuario sin exponer password.

### mapper

* **UsuarioMapper**: convierte entre `Usuario` ↔ `UsuarioRequest`.

### model

* **Usuario**: entidad JPA con campos `id, username, password, rol`, relación `@OneToMany` con tareas.
* **Tarea**: entidad JPA con `id, titulo, descripcion, completada, fechaCreacion, fechaActualizacion`, `@ManyToOne Usuario`.

### repository

* **UsuarioRepository**: extiende `JpaRepository<Usuario, Long>`, método `findByUsername`.
* **TareaRepository**: extiende `JpaRepository<Tarea, Long>`, métodos `findByUsuario`, `findByUsuarioAndCompletada`.

### security

* **JwtProperties**: carga `jwt.secret` y `jwt.expiration` de `application.properties`.
* **JwtUtil**: genera y valida JWT usando `Keys.hmacShaKeyFor`, `parserBuilder()`.
* **JwtFilter**: filtra rutas, ignora `/api/auth/**`, maneja expiración e invalidación.

### service

* **UsuarioService**: implementa `UserDetailsService`, gestiona registro, carga usuarios, cambio de rol.
* **TareaService**: lógica de negocio de tareas, creación, listado, toggle, edición, eliminación.

### aplicación principal

* **TareasApplication.java**: punto de entrada con `@SpringBootApplication`.

---

## 📊 Anotaciones clave utilizadas

| Anotación                       | Descripción                                                                     |
|---------------------------------|---------------------------------------------------------------------------------|
| `@Autowired`                    | Inyecta dependencias automáticamente                                            |
| `@Bean`                         | Declara un bean que Spring gestionará                                           |
| `@Component`                    | Marca una clase para que Spring la detecte y gestione como un bean              |
| `@Configuration`                | Marca clases de configuración de beans                                          |
| `@EnableMethodSecurity`         | Habilita la seguridad basada en métodos (ej. `@PreAuthorize`)                   |
| `@EnableWebSecurity`            | Habilita configuración personalizada de seguridad                               |
| `@Entity`                       | Declara una clase como entidad JPA                                              |
| `@Id / @GeneratedValue`         | Marca el identificador de una entidad JPA                                       |
| `@JsonBackReference`            | Complementa a `@JsonManagedReference` para evitar la recursión infinita         |
| `@JsonIgnore`                   | Excluye un atributo de la serialización JSON                                    |
| `@JsonManagedReference`         | Anotación de Jackson para serializar relaciones bidireccionales evitando bucles |
| `@NotBlank / @Size`             | Anotaciones de validación para campos de texto                                  |
| `@OneToMany / @ManyToOne`       | Define relaciones entre entidades                                               |
| `@PathVariable`                 | Extrae valores de la ruta URI                                                   |
| `@PostMapping, @PutMapping...`  | Anotaciones para métodos HTTP específicos                                       |
| `@PreAuthorize`                 | Restringe acceso a métodos según roles                                          |
| `@RequestBody`                  | Indica que el cuerpo de la petición se vincula al parámetro del método          |
| `@RequestMapping / @GetMapping` | Mapea métodos a rutas HTTP                                                      |
| `@RequestParam`                 | Extrae parámetros de la query string (`?param=valor`)                           |
| `@RestController`               | Define una clase como controlador REST                                          |
| `@Service / @Repository`        | Marca clases como servicios o repositorios para ser gestionadas por Spring      |
| `@SpringBootApplication`        | Marca la clase principal de una app Spring Boot                                 |
| `@Valid`                        | Activa validaciones en los DTOs enviados en el request                          |



---

## 🌐 Endpoints de la API

| Ruta                             | Método | Acceso      | Descripción                    |
|----------------------------------|--------|-------------|--------------------------------|
| `/api/auth/login`                | POST   | Pública     | Login → devuelve JWT           |
| `/api/auth/register`             | POST   | Pública     | Registro → devuelve JWT        |
| `/api/usuarios/profile`          | GET    | USER, ADMIN | Perfil propio usuario (DTO)    |
| `/api/tasks/create`              | GET    | USER, ADMIN | Lista tareas (filtro y orden)  |
| `/api/tasks/list`                | POST   | USER, ADMIN | Crea tarea                     |
| `/api/tasks/{id}`                | PUT    | USER, ADMIN | Edita tarea                    |
| `/api/tasks/{id}/toggleComplete` | PATCH  | USER, ADMIN | Toggle completada              |
| `/api/tasks/{id}`                | DELETE | ADMIN       | Elimina tarea                  |
| `/api/admin/users`               | GET    | ADMIN       | Lista todos los usuarios (DTO) |
| `/api/admin/{id}/promote`        | PATCH  | ADMIN       | Promociona a ADMIN             |
| `/api/admin/{id}/rol`            | PATCH  | ADMIN       | Cambiar el rol del usuario     |

---

## 🔐 Autenticación y autorización (JWT)

* Uso de **JSON Web Tokens** para stateless auth.
* Filtro `JwtFilter` que intercepta y valida el `Authorization: Bearer <token>`.
* Excepciones: `ExpiredJwtException` → 401 Token expirado; `JwtException` → 401 Token inválido.
* Seguridad: solo rutas `/api/auth/**` permitidas sin token; resto requiere autenticación y roles.
* Se utiliza `UsernamePasswordAuthenticationToken`, `AuthenticationManager`, y `JwtFilter` para interceptar peticiones y verificar tokens JWT. 
* Usuarios acceden con credenciales y reciben un token, que se debe enviar como `Authorization: Bearer <token>`. 
* Las contraseñas están cifradas con `BCryptPasswordEncoder`. 
* Usuarios ADMIN pueden ver todos los usuarios y cambiar roles.

---

## 💾 Registro y roles de usuario

* **Registro**: rol por defecto `USER`; no expone rol ni password en respuestas.
* **Promoción a ADMIN**: solo con endpoint protegido `/api/admin/{id}/promote` mediante `@PreAuthorize("hasRole('ADMIN')")`.
* **Usuario por defecto**: creado al iniciar con `CommandLineRunner`, username `admin`, rol `ADMIN`.

---

## 🧭 Gestión de tareas

* Relación `Usuario` → `Tarea` bidireccional.
* Se almacenan `fechaCreacion` y `fechaActualizacion` con `@CreationTimestamp` y `@UpdateTimestamp`.
* Filtro opcional `?completada=true|false` y orden múltiple `?orden=fecha_desc,estado_asc`.
* Toggle estado con `PATCH /api/tasks/{id}/toggleComplete`.

---

## 🧩 Perfiles de configuración: dev y prod

Spring Boot permite separar la configuración de distintos entornos mediante perfiles. En este proyecto se han definido dos:

- dev: para desarrollo local con base de datos en memoria (H2).
- prod: para producción, usando MySQL con Docker.

Esto permite una experiencia de desarrollo más ágil sin perder compatibilidad con entornos productivos.

### 🟢 Perfil de desarrollo (dev)

Se usa una base de datos H2 en memoria y se habilita la consola web para visualizar datos fácilmente.

* src/main/resources/application-dev.properties

```properties
spring.datasource.url=jdbc:h2:mem:devdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

- Se borra y recrea la base de datos cada vez.
- Acceso visual a la base de datos desde http://localhost:8080/h2-console.

---

### 🔴 Perfil de producción (prod)

Se configura una base de datos MySQL persistente, ideal para desplegar con Docker.

* src/main/resources/application-prod.properties

```properties
spring.datasource.url=jdbc:mysql://mysql:3306/tareas_db
spring.datasource.username=app_user
spring.datasource.password=app_pass
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.sql.init.mode=always
```

- Se conecta al contenedor mysql definido en Docker.
- Usa credenciales seguras y persistencia real de datos.
- ddl-auto=update actualiza el esquema sin borrar datos.

---

### ⚙️ Activar perfil

- En desarrollo:

   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

- En producción con Docker:

El perfil prod se activa desde el docker-compose.yml:

```yaml
environment:
- SPRING_PROFILES_ACTIVE=prod
```

## 🐳 Despliegue con Docker

**Dockerfile** en raíz:

```Dockerfile
FROM eclipse-temurin:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

**.env** (opcional):

```properties
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=tareas
MYSQL_USER=user
MYSQL_PASSWORD=pass
```

**.dockerignore** (opcional):

```gitignore
target/
.git
.idea
*.iml
*.log
```

---

## 🐳 Uso de Docker Compose

```yaml
version: '3.8'
services:
  db:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${MYSQL_DATABASE}
      MYSQL_USER: ${MYSQL_USER}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
    ports:
      - "3306:3306"
    volumes:
      - db_data:/var/lib/mysql
    restart: always

  app:
    build: .
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/${MYSQL_DATABASE}
      SPRING_DATASOURCE_USERNAME: ${MYSQL_USER}
      SPRING_DATASOURCE_PASSWORD: ${MYSQL_PASSWORD}
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
      SPRING_PROFILES_ACTIVE: prod
    ports:
      - "8080:8080"
    depends_on:
      - db
    restart: always

volumes:
  db_data:
```

---

## 📌 .gitignore

```gitignore
/target/
.mvn/
.vscode/
.idea/
*.iml
*.log
.DS_Store
.env

# Docker
docker-compose.override.yml
```
---

## ✅ ¿Qué se gana con esto?

| Entorno | Base de Datos | Uso Principal       | Persistencia | Consola Web |
|---------|---------------|---------------------|--------------|-------------|
| dev     | H2 (memoria)  | Desarrollo local    | ❌            | ✅           |
| prod    | MySQL         | Producción y Docker | ✅            | ❌           |

Esta separación permite desarrollar de forma ágil y desplegar con robustez y persistencia de datos reales.

---

## ▶️ Cómo ejecutar

1. Configura variables en `.env` (si usas).
2. Compila el proyecto:

   ```bash
   ./mvnw clean package
   ```
3. Ejecutar directamente:

   ```bash
   java -jar target/tareas.jar
   ```
4. O bien, construir imagen y levantar con Docker Compose:

   ```bash
   docker-compose up --build
   ```
5. Accede a `http://localhost:8080` y prueba la API.

---

## 💡 Notas finales

* Este proyecto fue desarrollado como backend de práctica para gestión de tareas.
* Se enfoca en autenticación robusta, buenas prácticas con DTOs, y separación clara de responsabilidades.