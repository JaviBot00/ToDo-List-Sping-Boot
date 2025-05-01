import os
import requests
from requests.exceptions import RequestException, Timeout, ConnectionError
import json
import time

BASE_URL = 'http://localhost:8080/api'
# BASE_URL = 'http://192.169.2.69:8080/api'
TOKEN_FILE = "./token.txt"
token = None
usuario_actual = None

# DISTINTOS ENDPOINTS DE LA API
ENDPOINTS = {
    # AUTH
    "login": f"{BASE_URL}/auth/login",
    "register": f"{BASE_URL}/auth/register",
    "hello": f"{BASE_URL}/auth/hello",

    # USER - ADMIN
    "ver_perfil": f"{BASE_URL}/user/profile",
    "listar_usuarios": f"{BASE_URL}/admin/users",
    "promocionar_usuario": f"{BASE_URL}/admin/{{user_id}}/promote",
    "cambiar_rol": f"{BASE_URL}/admin/{{user_id}}/rol",

    # TASKS
    "crear_tarea": f"{BASE_URL}/tasks/create",
    "listar_tareas": f"{BASE_URL}/tasks/list",
    "estado_tarea": f"{BASE_URL}/tasks/{{tarea_id}}/toggleComplete",
    "editar_tarea": f"{BASE_URL}/tasks/{{tarea_id}}",
    "eliminar_tarea": f"{BASE_URL}/tasks/{{tarea_id}}",
}

# --- Wrapper seguro ---
def safe_request_wrapper(original_method):
    def wrapper(url, *args, **kwargs):
        for intento in range(3):
            try:
                return original_method(url, *args, **kwargs)
            except (Timeout, ConnectionError) as e:
                print(f"🔁 Reintentando conexión ({intento + 1}/3) para {url}... {e}")
                time.sleep(1)
            except RequestException as e:
                print(f"⚠️ Error en {original_method.__name__}: {e}")
                break
        print(f"❌ Falló la petición a {url}")
        return None
    return wrapper

# --- Monkey patch de los métodos de requests ---
requests.get = safe_request_wrapper(requests.get)
requests.post = safe_request_wrapper(requests.post)
requests.put = safe_request_wrapper(requests.put)
requests.patch = safe_request_wrapper(requests.patch)
requests.delete = safe_request_wrapper(requests.delete)

# GESTION DEL TOKEN
def guardar_token(t, usuario):
    with open(TOKEN_FILE, "w") as f:
        f.write(f"{usuario}\n{t}")

def cargar_token():
    global token, usuario_actual
    if os.path.exists(TOKEN_FILE):
        with open(TOKEN_FILE, "r") as f:
            lineas = f.readlines()
            if len(lineas) >= 2:
                usuario_actual = lineas[0].strip()
                token = lineas[1].strip()
                print(f"\n🔐 Token cargado (usuario: {usuario_actual})")

def cerrar_sesion():
    global token, usuario_actual
    if os.path.exists(TOKEN_FILE):
        os.remove(TOKEN_FILE)
        print("🔓 Sesión cerrada.")
        # print("Hasta luego! \U0001F44B")
    token = None
    usuario_actual = None


# FUNCIONES AUX
def clear_console():
    if os.name == 'nt':
        os.system('cls')
    else:
        os.system('clear')

def requiere_autenticacion(func):
    def wrapper(*args, **kwargs):
        if not token:
            print("⚠️ Debes estar autenticado para esta operación.")
            return
        return func(*args, **kwargs)
    return wrapper

def headers():
    return {"Authorization": f"Bearer {token}"} if token else {}

def print_response(response):
    # clear_console()
    if response is None:
        print("⚠️ No se recibió respuesta del servidor.")
        return

    match response.status_code:
        case 200 | 201 | 204:  # ✅ Agrupación válida con |
            print("\n✅ Respuesta:")
            content_type = response.headers.get("Content-Type", "")
            if content_type.startswith("application/json"):
                try:
                    print(response.json())
                except Exception as e:
                    print("⚠️ Error al parsear JSON:", e)
                    print("Contenido como texto:\n", response.text)
            else:
                print(response.text)
        case 403:
            print(f"❌ Error: {response.status_code}")
            print("🚫 No tienes permiso para realizar esta operacion (requiere ADMIN).")
            print(response.text)
        case _:
            # print(f"⚠️ Otro estado: {response.status_code}")
            print(f"❌ Error: {response.status_code}")
            print(response.text)

    time.sleep(1)


# PETICIONES DE LA API - AUTH
def login():
    global token, usuario_actual
    username = input("Usuario: ")
    password = input("Contraseña: ")
    data = {"username": username, "password": password}
    response = requests.post(ENDPOINTS["login"], json=data)
    if response.status_code == 200:
        token = response.json().get("token")
        usuario_actual = username
        guardar_token(token, username)
        print("✅ Login exitoso. Token guardado.")
    else:
        print("❌ Error de login:", response.status_code, response.text)
        print("response: ", response)


def register():
    global token, usuario_actual
    username = input("Nuevo usuario: ")
    password = input("Nueva contraseña: ")
    data = {"username": username, "password": password}
    response = requests.post(ENDPOINTS["register"], json=data)
    if response.status_code == 200:
        token = response.json().get("token")
        usuario_actual = username
        guardar_token(token, username)
        print("✅ Registro exitoso. Token guardado.")
    else:
        print("❌ Error al registrar:", response.status_code, response.text)
        print("response: ", response)


# PETICIONES DE LA API - USER
@requiere_autenticacion
def ver_perfil():
    response = requests.get(ENDPOINTS["ver_perfil"], headers=headers())
    print_response(response)

