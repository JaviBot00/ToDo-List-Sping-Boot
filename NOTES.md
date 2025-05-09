# 📝 NOTES – Guía rápida “para tontos” de Spring Boot y tu Proyecto

---

## 🚧 1. Capas y responsabilidades

### 📦 Repositorio vs. Servicio

## 🎯 ¿Qué es un **`@Repository`** ?
Un repositorio es responsable de acceder a los datos: base de datos, memoria, ficheros, etc.

* Es la capa de acceso a datos (DAO).
* Se encarga de CRUD directamente sobre la fuente de datos.
* Si usas Spring Data, puedes extender JpaRepository, CrudRepository, etc.
* «Cajero de datos»: solo sabe hablar con la base de datos (o memoria).
* Extiende `JpaRepository` (o `CrudRepository`) y ofrece métodos `save()`, `findAll()`, etc.
* Métodos básicos: `save()`, `findAll()`, `findById()`, etc.

## 🧠 ¿Qué es un @Service?
Un servicio encapsula la lógica de negocio de tu aplicación.

* Orquesta las operaciones del repositorio.
* Puede contener validaciones, reglas, cálculos, etc.
* Llama a uno o más repositorios (o incluso servicios externos).
* «Jefe de la tienda»: orquesta, valida y decide qué hacer con los datos.
* Llama a uno o varios repositorios, aplica reglas de negocio y devuelve resultados limpios.


## 📊 Diferencias clave

| Concepto        | @Repository                       | @Service                               |
|-----------------|-----------------------------------|----------------------------------------|
| Qué hace        | Accede directamente a los datos   | Contiene la lógica del negocio         |
| Quién lo usa	   | Lo usa el Service                 | Lo usa el Controller                   |
| Spring Data     | Sí, puedes extender JpaRepository | No                                     |
| Responsable de	 | CRUD puro                         | Reglas de negocio, validaciones, flujo |


---

## 🌐 2. Controladores y rutas

- **`@RestController` + `@RequestMapping("/base")`**
  - Clase que recibe peticiones HTTP y las redirige a métodos.
  - Clase que expone rutas REST.


- **Verbos HTTP**
  - Métodos concretos para cada verbo (GET, POST…).
  - `@GetMapping` → leer datos
  - `@PostMapping` → crear
  - `@PutMapping` → reemplazar/editar
  - `@PatchMapping` → modificar campos concretos
  - `@DeleteMapping` → borrar


- **Parámetros**
  - `@PathVariable` → valores dentro de la URL (`/tareas/{id}`).
  - `@RequestParam` → valores tras `?` en la URL (`?completada=true`).
  - `@RequestBody` → el JSON que envía en el cuerpo de la petición.

---

## 🔒 3. Seguridad, JWT y roles

1. **`JwtFilter`** (hereda de `OncePerRequestFilter`):
   - Se ejecuta **antes** de los controladores.
   - Omite `/api/auth/**`.
   - Lee el header `Authorization: Bearer <token>`.
   - Llama a `JwtUtil` para validar firma y fecha de caducidad.
   - Carga el usuario real (`UserDetails`) y lo pone en el contexto de Spring (`SecurityContextHolder`).

2. **`JwtUtil`**:
   - Usa una **clave secreta** (mínimo 32 caracteres) y algoritmo HS256.
   - `generarToken(usuario)`: firma un token con `subject=username`.
   - `validarToken(token, username)`: comprueba firma + expiración.

3. **Anotaciones de método**
   - Permiten decir “solo ADMIN puede esto” directamente en el método.
   - `@EnableMethodSecurity` habilita `@PreAuthorize`.
   - `@PreAuthorize("hasRole('ADMIN')")` restringe solo a ADMIN.

4. **Perfiles de Spring** (`dev` vs `prod`):
   - **`dev`** usa H2 en memoria (`ddl-auto=create-drop`), consola en `/h2-console`.
   - **`prod`** usa MySQL real (`ddl-auto=update`), perfil activo vía `SPRING_PROFILES_ACTIVE=prod`.

### ✅ ¿Qué se gana con esto?
Diferencia en los entornos de desarrollo y producción

| Entorno | Base de Datos | Uso Principal       | Persistencia | Consola Web |
|---------|---------------|---------------------|--------------|-------------|
| dev     | H2 (memoria)  | Desarrollo local    | ❌            | ✅           |
| prod    | MySQL         | Producción y Docker | ✅            | ❌           |

Esta separación permite desarrollar de forma ágil y desplegar con robustez y persistencia de datos reales.

---

## 🗄️ 4. JPA & Entidades

