package com.hotguy.tareas.service;

import com.hotguy.tareas.dto.UsuarioRequest;
import com.hotguy.tareas.mapper.UsuarioMapper;
import com.hotguy.tareas.model.Usuario;
import com.hotguy.tareas.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public List<UsuarioRequest> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toDto) // Usar el mapper
                .collect(Collectors.toList());
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Optional<Usuario> promocionarAAdmin(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        usuarioOpt.ifPresent(usuario -> {
            usuario.setRol("ADMIN");
            usuarioRepository.save(usuario);
        });
        return usuarioOpt;
    }

    public boolean cambiarRol(Long id, String nuevoRol) {
        // Validar que el ID y el rol no sean null
        if (id == null || nuevoRol == null) {
            return false;
        }

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);
        if (optUsuario.isEmpty()) return false;

        Usuario usuario = optUsuario.get();
        if (!nuevoRol.equalsIgnoreCase("ADMIN") && !nuevoRol.equalsIgnoreCase("USER")) return false;

        usuario.setRol(nuevoRol.toUpperCase());
        usuarioRepository.save(usuario);
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca en la base de datos por nombre de usuario
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol()) // Aquí debe coincidir con lo que guardas en la BD
                .build();
    }
}
