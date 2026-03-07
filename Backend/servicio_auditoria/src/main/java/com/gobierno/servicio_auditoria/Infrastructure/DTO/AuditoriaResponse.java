package com.gobierno.servicio_auditoria.Infrastructure.DTO;

import java.sql.Timestamp;

public class AuditoriaResponse {

    private Integer usuario;
    private String accion;
    private String descripcion;
    private String ip;
    private String tipo;
    private Timestamp fecha;

    public AuditoriaResponse() {
    }

    public AuditoriaResponse(Integer usuario, String accion, String descripcion,
            String ip, String tipo, Timestamp fecha) {
        this.usuario = usuario;
        this.accion = accion;
        this.descripcion = descripcion;
        this.ip = ip;
        this.tipo = tipo;
        this.fecha = fecha;
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
