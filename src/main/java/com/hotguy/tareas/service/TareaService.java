package com.hotguy.tareas.service;

import com.hotguy.tareas.model.Tarea;
import com.hotguy.tareas.model.Usuario;
import com.hotguy.tareas.repository.TareaRepository;
import com.hotguy.tareas.repository.UsuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service // Esta anotación indica que esta clase será inyectada como servicio
public class TareaService {

    private final TareaRepository tareaRepository;
    private final UsuarioRepository usuarioRepository;

    public TareaService(TareaRepository tareaRepository, UsuarioRepository usuarioRepository) {
        this.tareaRepository = tareaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Tarea crearTarea(String titulo, String descripcion) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();

        Tarea tarea = new Tarea();
        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion);
        tarea.setUsuario(usuario); // vínculo con el usuario
        return tareaRepository.save(tarea);
    }

    public List<Tarea> obtenerTareasFiltradasYOrdenadas(String username, Boolean completada, String orden) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<Sort.Order> ordenes = new ArrayList<>();

        for (String campo : orden.split(",")) {
            switch (campo) {
                case "fecha_asc" -> ordenes.add(Sort.Order.asc("fechaCreacion"));
                case "fecha_desc" -> ordenes.add(Sort.Order.desc("fechaCreacion"));
                case "estado_asc" -> ordenes.add(Sort.Order.asc("completada"));
                case "estado_desc" -> ordenes.add(Sort.Order.desc("completada"));
            }
        }

        Sort sort = ordenes.isEmpty() ? Sort.by("fechaCreacion").descending() : Sort.by(ordenes);

        if (completada == null) {
            return tareaRepository.findByUsuario(usuario, sort);
        } else {
            return tareaRepository.findByUsuarioAndCompletada(usuario, completada, sort);
        }
    }

    public Tarea toggleCompletada(Long id) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        tarea.setCompletada(!tarea.isCompletada()); // Invierte el estado actual
//        tarea.setFechaActualizacion(LocalDateTime.now());

        return tareaRepository.save(tarea);
    }

    public Tarea editarTarea(Long id, String descripcion, Boolean completada) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        if (descripcion != null) tarea.setDescripcion(descripcion);
        if (completada != null) tarea.setCompletada(completada);
//        tarea.setFechaActualizacion(LocalDateTime.now());

        return tareaRepository.save(tarea);
    }


    public boolean eliminarTarea(Long id) {
        if (tareaRepository.existsById(id)) {
            tareaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}