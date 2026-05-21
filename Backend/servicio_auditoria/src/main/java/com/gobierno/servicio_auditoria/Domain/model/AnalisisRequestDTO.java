package com.gobierno.servicio_auditoria.domain.model;

import java.time.LocalDate;
import java.util.Map;

public class AnalisisRequestDTO {
    private String estrategia;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private Map<String, String> filtros;

    public AnalisisRequestDTO() {}

    public AnalisisRequestDTO(String estrategia, LocalDate fechaDesde, LocalDate fechaHasta, Map<String, String> filtros) {
        this.estrategia = estrategia;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.filtros = filtros;
    }

    public String getEstrategia() { return estrategia; }
    public void setEstrategia(String estrategia) { this.estrategia = estrategia; }
    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }
    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }
    public Map<String, String> getFiltros() { return filtros; }
    public void setFiltros(Map<String, String> filtros) { this.filtros = filtros; }
}
