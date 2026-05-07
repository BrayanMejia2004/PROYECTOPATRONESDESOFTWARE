package com.gobierno.servicio_auditoria.infrastructure.adapter.dto;

import java.time.LocalDateTime;

public class IpDetalleEventoDTO {
    private Long usuarioId;
    private String accion;
    private String descripcion;
    private LocalDateTime fecha;
    private String tipo;

    public IpDetalleEventoDTO() {}

    public IpDetalleEventoDTO(Long usuarioId, String accion, String descripcion,
                              LocalDateTime fecha, String tipo) {
        this.usuarioId = usuarioId;
        this.accion = accion;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.tipo = tipo;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
