package com.gobierno.servicio_reportes.domain.entities;

import java.time.LocalDateTime;

public class Reporte {
    
    private Long id;
    private String tipo;
    private String titulo;
    private String descripcion;
    private String contenido;
    private String formato;
    private LocalDateTime fechaGeneracion;
    private String usuarioSolicitante;
    private String estado;
    
    public Reporte() {
        this.fechaGeneracion = LocalDateTime.now();
        this.estado = "GENERADO";
    }
    
    public Reporte(String tipo, String titulo, String descripcion, String contenido, 
                   String formato, String usuarioSolicitante) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.contenido = contenido;
        this.formato = formato;
        this.usuarioSolicitante = usuarioSolicitante;
        this.fechaGeneracion = LocalDateTime.now();
        this.estado = "GENERADO";
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public String getUsuarioSolicitante() { return usuarioSolicitante; }
    public void setUsuarioSolicitante(String usuarioSolicitante) { this.usuarioSolicitante = usuarioSolicitante; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
