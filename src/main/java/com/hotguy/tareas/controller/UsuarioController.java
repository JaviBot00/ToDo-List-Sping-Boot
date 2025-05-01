package com.hotguy.tareas.controller;

import com.hotguy.tareas.dto.UsuarioRequest;
import com.hotguy.tareas.mapper.UsuarioMapper;
import com.hotguy.tareas.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Marca esta clase como controlador REST
@RequestMapping(ApiPaths.USUARIO) // Ruta base de todos los endpoints de esta clase
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;


    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @GetMapping(ApiPaths.UserPaths.PERFIL)
    public ResponseEntity<UsuarioRequest> perfilUsuario(Authentication auth) {
        String username = auth.getName();
        return usuarioService.buscarPorUsername(username)
                .map(usuarioMapper::toDto) // Usar el mapper
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}