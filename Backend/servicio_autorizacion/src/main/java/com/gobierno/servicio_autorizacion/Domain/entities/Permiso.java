package com.gobierno.servicio_autorizacion.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "permisos")
public class Permiso { // Entidad que representa un permiso en el sistema

    @Id // Define este campo como clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento en PostgreSQL
    private Long id; // ID único del permiso (PK)

    @Column(unique = true, nullable = false) // Columna nombre, único y obligatorio
    private String nombre; // Nombre del permiso (ej: "LEER_USUARIOS")

    private String descripcion; // Descripción del permiso

    private String recurso; // Recurso al que se aplica el permiso (ej: "usuarios")

    private String accion; // Acción que permite el permiso (ej: "READ", "WRITE")

    public Permiso() {
    } // Constructor vacío para JPA

    public Permiso(String nombre, String descripcion, String recurso, String accion) { // Constructor con parámetros
        this.nombre = nombre; // Asigna el nombre
        this.descripcion = descripcion; // Asigna la descripción
        this.recurso = recurso; // Asigna el recurso
        this.accion = accion; // Asigna la acción
    }

    public Long getId() { // Getter para obtener el ID
        return id;
    }

    public String getNombre() { // Getter para obtener el nombre
        return nombre;
    }

    public void setNombre(String nombre) { // Setter para modificar el nombre
        this.nombre = nombre;
    }

    public String getDescripcion() { // Getter para obtener la descripción
        return descripcion;
    }

    public void setDescripcion(String descripcion) { // Setter para modificar la descripción
        this.descripcion = descripcion;
    }

    public String getRecurso() { // Getter para obtener el recurso
        return recurso;
    }

    public void setRecurso(String recurso) { // Setter para modificar el recurso
        this.recurso = recurso;
    }

    public String getAccion() { // Getter para obtener la acción
        return accion;
    }

    public void setAccion(String accion) { // Setter para modificar la acción
        this.accion = accion;
    }
}