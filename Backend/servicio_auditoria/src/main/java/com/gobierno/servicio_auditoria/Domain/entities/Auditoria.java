package com.gobierno.servicio_auditoria.domain.entities;

import java.sql.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auditoria")
public class Auditoria {  // Entidad que representa un registro de auditoría

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // ID único del registro de auditoría

    @Column(name = "usuario_id", nullable = false)
    private Integer usuario_id;  // ID del usuario que realiza la acción

    @Column(name = "accion", nullable = false, length = 100)
    private String accion;  // Nombre de la acción realizada

    @Column(name = "descripcion", length = 255)
    private String descripcion;  // Descripción detallada de la acción

    @Column(name = "fecha")
    private Timestamp fecha;  // Fecha y hora de la auditoría

    @Column(name = "ip_origen", length = 45)
    private String ip_origen;  // Dirección IP de origen

    @Column(name = "tipo", length = 50)
    private String tipo;  // Tipo de auditoría (BASICA, COMPLETA, SEGURIDAD)

    @Column(name = "simulacion_id", length = 50)
    private String simulacion_id;  // ID de simulación para eventos generados por el simulador (PLUS_08)

    public Auditoria() {
    }  // Constructor vacío para JPA

    private Auditoria(Builder builder) {  // Constructor privado que recibe el Builder
        this.usuario_id = builder.usuario_id;  // Asigna el usuario
        this.accion = builder.accion;  // Asigna la acción
        this.descripcion = builder.descripcion;  // Asigna la descripción
        this.fecha = builder.fecha;  // Asigna la fecha
        this.ip_origen = builder.ip_origen;  // Asigna la IP
        this.tipo = builder.tipo;  // Asigna el tipo
    }

    public static class Builder {  // Clase interna para patrón Builder

        private Integer usuario_id;
        private String accion;
        private String descripcion;
        private Timestamp fecha;
        private String ip_origen;
        private String tipo;

        public Builder usuario(Integer usuario_id) {  // Setter fluent para usuario
            this.usuario_id = usuario_id;
            return this;
        }

        public Builder accion(String accion) {  // Setter fluent para acción
            this.accion = accion;
            return this;
        }

        public Builder descripcion(String descripcion) {  // Setter fluent para descripción
            this.descripcion = descripcion;
            return this;
        }

        public Builder ip(String ip_origen) {  // Setter fluent para IP
            this.ip_origen = ip_origen;
            return this;
        }

        public Builder tipo(String tipo) {  // Setter fluent para tipo
            this.tipo = tipo;
            return this;
        }

        public Builder fecha(Timestamp fecha) {  // Setter fluent para fecha
            this.fecha = fecha;
            return this;
        }

        public Auditoria build() {  // Construye el objeto Auditoria
            return new Auditoria(this);
        }
    }

    public Long getId() { return id; }  // Getter para ID

    public Integer getUsuario_id() { return usuario_id; }  // Getter para usuario ID

    public String getAccion() { return accion; }  // Getter para acción

    public String getDescripcion() { return descripcion; }  // Getter para descripción

    public Timestamp getFecha() { return fecha; }  // Getter para fecha

    public String getIp_origen() { return ip_origen; }  // Getter para IP

    public String getTipo() { return tipo; }  // Getter para tipo

    public String getSimulacion_id() { return simulacion_id; }  // Getter para ID de simulación

    public void setSimulacion_id(String simulacion_id) { this.simulacion_id = simulacion_id; }  // Setter para ID de simulación

    public void setUsuario_id(Integer usuario_id) { this.usuario_id = usuario_id; }  // Setter para usuario ID

    public void setAccion(String accion) { this.accion = accion; }  // Setter para acción

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }  // Setter para descripción

    public void setIp_origen(String ip_origen) { this.ip_origen = ip_origen; }  // Setter para IP

    public void setTipo(String tipo) { this.tipo = tipo; }  // Setter para tipo

    public void setFecha(Timestamp fecha) { this.fecha = fecha; }  // Setter para fecha

    @Override
    public Auditoria clone() {  // Método del patrón Prototype para clonar la auditoría
        Auditoria copia = new Auditoria();  // Crea una nueva instancia
        copia.usuario_id = this.usuario_id;  // Copia el usuario
        copia.accion = this.accion;  // Copia la acción
        copia.descripcion = this.descripcion;  // Copia la descripción
        copia.fecha = this.fecha;  // Copia la fecha
        copia.ip_origen = this.ip_origen;  // Copia la IP
        copia.tipo = this.tipo;  // Copia el tipo
        copia.simulacion_id = this.simulacion_id;  // Copia el ID de simulación
        return copia;  // Retorna la copia
    }
}