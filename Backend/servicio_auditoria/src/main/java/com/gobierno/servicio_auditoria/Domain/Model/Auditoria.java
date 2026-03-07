package com.gobierno.servicio_auditoria.Domain.Model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    @Column(name = "fecha", nullable = false)
    private Timestamp fecha;

    @Column(name = "ip_origen", length = 45)
    private String ip_origen;

    // Constructor para crear una nueva auditoría
    public Auditoria(Integer usuario_id, String accion, String descripcion, Timestamp fecha, String ip_origen) {
        this.usuario_id = usuario_id;
        this.accion = accion;
        this.descripcion = descripcion;
        this.fecha = Timestamp.from(java.time.Instant.now());
        this.ip_origen = ip_origen;
    }

    // Getters para los campos de la entidad
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

    public void setId(Long id) {
        this.id = id;
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

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public void setIp_origen(String ip_origen) {
        this.ip_origen = ip_origen;
    }
}
