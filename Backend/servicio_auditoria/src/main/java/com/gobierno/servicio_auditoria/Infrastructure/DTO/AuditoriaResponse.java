package com.gobierno.servicio_auditoria.Infrastructure.DTO;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonInclude;

// Excluye campos nulos en la serializacion JSON
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditoriaResponse {

    // Campos de la respuesta
    private Integer usuario;
    private String accion;
    private String descripcion;
    private String ip;
    private String tipo;
    private Timestamp fecha;

    // Constructor vacio requerido por Jackson
    public AuditoriaResponse() {
    }

    // Constructor privado requerido por el Builder
    private AuditoriaResponse(Builder builder) {
        this.usuario = builder.usuario;
        this.accion = builder.accion;
        this.descripcion = builder.descripcion;
        this.fecha = builder.fecha;
        this.ip = builder.ip;
        this.tipo = builder.tipo;
    }

    // Builder Pattern - clase estatica para construccion fluida
    public static class Builder {

        private Integer usuario;
        private String accion;
        private String descripcion;
        private Timestamp fecha;
        private String ip;
        private String tipo;

        // Metodos fluent para configurar cada campo
        public Builder usuario(Integer usuario) {
            this.usuario = usuario;
            return this;
        }

        public Builder accion(String accion) {
            this.accion = accion;
            return this;
        }

        public Builder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public Builder ip(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder tipo(String tipo) {
            this.tipo = tipo;
            return this;
        }

        public Builder fecha(Timestamp fecha) {
            this.fecha = fecha;
            return this;
        }

        // Metodo build que crea la instancia
        public AuditoriaResponse build() {
            return new AuditoriaResponse(this);
        }
    }

    // Getters y setters
    public Integer getUsuario() {
        return usuario;
    }

    public void setUsuario(Integer usuario) {
        this.usuario = usuario;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }
}
