package com.gobierno.servicio_auditoria.domain.model;

import java.util.List;

public class EscenarioSimulacionDTO {
    private String nombre;
    private String descripcion;
    private List<EventoSimuladoDTO> eventos;
    private int batchSize;

    public EscenarioSimulacionDTO() {}

    public EscenarioSimulacionDTO(String nombre, String descripcion, List<EventoSimuladoDTO> eventos, int batchSize) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.eventos = eventos;
        this.batchSize = batchSize;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public List<EventoSimuladoDTO> getEventos() { return eventos; }
    public void setEventos(List<EventoSimuladoDTO> eventos) { this.eventos = eventos; }
    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
}
