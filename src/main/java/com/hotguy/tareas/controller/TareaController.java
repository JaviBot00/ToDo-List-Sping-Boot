package com.hotguy.tareas.controller;

import com.hotguy.tareas.dto.TareaRequest;
import com.hotguy.tareas.model.Tarea;
import com.hotguy.tareas.service.TareaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marca esta clase como controlador REST
@RequestMapping(ApiPaths.TAREAS) // Ruta base de todos los endpoints de esta clase
public class TareaController {

    @Autowired
    private TareaService tareaService;

    @GetMapping(ApiPaths.TareaPaths.LISTAR)
    public ResponseEntity<List<Tarea>> listarTareas(
            @RequestParam(required = false) Boolean completada,
            @RequestParam(required = false, defaultValue = "fecha_desc") String orden,
            Authentication auth
    ) {
        String username = auth.getName();
        List<Tarea> tareas = tareaService.obtenerTareasFiltradasYOrdenadas(username, completada, orden);
        return ResponseEntity.ok(tareas);
    }

    @PostMapping(ApiPaths.TareaPaths.CREAR)
    public ResponseEntity<Tarea> crearTarea(@RequestBody TareaRequest request) {
        Tarea tarea = tareaService.crearTarea(request.getTitulo(), request.getDescripcion());
        return ResponseEntity.ok(tarea);
    }

    @PatchMapping(ApiPaths.TareaPaths.COMPLETAR)
    public ResponseEntity<Tarea> alternarCompletada(@PathVariable Long id) {
        Tarea tarea = tareaService.toggleCompletada(id);
        return ResponseEntity.ok(tarea);
    }

    @PatchMapping(ApiPaths.TareaPaths.EDITAR)
    public ResponseEntity<Tarea> editarTarea(@PathVariable Long id, @RequestBody TareaRequest request) {
        Tarea tarea = tareaService.editarTarea(id, request.getDescripcion(), request.getCompletada());
        return ResponseEntity.ok(tarea);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(ApiPaths.TareaPaths.ELIMINAR)
    public ResponseEntity<?> eliminarTarea(@PathVariable Long id) {
        boolean eliminada = tareaService.eliminarTarea(id);
        if (eliminada) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}