package com.gobierno.servicio_auditoria.domain.model;

import java.time.LocalDate;

public class ActividadDiariaDTO {
    private LocalDate fecha;
    private Long total;

    public ActividadDiariaDTO() {}

    public ActividadDiariaDTO(LocalDate fecha, Long total) {
        this.fecha = fecha;
        this.total = total;
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
}
