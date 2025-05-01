package com.hotguy.tareas.dto;

import com.hotguy.tareas.model.Usuario;

public class UsuarioRequest {
    private Long id;
    private String username;
    private String password;
    private String rol;

    // Constructor por defecto requerido por Jackson
    public UsuarioRequest() {
    }

    public UsuarioRequest(Usuario u) {
        this.id = u.getId();
        this.username = u.getUsername();
        this.rol = u.getRol();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}