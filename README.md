# RESUMEN GENERAL DEL PROYECTO

## Objetivo:

Crear una API REST de tareas (Tarea) con validaciones, seguridad básica, autenticación vía JWT, y estructura limpia por capas.

<hr>

# FUNCIONALIDADES CUBIERTAS

1. CRUD de tareas: crear, leer, actualizar, eliminar.

2. Validación con anotaciones (@NotBlank, @Size).

3. Autenticación:
   - Inicial: auth básica en memoria.
   - Luego: JWT (JSON Web Token) para login y autorización.

4. Filtros de seguridad para interceptar y validar JWT en cada request.

5. Separación de clases por responsabilidad: controladores, servicios, seguridad, etc.

```
com.ejemplo.tareas
│
├── controller
│   ├── TareaController.java         → Endpoints de la API de tareas
│   ├── HelloController.java         → 
│   └── AuthController.java          → Endpoint de login y generación de JWT
│
├── model
│   └── Tarea.java                   → Entidad JPA con validaciones
│
├── repository
│   └── TareaRepository.java         → Interfaz CRUD que extiende JpaRepository
│
├── dto
│   └── AuthRequest.java             → Objeto para login (username y password)
│
├── config
│   ├── SeguridadConfig.java        → Reglas de seguridad de la API
│   ├── UsuariosConfig.java         → Usuarios en memoria y codificador de contraseñas
│   └── AuthManagerConfig.java      → Bean del AuthenticationManager
│
├── service/
│   └── TareaService.java           → 
│
├── security
│   ├── JwtUtil.java                → Clase utilitaria para generar y validar JWT
│   └── JwtFilter.java              → Filtro que valida JWT en cada request
│
└── TareasApplication.java          → Clase principal `@SpringBootApplication`
```
<hr>

# ANOTACIONES IMPORTANTES UTILIZADAS
```
Anotación                       Descripción
@RestController                 Marca una clase como controlador REST.
@RequestMapping                 Define la ruta base para los endpoints.
@GetMapping, @PostMapping etc	Definen rutas HTTP específicas.
@Entity	                        Marca una clase como entidad JPA (tabla).
@Id, @GeneratedValue	        Define clave primaria y auto-incremento.
@NotBlank, @Size                Validaciones de campos (Bean Validation).
@Valid	                        Activa validaciones en parámetros del controlador.
@Component                      Marca una clase como componente de Spring.
@Configuration	                Marca clases de configuración (para definir beans).
@Bean	                        Define métodos que crean beans gestionados por Spring.
@Autowired (implícito)	        Inyección automática de dependencias.
@RequestBody, @PathVariable     Vinculan datos de la petición a los parámetros del método.
```
<hr>

# ✅ EXPLICACIÓN DE TODAS LAS CLASES

## 🎯 Controladores (controller/)

### TareaController.java
* Define endpoints para manejar tareas (/api/tareas).
* Métodos:
  * GET: obtener todas las tareas.
  * POST: crear nueva tarea.
  * PUT: actualizar.
  * DELETE: eliminar.
* Usa la capa de servicio para lógica de negocio.

### AuthController.java
* Define el endpoint /api/auth/login.
* Recibe username y password → devuelve un JWT si son válidos.
* Usa AuthenticationManager y JwtUtil.

<hr>

## 🧠 Modelo y DTO (model/ y dto/)
### Tarea.java
* Entidad que representa una tarea.
* Tiene anotaciones de validación como @NotBlank.
* Será una tabla si usamos una base de datos.

### AuthRequest.java
* DTO (objeto de transferencia) para recibir las credenciales del usuario al hacer login.

<hr>

## 💾 Repositorio (repository/)
### TareaRepository.java
* Interfaz que extiende JpaRepository.
* Spring implementa automáticamente los métodos para guardar, buscar, eliminar tareas.

<hr>

## 🔐 Seguridad (security/)
### JwtUtil.java
* Clase utilitaria para:
  * Crear tokens JWT.
  * Validarlos.
  * Extraer información como el usuario.

