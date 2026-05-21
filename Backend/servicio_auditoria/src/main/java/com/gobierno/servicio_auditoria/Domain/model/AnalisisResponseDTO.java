package com.gobierno.servicio_auditoria.domain.model;

import java.util.List;
import java.util.Map;

public class AnalisisResponseDTO {
    private String estrategia;
    private List<Map<String, Object>> datos;
    private Map<String, Object> metricas;
    private List<String> insights;

    public AnalisisResponseDTO() {}

    public AnalisisResponseDTO(String estrategia, List<Map<String, Object>> datos, Map<String, Object> metricas, List<String> insights) {
        this.estrategia = estrategia;
        this.datos = datos;
        this.metricas = metricas;
        this.insights = insights;
    }

    public String getEstrategia() { return estrategia; }
    public void setEstrategia(String estrategia) { this.estrategia = estrategia; }
    public List<Map<String, Object>> getDatos() { return datos; }
    public void setDatos(List<Map<String, Object>> datos) { this.datos = datos; }
    public Map<String, Object> getMetricas() { return metricas; }
    public void setMetricas(Map<String, Object> metricas) { this.metricas = metricas; }
    public List<String> getInsights() { return insights; }
    public void setInsights(List<String> insights) { this.insights = insights; }
}
