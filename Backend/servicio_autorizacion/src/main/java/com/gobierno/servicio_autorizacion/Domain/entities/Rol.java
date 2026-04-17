package com.gobierno.servicio_autorizacion.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Rol { // Entidad que representa un rol en el sistema

    @Id // Define este campo como clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento en PostgreSQL
    private Long id; // ID único del rol (PK)

    @Column(name = "nombre", nullable = false, unique = true) // Columna nombre, obligatorio y único
    private String nombre; // Nombre del rol (ADMIN, USER, AUDITOR)

    @Column(name = "descripcion") // Columna para descripción del rol
    private String descripcion; // Descripción del rol

    public Rol() {
    } // Constructor vacío para JPA

    public Rol(String nombre, String descripcion) { // Constructor con parámetros
        this.nombre = nombre; // Asigna el nombre
        this.descripcion = descripcion; // Asigna la descripción
    }

    public Long getId() { // Getter para obtener el ID
        return id;
    }

    public String getNombre() { // Getter para obtener el nombre
        return nombre;
    }

    public String getDescripcion() { // Getter para obtener la descripción
        return descripcion;
    }

    public void setNombre(String nombre) { // Setter para modificar el nombre
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) { // Setter para modificar la descripción
        this.descripcion = descripcion;
    }
}