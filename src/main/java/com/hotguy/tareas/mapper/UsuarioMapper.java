package com.hotguy.tareas.mapper;

import com.hotguy.tareas.dto.UsuarioRequest;
import com.hotguy.tareas.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioRequest toDto(Usuario usuario) {
        UsuarioRequest dto = new UsuarioRequest();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setRol(usuario.getRol());
        // No ponemos el password por seguridad
        return dto;
    }

    public Usuario toEntity(UsuarioRequest dto) {
        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(dto.getPassword()); // Hashea esto después si hace falta
        usuario.setRol(dto.getRol());
        return usuario;
    }
}
