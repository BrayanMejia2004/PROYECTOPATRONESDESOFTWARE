package com.gobierno.servicio_auditoria.infrastructure.adapter.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditoriaResponse {

    private Integer usuario;
    private String accion;
    private String descripcion;
    private String ip;
    private String tipo;
    private Timestamp fecha;

    public AuditoriaResponse() {
    }

    private AuditoriaResponse(Builder builder) {
        this.usuario = builder.usuario;
        this.accion = builder.accion;
        this.descripcion = builder.descripcion;
        this.fecha = builder.fecha;
        this.ip = builder.ip;
        this.tipo = builder.tipo;
    }

    public static class Builder {

        private Integer usuario;
        private String accion;
        private String descripcion;
        private Timestamp fecha;
        private String ip;
        private String tipo;

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

        public AuditoriaResponse build() {
            return new AuditoriaResponse(this);
        }
    }

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
