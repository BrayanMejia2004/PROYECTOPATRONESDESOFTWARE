package com.gobierno.servicio_identidad.Domain.Model;

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

    protected Usuario() {

    }

    public Usuario(String username, String password, String email, Boolean estado, Timestamp fechaCreacion) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.estado = true;
        this.fechaCreacion = Timestamp.from(java.time.Instant.now());
    }

    public void desactivar() {
        this.estado = false;
    }

    public void activar() {
        this.estado = true;
    }

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
}
