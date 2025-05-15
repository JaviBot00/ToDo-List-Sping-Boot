package com.hotguy.tareas.controller;

import com.hotguy.tareas.dto.UsuarioRequest;
import com.hotguy.tareas.model.Usuario;
import com.hotguy.tareas.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Marca esta clase como controlador REST
@RequestMapping(ApiPaths.ADMIN) // Ruta base de todos los endpoints de esta clase
public class AdminController {

    private final UsuarioService usuarioService;

    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping(ApiPaths.AdminPaths.LISTAR_USUARIOS)
    public ResponseEntity<List<UsuarioRequest>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @PatchMapping(ApiPaths.AdminPaths.PROMOCIONAR)
    public ResponseEntity<?> promocionarAAdmin(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioService.promocionarAAdmin(id);
        return usuarioOpt
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok("Usuario promocionado a ADMIN"))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping(ApiPaths.AdminPaths.CAMBIAR_ROL)
    public ResponseEntity<?> cambiarRol(@PathVariable Long id, @RequestBody UsuarioRequest request) {
        boolean actualizado = usuarioService.cambiarRol(id, request.getRol());
        if (actualizado) {
            return ResponseEntity.ok("Rol actualizado");
        } else {
            return ResponseEntity.badRequest().body("No se pudo actualizar el rol");
        }
    }
}
