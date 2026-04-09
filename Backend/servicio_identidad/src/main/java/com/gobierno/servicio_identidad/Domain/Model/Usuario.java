package com.gobierno.servicio_identidad.Domain.Model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Modelo Entidad Usuario

@Entity
@Table(name = "usuarios")
public class Usuario {

    // Campos de la entidad Usuario
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "fecha_creacion")
    private Timestamp fechaCreacion;

    // Constructor vacío requerido por JPA
    protected Usuario() {

    }

    private Usuario(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.estado = builder.estado;
        this.fechaCreacion = builder.fechaCreacion;
    }

    public static class Builder {
        private String username;
        private String password;
        private String email;
        private Boolean estado;
        private java.sql.Timestamp fechaCreacion;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder estado(Boolean estado) {
            this.estado = estado;
            return this;
        }

        public Builder fechaCreacion(java.sql.Timestamp fechaCreacion) {
            this.fechaCreacion = fechaCreacion;
            return this;
        }

        public Usuario build() {
            return new Usuario(this);
        }
    }

    // Getters para los campos de la entidad
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getEstado() {
        return estado;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    
    // Método para activar el usuario
    public void activar() {
        this.estado = true;
    }

    // Métodos para desactivar el usuario
    public void desactivar() {
        this.estado = false;
    }

    // Metodo para actualizar el email del usuario
    public void actualizarEmail(String nuevoEmail) {
        this.email = nuevoEmail;
    }

    // Método para actualizar la contraseña del usuario
    public void actualizarPassword(String nuevaPasswordEncriptada) {
        this.password = nuevaPasswordEncriptada;
    }
}
