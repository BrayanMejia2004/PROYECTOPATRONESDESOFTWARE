package com.gobierno.servicio_identidad.domain.entities;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tokens_revocados")
public class TokenRevocado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    @Column(name = "fecha_revocacion", nullable = false)
    private Timestamp fechaRevocacion;

    @Column(name = "revocado_por", length = 50)
    private String revocadoPor;

    protected TokenRevocado() {
    }

    private TokenRevocado(Builder builder) {
        this.tokenHash = builder.tokenHash;
        this.fechaRevocacion = builder.fechaRevocacion;
        this.revocadoPor = builder.revocadoPor;
    }

    public static class Builder {
        private String tokenHash;
        private Timestamp fechaRevocacion;
        private String revocadoPor;

        public Builder tokenHash(String tokenHash) {
            this.tokenHash = tokenHash;
            return this;
        }

        public Builder fechaRevocacion(Timestamp fechaRevocacion) {
            this.fechaRevocacion = fechaRevocacion;
            return this;
        }

        public Builder revocadoPor(String revocadoPor) {
            this.revocadoPor = revocadoPor;
            return this;
        }

        public TokenRevocado build() {
            return new TokenRevocado(this);
        }
    }

    public Long getId() {
        return id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Timestamp getFechaRevocacion() {
        return fechaRevocacion;
    }

    public String getRevocadoPor() {
        return revocadoPor;
    }
}
