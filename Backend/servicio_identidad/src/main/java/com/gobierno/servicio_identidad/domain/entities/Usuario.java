package com.gobierno.servicio_identidad.domain.entities;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id // Define este campo como clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento en PostgreSQL
    private Long id; // ID único del usuario (PK)

    @Column(name = "username", nullable = false, unique = true, length = 50) // Columna username, obligatorio y único
    private String username; // Nombre de usuario para login

    @Column(name = "password", nullable = false, length = 255) // Columna password, obligatorio
    private String password; // Contraseña encriptada con BCrypt

    @Column(name = "email", nullable = false, unique = true, length = 100) // Columna email, obligatorio y único
    private String email; // Correo electrónico único del usuario

    @Column(name = "estado") // Columna para estado activo/inactivo
    private Boolean estado; // true = activo, false = inactivo

    @Column(name = "fecha_creacion") // Columna para fecha de registro
    private Timestamp fechaCreacion; // Fecha y hora de creación del usuario

    protected Usuario() {
    } // Constructor protegido para que JPA pueda instanciar la entidad

    private Usuario(Builder builder) { // Constructor privado que recibe el Builder
        this.id = builder.id; // Asigna el ID desde el builder
        this.username = builder.username; // Asigna el username desde el builder
        this.password = builder.password; // Asigna el password desde el builder
        this.email = builder.email; // Asigna el email desde el builder
        this.estado = builder.estado; // Asigna el estado desde el builder
        this.fechaCreacion = builder.fechaCreacion; // Asigna la fecha desde el builder
    }

    public static class Builder { // Clase interna para patrón Builder
        private Long id; // Campo opcional para ID
        private String username; // Campo obligatorio para username
        private String password; // Campo obligatorio para password
        private String email; // Campo obligatorio para email
        private Boolean estado; // Campo opcional para estado
        private java.sql.Timestamp fechaCreacion; // Campo opcional para fecha

        public Builder id(Long id) { // Setter fluent para ID
            this.id = id;
            return this; // Retorna this para encadenar llamadas
        }

        public Builder username(String username) { // Setter fluent para username
            this.username = username;
            return this;
        }

        public Builder password(String password) { // Setter fluent para password
            this.password = password;
            return this;
        }

        public Builder email(String email) { // Setter fluent para email
            this.email = email;
            return this;
        }

        public Builder estado(Boolean estado) { // Setter fluent para estado
            this.estado = estado;
            return this;
        }

        public Builder fechaCreacion(java.sql.Timestamp fechaCreacion) { // Setter fluent para fecha
            this.fechaCreacion = fechaCreacion;
            return this;
        }

        public Usuario build() { // Método que construye el objeto Usuario
            return new Usuario(this); // Crea una nueva instancia con los datos del builder
        }
    }

    public Long getId() { // Getter para obtener el ID
        return id;
    }

    public void setId(Long id) { // Setter para modificar el ID
        this.id = id;
    }

    public String getUsername() { // Getter para obtener el username
        return username;
    }

    public void setUsername(String username) { // Setter para modificar el username
        this.username = username;
    }

    public String getPassword() { // Getter para obtener el password (encriptado)
        return password;
    }

    public String getEmail() { // Getter para obtener el email
        return email;
    }

    public Boolean getEstado() { // Getter para obtener el estado
        return estado;
    }

    public Timestamp getFechaCreacion() { // Getter para obtener la fecha de creación
        return fechaCreacion;
    }

    public void activar() { // Método para activar al usuario
        this.estado = true; // Cambia el estado a activo
    }

    public void desactivar() { // Método para desactivar al usuario
        this.estado = false; // Cambia el estado a inactivo
    }

    public void actualizarEmail(String nuevoEmail) { // Método para actualizar el email
        this.email = nuevoEmail; // Asigna el nuevo email
    }

    public void actualizarPassword(String nuevaPasswordEncriptada) { // Método para actualizar password
        this.password = nuevaPasswordEncriptada; // Asigna la nueva contraseña encriptada
    }
}