### JwtFilter.java
* Filtro que intercepta cada request.
* Extrae el token del header y, si es válido, autentica al usuario en el contexto de Spring.

<hr>

## ⚙️ Configuraciones (config/)
### SeguridadConfig.java
* Configura reglas de seguridad:
  * Permite acceso sin token solo a /api/auth/**.
  * Protege el resto con JWT.
  * Inserta el filtro JwtFilter.

### UsuariosConfig.java
* Define dos usuarios en memoria (usuario, admin) y sus roles.
* Crea un PasswordEncoder para encriptar las contraseñas.

### AuthManagerConfig.java
* Expone el AuthenticationManager como un bean para que Spring lo pueda inyectar.
* Es requerido por AuthController.

<hr>

# 🧭 Relación entre clases (esquema simplificado)

```
TareasApplication
└── Escanea todo el proyecto e inicia Spring Boot

controller/
├── TareaController  ←  llama a  →  TareaRepository
└── AuthController   ←  usa      →  AuthenticationManager + JwtUtil

model/
└── Tarea            ←  validado por  →  Bean Validation

dto/
└── AuthRequest      ←  usado por     →  AuthController

repository/
└── TareaRepository  ←  llamado por   →  TareaController

security/
├── JwtUtil          ←  usado por     →  AuthController y JwtFilter
└── JwtFilter        ←  validación de →  token JWT en cada request

config/
├── SeguridadConfig  →  define reglas de seguridad y aplica filtros
├── UsuariosConfig   →  usuarios y encoder
└── AuthManagerConfig → define AuthenticationManager
```

<hr>

# ✅ Ya tenemos todo para probar los endpoints:

* POST /api/login → te da un token
* GET /api/hello → acceso protegido
* POST /api/tareas → crear una tarea (requiere token)
* GET /api/tareas → listar tareas (requiere token)
* PUT /api/tareas/{id}/completar → marcar completada
* DELETE /api/tareas/{id} → eliminar

<hr>

# 📋 @Repository VS @Service

## 🎯 ¿Qué es un @Repository?
Un repositorio es responsable de acceder a los datos: base de datos, memoria, ficheros, etc.

* Es la capa de acceso a datos (DAO).
* Se encarga de CRUD directamente sobre la fuente de datos.
* Si usas Spring Data, puedes extender JpaRepository, CrudRepository, etc.

## 🧠 ¿Qué es un @Service?
Un servicio encapsula la lógica de negocio de tu aplicación.

* Orquesta las operaciones del repositorio.
* Puede contener validaciones, reglas, cálculos, etc.
* Llama a uno o más repositorios (o incluso servicios externos).

## 📊 Diferencias clave
```
Concepto          @Repository                           @Service
Qué hace          Accede directamente a los datos       Contiene la lógica del negocio
Quién lo usa	  Lo usa el Service                     Lo usa el Controller
Spring Data       Sí, puedes extender JpaRepository     No
Responsable de	  CRUD puro                             Reglas de negocio, validaciones, flujo
```

<hr>

# 🔍 Diferencia entre @JsonManagedReference y @JsonBackReference
Cuando tienes relaciones bidireccionales en JPA (por ejemplo, un Usuario tiene una lista de Tarea y cada Tarea tiene un Usuario), Jackson, la librería que convierte tus objetos en JSON, puede entrar en un bucle infinito al serializar.
Ahí entran estas dos anotaciones:

## 📌 @JsonManagedReference
* Se pone en el lado "propietario" o padre de la relación (usualmente el @OneToMany).
* Este lado SÍ se serializa.

```java
@OneToMany(mappedBy = "usuario")
@JsonManagedReference
private List<Tarea> tareas;
```
Esto significa: "Incluye estas tareas al serializar un usuario".

# 📌 @JsonBackReference
* Se pone en el lado inverso de la relación (usualmente el @ManyToOne).
* Este lado NO se serializa.

```java
@ManyToOne
@JoinColumn(name = "usuario_id")
@JsonBackReference
private Usuario usuario;
```
Esto significa: "No serialices el usuario cuando serialices la tarea", para evitar el bucle.






./mvnw clean package
docker-compose up --build










