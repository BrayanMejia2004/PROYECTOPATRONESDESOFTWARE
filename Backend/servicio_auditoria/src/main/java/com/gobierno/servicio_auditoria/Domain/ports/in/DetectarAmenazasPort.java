package com.gobierno.servicio_auditoria.domain.ports.in;

import java.time.LocalDate;
import java.util.List;
import com.gobierno.servicio_auditoria.domain.model.ThreatEventDTO;

public interface DetectarAmenazasPort {
    List<ThreatEventDTO> obtenerAmenazasActivas();
    List<ThreatEventDTO> obtenerHistorialAmenazas(LocalDate desde, LocalDate hasta);
    List<ThreatEventDTO> ejecutarDeteccion();
    void resolverAmenaza(Long id);
}