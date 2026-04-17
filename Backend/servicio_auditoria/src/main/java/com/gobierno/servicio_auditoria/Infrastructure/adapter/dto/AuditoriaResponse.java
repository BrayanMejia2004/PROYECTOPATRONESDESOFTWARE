package com.gobierno.servicio_auditoria.infrastructure.adapter.dto;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditoriaResponse {  // DTO de respuesta para auditoría

    private Integer usuario;  // ID del usuario
    private String accion;  // Acción realizada
    private String descripcion;  // Descripción de la acción
    private String ip;  // IP de origen
    private String tipo;  // Tipo de auditoría
    private Timestamp fecha;  // Fecha de la auditoría

    public AuditoriaResponse() {
    }  // Constructor vacío

    private AuditoriaResponse(Builder builder) {  // Constructor privado del Builder
        this.usuario = builder.usuario;
        this.accion = builder.accion;
        this.descripcion = builder.descripcion;
        this.fecha = builder.fecha;
        this.ip = builder.ip;
        this.tipo = builder.tipo;
    }

    public static class Builder {  // Clase interna para patrón Builder

        private Integer usuario;
        private String accion;
        private String descripcion;
        private Timestamp fecha;
        private String ip;
        private String tipo;

        public Builder usuario(Integer usuario) {  // Setter fluent
            this.usuario = usuario;
            return this;
        }

        public Builder accion(String accion) {  // Setter fluent
            this.accion = accion;
            return this;
        }

        public Builder descripcion(String descripcion) {  // Setter fluent
            this.descripcion = descripcion;
            return this;
        }

        public Builder ip(String ip) {  // Setter fluent
            this.ip = ip;
            return this;
        }

        public Builder tipo(String tipo) {  // Setter fluent
            this.tipo = tipo;
            return this;
        }

        public Builder fecha(Timestamp fecha) {  // Setter fluent
            this.fecha = fecha;
            return this;
        }

        public AuditoriaResponse build() {  // Construye la respuesta
            return new AuditoriaResponse(this);
        }
    }

    public Integer getUsuario() { return usuario; }
    public void setUsuario(Integer usuario) { this.usuario = usuario; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
}