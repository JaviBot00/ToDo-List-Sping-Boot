package com.hotguy.tareas.service;

import com.hotguy.tareas.dto.AuthRequest;
import com.hotguy.tareas.dto.UsuarioRequest;
import com.hotguy.tareas.model.Usuario;
import com.hotguy.tareas.repository.UsuarioRepository;
import com.hotguy.tareas.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthenticationService(UsuarioRepository usuarioRepository, AuthenticationManager authManager, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.authManager = authManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> loguearUsuario(UsuarioRequest request) {
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

    public Optional<String> registrarUsuario(String username, String password) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            return Optional.empty();
        }

        Usuario nuevo = new Usuario();
        nuevo.setUsername(username);
        nuevo.setPassword(passwordEncoder.encode(password));
        nuevo.setRol("USER"); // 👈 Rol por defecto
//        nuevo.setRol(rol.equalsIgnoreCase("ADMIN") ? "ADMIN" : "USER"); // validación mínima

        usuarioRepository.save(nuevo);

        // Generar JWT
        String token = jwtUtil.generarToken(username);
        return Optional.of(token);
    }
}