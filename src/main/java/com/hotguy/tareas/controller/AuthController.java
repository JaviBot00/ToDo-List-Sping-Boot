package com.hotguy.tareas.controller;

import com.hotguy.tareas.dto.AuthRequest;
import com.hotguy.tareas.dto.UsuarioRequest;
import com.hotguy.tareas.service.AuthenticationService;
import com.hotguy.tareas.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.AUTH)
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(ApiPaths.AuthPaths.LOGIN)
    public ResponseEntity<?> login(@RequestBody UsuarioRequest request) {
        return authenticationService.loguearUsuario(request);
    }

    // Endpoint para registrar nuevos usuarios
    @PostMapping(ApiPaths.AuthPaths.REGISTER)
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioRequest request) {
        return authenticationService.registrarUsuario(request.getUsername(), request.getPassword())
                .<ResponseEntity<?>>map(token -> ResponseEntity.ok(new AuthRequest(token)))
                .orElseGet(() -> ResponseEntity.badRequest().body("El usuario ya existe"));
    }

    // Este endpoint requiere token válido (por la configuración de seguridad)
    @GetMapping(ApiPaths.AuthPaths.HELLO)
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("¡Hola! Accediste con un token válido.");
    }
}