package com.hotguy.tareas.controller;

public class ApiPaths {

    public static final String BASE = "/api";

    public static final String AUTH = BASE + "/auth";
    public static final String TAREAS = BASE + "/tasks";
    public static final String USUARIO = BASE + "/user";
    public static final String ADMIN = BASE + "/admin";

    public static class AuthPaths {
        public static final String LOGIN = "/login";
        public static final String REGISTER = "/register";
        public static final String HELLO = "/hello";
    }

    public static class TareaPaths {
        public static final String CREAR = "/create";
        public static final String LISTAR = "/list";
        public static final String COMPLETAR = "/{id}/toggleComplete";
        public static final String EDITAR = "/{id}";
        public static final String ELIMINAR = "/{id}";
    }

    public static class AdminPaths {
        public static final String LISTAR_USUARIOS = "/users";
        public static final String PROMOCIONAR = "/{id}/promote";
        public static final String CAMBIAR_ROL = "/{id}/rol";
    }

    public static class UserPaths {
        public static final String PERFIL = "/profile";
    }
}

