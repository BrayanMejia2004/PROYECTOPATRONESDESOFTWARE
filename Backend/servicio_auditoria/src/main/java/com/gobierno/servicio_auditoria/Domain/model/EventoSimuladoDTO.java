package com.gobierno.servicio_auditoria.domain.model;

public class EventoSimuladoDTO {
    private String accion;
    private String descripcion;
    private Integer usuarioId;
    private String ipOrigen;
    private String tipo;
    private String fecha;

    public EventoSimuladoDTO() {}

    public EventoSimuladoDTO(String accion, String descripcion, Integer usuarioId, String ipOrigen, String tipo, String fecha) {
        this.accion = accion;
        this.descripcion = descripcion;
        this.usuarioId = usuarioId;
        this.ipOrigen = ipOrigen;
        this.tipo = tipo;
        this.fecha = fecha;
    }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public String getIpOrigen() { return ipOrigen; }
    public void setIpOrigen(String ipOrigen) { this.ipOrigen = ipOrigen; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