@requiere_autenticacion
def listar_usuarios():
    response = requests.get(ENDPOINTS["listar_usuarios"], headers=headers())
    print_response(response)

@requiere_autenticacion
def promocionar_usuario():
    user_id = input("ID del usuario a promocionar: ")
    response = requests.patch(ENDPOINTS["promocionar_usuario"].format(user_id=user_id), headers=headers())
    print_response(response)

@requiere_autenticacion
def cambiar_rol():
    user_id = input("ID del usuario: ")
    nuevo_rol = input("Nuevo rol (USER o ADMIN): ")
    if nuevo_rol.upper() in ("USER", "ADMIN"):
        data = {"rol": nuevo_rol}
        response = requests.patch(ENDPOINTS["cambiar_rol"].format(user_id=user_id), json=data, headers=headers())
        print_response(response)
    else:
        print("Rol incorrecto, pruebe de nuevo")


# PETICIONES DE LA API - HELLO WORLD
@requiere_autenticacion
def hello():
    response = requests.get(ENDPOINTS["hello"], headers=headers())
    print_response(response)


# PETICIONES DE LA API - TAREAS
@requiere_autenticacion
def crear_tarea():
    titulo = input("Título de la tarea: ")
    descripcion = input("Descripción: ")
    data = { "titulo": titulo, "descripcion": descripcion }
    response = requests.post(ENDPOINTS["crear_tareas"], json=data, headers=headers())
    print_response(response)

@requiere_autenticacion
def listar_tareas():
    print("Opciones de filtrado:")
    completada = input("¿Filtrar por completada? (true/false o dejar en blanco): ").strip()
    orden = input("Ordenar por estado_desc/estado_asc, fecha_desc/fecha_asc o dejar en blanco): ").strip()

    params = {}
    if completada:
        params["completada"] = completada
    if orden:
        params["orden"] = orden
    
    response = requests.get(ENDPOINTS["listar_tareas"], headers=headers(), params=params)
    print_response(response)

@requiere_autenticacion
def estado_tarea():
    tarea_id = input("ID de la tarea a alternar completado: ")
    response = requests.patch(ENDPOINTS["estado_tarea"].format(tarea_id=tarea_id), headers=headers())
    print_response(response)

@requiere_autenticacion
def editar_tarea():
    tarea_id = input("ID de la tarea a editar: ")
    new_titulo = input("Nuevo título: ")
    new_desc = input("Nueva descripción: ")
    new_estado = input("¿Completada? (true/false): ").lower() == "true"
    data = { "titulo": new_titulo, "descripcion": new_desc, "completada": new_estado }
    response = requests.put(ENDPOINTS["editar_tarea"].format(tarea_id=tarea_id), json=data, headers=headers())
    print_response(response)

@requiere_autenticacion
def eliminar_tarea():
    tarea_id = input("ID de la tarea a eliminar: ")
    response = requests.delete(ENDPOINTS["eliminar_tarea"].format(tarea_id=tarea_id), headers=headers())
    print_response(response)


# MENUS PARA USAR LOS DISTINTOS ENDPOINTS DE LA API
def menu_tareas():
    opciones = {
        "1": ("Crear tarea", crear_tarea),
        "2": ("Listar tareas", listar_tareas),
        "3": ("Cambiar estado tarea", estado_tarea),
        "4": ("Editar tarea", editar_tarea),
        "5": ("Eliminar tarea (ADMIN)", eliminar_tarea),
        "0": ("Volver al menú principal", menu)
    }
    while True:
        if usuario_actual:
            print(f"\n👤 Usuario conectado: {usuario_actual.upper()}")
        print("\nGestión de Tareas:")
        for k, (desc, _) in opciones.items():
            print(f"  {k}. {desc}")
        eleccion = input("\nElige una opción: ")
        accion = opciones.get(eleccion)
        if accion:
            if eleccion == "0":
                return
            accion[1]()
        else:
            print("Opción no válida")


def menu_usuarios():
    opciones = {
        "1": ("Ver mi perfil", ver_perfil),
        "2": ("Listar usuarios (ADMIN)", listar_usuarios),
        "3": ("Promocionar usuario a ADMIN (ADMIN)", promocionar_usuario),
        "4": ("Cambiar rol de un usuario (ADMIN)", cambiar_rol),
        "0": ("Volver al menú principal", menu)
    }
    while True:
        if usuario_actual:
            print(f"\n👤 Usuario conectado: {usuario_actual.upper()}")
        print("\nGestión de Usuarios:")
        for k, (desc, _) in opciones.items():
            print(f"  {k}. {desc}")
        eleccion = input("\nElige una opción: ")
        accion = opciones.get(eleccion)
        if accion:
            if eleccion == "0":
                return
            accion[1]()
        else:
            print("Opción no válida")


def menu():
    opciones = {
        "1": ("Iniciar sesión", login),
        "2": ("Registrarse", register),
        "3": ("Ver el Hello World (protegido)", hello),
        "4": ("Gestionar tareas", menu_tareas),
        "5": ("Gestionar usuarios", menu_usuarios),
        "6": ("Cerrar sesión", cerrar_sesion),
        "0": ("Salir", exit)
    }
    
    while True:
        if usuario_actual:
            print(f"\n👤 Usuario conectado: {usuario_actual.upper()}")
        print("--- \nMenú Principal ---")
        for k, (desc, _) in opciones.items():
            print(f"{k}. {desc}")
        eleccion = input("\nElige una opción: ").strip()
        accion = opciones.get(eleccion)
        if accion:
            # print(accion[0])
            accion[1]()
            # print("\n", headers())
        else:
            print("Opción no válida.")


if __name__ == "__main__":
    clear_console()
    cargar_token()
    menu()