package com.gobierno.servicio_auditoria.domain.model;

import java.time.LocalDateTime;
import java.util.Map;

public class ThreatEventDTO {
    private Long id;
    private String tipo;
    private String severidad;
    private String descripcion;
    private Integer usuarioId;
    private String ipOrigen;
    private LocalDateTime fecha;
    private String accion;
    private Map<String, Object> metricas;
    private Boolean activa;

    public ThreatEventDTO() {}

    public ThreatEventDTO(Long id, String tipo, String severidad, String descripcion,
                          Integer usuarioId, String ipOrigen, LocalDateTime fecha,
                          String accion, Map<String, Object> metricas, Boolean activa) {
        this.id = id;
        this.tipo = tipo;
        this.severidad = severidad;
        this.descripcion = descripcion;
        this.usuarioId = usuarioId;
        this.ipOrigen = ipOrigen;
        this.fecha = fecha;
        this.accion = accion;
        this.metricas = metricas;
        this.activa = activa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getSeveridad() { return severidad; }
    public void setSeveridad(String severidad) { this.severidad = severidad; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getIpOrigen() { return ipOrigen; }
    public void setIpOrigen(String ipOrigen) { this.ipOrigen = ipOrigen; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public Map<String, Object> getMetricas() { return metricas; }
    public void setMetricas(Map<String, Object> metricas) { this.metricas = metricas; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
}