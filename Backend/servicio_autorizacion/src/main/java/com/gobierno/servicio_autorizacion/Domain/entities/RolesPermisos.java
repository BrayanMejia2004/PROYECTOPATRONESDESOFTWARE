package com.gobierno.servicio_autorizacion.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles_permisos")
public class RolesPermisos { // Entidad para la relación rol-permiso

    @Id // Define este campo como clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento en PostgreSQL
    private Long id; // ID único de la relación (PK)

    @ManyToOne // Relación muchos a uno con Rol
    @JoinColumn(name = "rol_id", nullable = false) // Columna FK al rol
    private Rol rol; // Rol que tiene el permiso

    @ManyToOne // Relación muchos a uno con Permiso
    @JoinColumn(name = "permiso_id", nullable = false) // Columna FK al permiso
    private Permiso permiso; // Permiso asociado al rol

    public RolesPermisos() {
    } // Constructor vacío para JPA

    public RolesPermisos(Rol rol, Permiso permiso) { // Constructor con parámetros
        this.rol = rol; // Asigna el rol
        this.permiso = permiso; // Asigna el permiso
    }

    public Long getId() { // Getter para obtener el ID
        return id;
    }

    public Rol getRol() { // Getter para obtener el rol
        return rol;
    }

    public void setRol(Rol rol) { // Setter para modificar el rol
        this.rol = rol;
    }

    public Permiso getPermiso() { // Getter para obtener el permiso
        return permiso;
    }

    public void setPermiso(Permiso permiso) { // Setter para modificar el permiso
        this.permiso = permiso;
    }
}