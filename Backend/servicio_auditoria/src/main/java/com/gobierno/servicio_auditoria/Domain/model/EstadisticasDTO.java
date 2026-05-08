package com.gobierno.servicio_auditoria.domain.model;

import java.util.List;
import java.util.Map;

public class EstadisticasDTO {
    private Map<String, Long> eventosPorTipo;
    private Map<Integer, Long> actividadPorHora;
    private List<UsuarioActivoDTO> top5UsuariosActivos;
    private List<UsuarioInactivoDTO> usuariosSinActividad;

    public EstadisticasDTO() {}

    public EstadisticasDTO(Map<String, Long> eventosPorTipo, Map<Integer, Long> actividadPorHora,
                           List<UsuarioActivoDTO> top5UsuariosActivos, List<UsuarioInactivoDTO> usuariosSinActividad) {
        this.eventosPorTipo = eventosPorTipo;
        this.actividadPorHora = actividadPorHora;
        this.top5UsuariosActivos = top5UsuariosActivos;
        this.usuariosSinActividad = usuariosSinActividad;
    }

    public Map<String, Long> getEventosPorTipo() { return eventosPorTipo; }
    public void setEventosPorTipo(Map<String, Long> eventosPorTipo) { this.eventosPorTipo = eventosPorTipo; }
    public Map<Integer, Long> getActividadPorHora() { return actividadPorHora; }
    public void setActividadPorHora(Map<Integer, Long> actividadPorHora) { this.actividadPorHora = actividadPorHora; }
    public List<UsuarioActivoDTO> getTop5UsuariosActivos() { return top5UsuariosActivos; }
    public void setTop5UsuariosActivos(List<UsuarioActivoDTO> top5UsuariosActivos) { this.top5UsuariosActivos = top5UsuariosActivos; }
    public List<UsuarioInactivoDTO> getUsuariosSinActividad() { return usuariosSinActividad; }
    public void setUsuariosSinActividad(List<UsuarioInactivoDTO> usuariosSinActividad) { this.usuariosSinActividad = usuariosSinActividad; }
}
