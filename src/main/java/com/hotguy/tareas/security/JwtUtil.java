package com.hotguy.tareas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component // Marca esta clase como un bean para inyección de dependencias
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private final SecretKey key;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    // Genera un token JWT con el nombre de usuario como subject
    public String generarToken(String username) {
        return Jwts.builder()
                .subject(username) // Quien es el dueño del token
                .issuedAt(new Date()) // Fecha de emisión
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration())) // Fecha de expiración
                .signWith(key) // Algoritmo y clave secreta
                .compact();
    }

    // Valida si el token es correcto
    public boolean validarToken(String token, String username) {
        try {
            String usuarioDelToken = extraerUsername(token);
            return usuarioDelToken.equals(username) && !estaExpirado(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Extrae el nombre de usuario (subject) del token
    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }

    // Verifica si el token está expirado
    private boolean estaExpirado(String token) {
        return extraerClaims(token).getExpiration().before(new Date());
    }

    // Extrae todos los claims (información) del token
    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}