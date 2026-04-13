package com.gobierno.servicio_reportes.infrastructure.persistence.entity;

import com.gobierno.servicio_reportes.domain.entities.Reporte;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
public class ReporteEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String tipo;
    
    @Column(nullable = false)
    private String titulo;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;
    
    @Column(nullable = false, length = 20)
    private String formato;
    
    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;
    
    @Column(name = "usuario_solicitante", length = 100)
    private String usuarioSolicitante;
    
    @Column(length = 20)
    private String estado = "GENERADO";
    
    public ReporteEntity() {
        this.fechaGeneracion = LocalDateTime.now();
        this.estado = "GENERADO";
    }
    
    public static ReporteEntity fromDomain(Reporte reporte) {
        ReporteEntity entity = new ReporteEntity();
        if (reporte.getId() != null) entity.setId(reporte.getId());
        entity.setTipo(reporte.getTipo());
        entity.setTitulo(reporte.getTitulo());
        entity.setDescripcion(reporte.getDescripcion());
        entity.setContenido(reporte.getContenido());
        entity.setFormato(reporte.getFormato());
        entity.setFechaGeneracion(reporte.getFechaGeneracion());
        entity.setUsuarioSolicitante(reporte.getUsuarioSolicitante());
        entity.setEstado(reporte.getEstado());
        return entity;
    }
    
    public Reporte toDomain() {
        Reporte reporte = new Reporte();
        reporte.setId(this.id);
        reporte.setTipo(this.tipo);
        reporte.setTitulo(this.titulo);
        reporte.setDescripcion(this.descripcion);
        reporte.setContenido(this.contenido);
        reporte.setFormato(this.formato);
        reporte.setFechaGeneracion(this.fechaGeneracion);
        reporte.setUsuarioSolicitante(this.usuarioSolicitante);
        reporte.setEstado(this.estado);
        return reporte;
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
