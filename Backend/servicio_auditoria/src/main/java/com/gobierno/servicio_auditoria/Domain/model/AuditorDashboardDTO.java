package com.gobierno.servicio_auditoria.domain.model;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import java.util.List;

public class AuditorDashboardDTO {
    private long eventosHoy;
    private long eventoSemana;
    private long eventosMes;
    private List<ActividadDiariaDTO> actividadDiaria;
    private List<Auditoria> ultimosEventosSeguridad;
    private List<IpCountDTO> topIpsHoy;

    public AuditorDashboardDTO() {}

    public AuditorDashboardDTO(long eventosHoy, long eventoSemana, long eventosMes,
                               List<ActividadDiariaDTO> actividadDiaria,
                               List<Auditoria> ultimosEventosSeguridad,
                               List<IpCountDTO> topIpsHoy) {
        this.eventosHoy = eventosHoy;
        this.eventoSemana = eventoSemana;
        this.eventosMes = eventosMes;
        this.actividadDiaria = actividadDiaria;
        this.ultimosEventosSeguridad = ultimosEventosSeguridad;
        this.topIpsHoy = topIpsHoy;
    }

    public long getEventosHoy() { return eventosHoy; }
    public void setEventosHoy(long eventosHoy) { this.eventosHoy = eventosHoy; }
    public long getEventoSemana() { return eventoSemana; }
    public void setEventoSemana(long eventoSemana) { this.eventoSemana = eventoSemana; }
    public long getEventosMes() { return eventosMes; }
    public void setEventosMes(long eventosMes) { this.eventosMes = eventosMes; }
    public List<ActividadDiariaDTO> getActividadDiaria() { return actividadDiaria; }
    public void setActividadDiaria(List<ActividadDiariaDTO> actividadDiaria) { this.actividadDiaria = actividadDiaria; }
    public List<Auditoria> getUltimosEventosSeguridad() { return ultimosEventosSeguridad; }
    public void setUltimosEventosSeguridad(List<Auditoria> ultimosEventosSeguridad) { this.ultimosEventosSeguridad = ultimosEventosSeguridad; }
    public List<IpCountDTO> getTopIpsHoy() { return topIpsHoy; }
    public void setTopIpsHoy(List<IpCountDTO> topIpsHoy) { this.topIpsHoy = topIpsHoy; }
}