Marcan clases y campos para que se conviertan en tablas y columnas.
- **`@Entity`**, **`@Id`**, **`@GeneratedValue`**

Cuando tienes relaciones bidireccionales en JPA (por ejemplo, un Usuario tiene una lista de Tareas y cada Tarea tiene un Usuario), Jackson, la librería que convierte tus objetos en JSON, puede entrar en un bucle infinito al serializar.
Ahí entran estas dos anotaciones:

### **`@JsonManagedReference`**
- Se pone en el lado "propietario" o padre de la relación (usualmente el @OneToMany).
- Este lado SÍ se serializa.

```java
@OneToMany(mappedBy = "usuario")
@JsonManagedReference
private List<Tarea> tareas;
```
Esto significa: "Incluye estas tareas al serializar un usuario".

### **`@JsonBackReference`**
- Se pone en el lado inverso de la relación (usualmente el @ManyToOne).
- Este lado NO se serializa.

```java
@ManyToOne
@JoinColumn(name = "usuario_id")
@JsonBackReference
private Usuario usuario;
```
Esto significa: "No serialices el usuario cuando serialices la tarea", para evitar el bucle.

> Si no necesitas serializar la relación, usa `@JsonIgnore` en el campo que no quieres en el JSON.

### `spring.jpa.hibernate.ddl-auto`

Esta es una propiedad muy importante que controla cómo Hibernate manejará el esquema de la base de datos. Hay dos valores mostrados:

**1. `update`**:
- Con este valor, Hibernate:
    - Mantiene los datos existentes en la base de datos
    - Actualiza el esquema si hay cambios en las entidades
    - Agrega nuevas tablas/columnas según sea necesario
    - **No elimina** datos o tablas existentes
- Útil para:
    - Entornos de desarrollo
    - Cuando necesitas preservar datos entre reinicios de la aplicación

**2. `create-drop`**:
- Con este valor, Hibernate:
    - **Elimina** todo el esquema al iniciar la aplicación
    - Crea un esquema nuevo desde cero
    - Al detener la aplicación, elimina todo el esquema
- Útil para:
    - Pruebas unitarias
    - Cuando necesitas empezar con una base de datos limpia en cada inicio

### Recomendaciones de uso:
- `update`: Para desarrollo local
- `create-drop`: Para pruebas
- `validate`: Para producción (solo verifica que el esquema coincida)
- `none`: Para producción cuando la gestión del esquema se hace manualmente

⚠️ **Nota importante**: Nunca uses `create-drop` en producción, ya que perderás todos los datos cada vez que la aplicación se reinicie.

---

## 📦 5. DTOs & Mappers

- **¿Para qué?**
  - No exponer tu entidad JPA con datos sensibles (password, colecciones).
  - Evitar errores de `LazyInitializationException`.
  - Controlar exactamente qué llega y qué sale del backend.

- **Patrón**:
  1. Definir DTO (p.ej. `UsuarioRequest`, `TareaRequest`).
  2. Métodos `toDto(Usuario)` y `toEntity(UsuarioRequest)`.
  3. Inyectar `UsuarioMapper` en controladores/servicios con `@Autowired` o constructor.

---

## 📝 6. Validaciones con Hibernate Validator

- **`@NotBlank`** → campo **no vacío** ni solo espacios.
- **`@Size(min=…, max=…)`** → longitud del texto entre límites.
- **`@Valid @RequestBody`** en el controlador para que Spring valide automáticamente.

---

## 📂 7. Ignorar archivos

### `.gitignore`
- **Build**: `/target/`, `.mvn/`
- **IDE**: `.idea/`, `.vscode/`, `.settings/`
- **Logs/temp**: `*.log`, `*.tmp`, `.DS_Store`
- **Env**: `.env`, `token.txt`

### `.dockerignore`
- **Build**: `/target/`
- **Git**: `.git`, `.gitignore`
- **IDE**: `.idea/`, `.vscode/`
- **Env**: `.env*`, `token.txt`
- **Others**: `README.md`, `logs`, `temp`
- **Docker overrides**: `docker-compose.override.yml`

### gitignore VS dockerignore

**Diferencias principales**:
- **.gitignore**
  - Ignora archivos para el control de versiones Git
  - Afecta solo a operaciones de Git

- **.dockerignore**
  - Ignora archivos durante la construcción de imágenes Docker
  - Optimiza el tamaño de la imagen y velocidad de construcción
  - Solo afecta al comando `docker build`

---

## 🐳 8. Docker & Despliegue

- **`Dockerfile`**:
```dockerfile
FROM eclipse-temurin:17-jdk-alpine
COPY target/tareas.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```