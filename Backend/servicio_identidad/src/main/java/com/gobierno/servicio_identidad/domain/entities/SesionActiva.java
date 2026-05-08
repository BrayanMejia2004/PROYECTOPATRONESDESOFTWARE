package com.gobierno.servicio_identidad.domain.entities;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sesiones_activas")
public class SesionActiva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "fecha_inicio", nullable = false)
    private Timestamp fechaInicio;

    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    @Column(name = "activa", nullable = false)
    private Boolean activa;

    protected SesionActiva() {
    }

    private SesionActiva(Builder builder) {
        this.usuarioId = builder.usuarioId;
        this.username = builder.username;
        this.ipOrigen = builder.ipOrigen;
        this.fechaInicio = builder.fechaInicio;
        this.tokenHash = builder.tokenHash;
        this.activa = builder.activa;
    }

    public static class Builder {
        private Long usuarioId;
        private String username;
        private String ipOrigen;
        private Timestamp fechaInicio;
        private String tokenHash;
        private Boolean activa;

        public Builder usuarioId(Long usuarioId) {
            this.usuarioId = usuarioId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder ipOrigen(String ipOrigen) {
            this.ipOrigen = ipOrigen;
            return this;
        }

        public Builder fechaInicio(Timestamp fechaInicio) {
            this.fechaInicio = fechaInicio;
            return this;
        }

        public Builder tokenHash(String tokenHash) {
            this.tokenHash = tokenHash;
            return this;
        }

        public Builder activa(Boolean activa) {
            this.activa = activa;
            return this;
        }

        public SesionActiva build() {
            return new SesionActiva(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getUsername() {
        return username;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public Timestamp getFechaInicio() {
        return fechaInicio;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
}
