package com.hotguy.tareas.controller;

import com.hotguy.tareas.dto.AuthRequest;
import com.hotguy.tareas.dto.UsuarioRequest;
import com.hotguy.tareas.security.JwtUtil;
import com.hotguy.tareas.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.AUTH)
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, UsuarioService usuarioService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @PostMapping(ApiPaths.AuthPaths.LOGIN)
    public ResponseEntity<?> login(@RequestBody UsuarioRequest request) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String token = jwtUtil.generarToken(request.getUsername());
            return ResponseEntity.ok(new AuthRequest(token));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

    // Endpoint para registrar nuevos usuarios
    @PostMapping(ApiPaths.AuthPaths.REGISTER)
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioRequest request) {
        return usuarioService.registrarUsuario(request.getUsername(), request.getPassword())
                .<ResponseEntity<?>>map(token -> ResponseEntity.ok(new AuthRequest(token)))
                .orElseGet(() -> ResponseEntity.badRequest().body("El usuario ya existe"));
    }

    // Este endpoint requiere token válido (por la configuración de seguridad)
    @GetMapping(ApiPaths.AuthPaths.HELLO)
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("¡Hola! Accediste con un token válido.");
    }
}