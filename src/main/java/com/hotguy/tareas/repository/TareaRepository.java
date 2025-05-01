package com.hotguy.tareas.repository;

import com.hotguy.tareas.model.Tarea;
import com.hotguy.tareas.model.Usuario;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Indica que esta interfaz es un repositorio
public interface TareaRepository extends JpaRepository<Tarea, Long> {
    // Puedes definir métodos personalizados si los necesitas
    List<Tarea> findByUsuario(Usuario usuario, Sort sort);

    List<Tarea> findByUsuarioAndCompletada(Usuario usuario, boolean completada, Sort sort);
}