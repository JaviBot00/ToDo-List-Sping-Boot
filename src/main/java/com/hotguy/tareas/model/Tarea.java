package com.hotguy.tareas.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity // Marca esta clase como una entidad JPA (tabla en la BD)
@Table(name = "tareas") // (Opcional) nombre de la tabla
public class Tarea {

    @Id // Marca este campo como clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // El valor se genera automáticamente (auto-incremento)
    private Long id;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(min = 3, message = "La descripción debe tener al menos 3 caracteres")
    private String titulo;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(min = 3, message = "La descripción debe tener al menos 3 caracteres")
    private String descripcion;

    private boolean completada;

    @CreationTimestamp // Hibernate: se asigna automáticamente al crear la tarea
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp // Se actualiza automáticamente al modificar
    private LocalDateTime fechaActualizacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id") // clave foránea
    @JsonBackReference
    private Usuario usuario;

    // Constructor vacío (requerido por Spring)
    public Tarea() {
    }

    public Tarea(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.completada = false;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}