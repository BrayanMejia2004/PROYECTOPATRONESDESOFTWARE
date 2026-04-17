package com.gobierno.servicio_autorizacion.domain.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios_roles")
public class UsuariosRoles { // Entidad para la relación usuario-rol

    @Id // Define este campo como clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento en PostgreSQL
    private Long id; // ID único de la relación (PK)

    private String username; // Nombre de usuario al que se asigna el rol

    @ManyToOne // Relación muchos a uno con Rol
    @JoinColumn(name = "rol_id", nullable = false) // Columna FK al rol
    private Rol rol; // Rol asignado al usuario

    private LocalDateTime fechaAsignacion; // Fecha en que se asignó el rol

    public UsuariosRoles() {
    } // Constructor vacío para JPA

    public UsuariosRoles(String username, Rol rol) { // Constructor con parámetros
        this.username = username; // Asigna el username
        this.rol = rol; // Asigna el rol
        this.fechaAsignacion = LocalDateTime.now(); // Asigna la fecha actual
    }

    public Long getId() { // Getter para obtener el ID
        return id;
    }

    public String getUsername() { // Getter para obtener el username
        return username;
    }

    public void setUsername(String username) { // Setter para modificar el username
        this.username = username;
    }

    public Rol getRol() { // Getter para obtener el rol
        return rol;
    }

    public void setRol(Rol rol) { // Setter para modificar el rol
        this.rol = rol;
    }

    public LocalDateTime getFechaAsignacion() { // Getter para obtener la fecha de asignación
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) { // Setter para modificar la fecha
        this.fechaAsignacion = fechaAsignacion;
    }
}