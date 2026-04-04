package com.gobierno.servicio_auditoria.Domain.Model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Entidad auditoria
@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuario_id;

    @Column(name = "accion", nullable = false, length = 100)
    private String accion;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "fecha")
    private Timestamp fecha;

    @Column(name = "ip_origen", length = 45)
    private String ip_origen;

    @Column(name = "tipo", length = 50)
    private String tipo;

    // Constructor requerido por JPA
    public Auditoria() {

    }

    // Constructor requerido por Builder
    private Auditoria(Builder builder) {
        this.usuario_id = builder.usuario_id;
        this.accion = builder.accion;
        this.descripcion = builder.descripcion;
        this.fecha = builder.fecha;
        this.ip_origen = builder.ip_origen;
        this.tipo = builder.tipo;
    }

    public static class Builder {

        private Integer usuario_id;
        private String accion;
        private String descripcion;
        private Timestamp fecha;
        private String ip_origen;
        private String tipo;

        public Builder usuario(Integer usuario_id) {
            this.usuario_id = usuario_id;
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

        public Builder ip(String ip_origen) {
            this.ip_origen = ip_origen;
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

        public Auditoria build() {
            return new Auditoria(this);
        }
    }

    public Long getId() {
        return id;
    }

    public Integer getUsuario_id() {
        return usuario_id;
    }

    public String getAccion() {
        return accion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public String getIp_origen() {
        return ip_origen;
    }

    public String getTipo() {
        return tipo;
    }

    public void setUsuario_id(Integer usuario_id) {
        this.usuario_id = usuario_id;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setIp_origen(String ip_origen) {
        this.ip_origen = ip_origen;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    // metodo Prototype
    // permite crear una copia exacta del objeto actual
    @Override
    public Auditoria clone() {

        Auditoria copia = new Auditoria();

        copia.usuario_id = this.usuario_id;
        copia.accion = this.accion;
        copia.descripcion = this.descripcion;
        copia.fecha = this.fecha;
        copia.ip_origen = this.ip_origen;
        copia.tipo = this.tipo;

        return copia;
    }
}
