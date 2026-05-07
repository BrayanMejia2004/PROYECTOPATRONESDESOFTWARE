package com.gobierno.servicio_auditoria.infrastructure.adapter.dto;

import java.time.LocalDateTime;

public class TimelineEventoDTO {
    private Long id;
    private String accion;
    private String descripcion;
    private LocalDateTime fecha;
    private String ipOrigen;
    private String tipo;
    private String icono;

    public TimelineEventoDTO() {}

    public TimelineEventoDTO(Long id, String accion, String descripcion, LocalDateTime fecha, String ipOrigen, String tipo, String icono) {
        this.id = id;
        this.accion = accion;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.ipOrigen = ipOrigen;
        this.tipo = tipo;
        this.icono = icono;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getIpOrigen() { return ipOrigen; }
    public void setIpOrigen(String ipOrigen) { this.ipOrigen = ipOrigen; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }
}
