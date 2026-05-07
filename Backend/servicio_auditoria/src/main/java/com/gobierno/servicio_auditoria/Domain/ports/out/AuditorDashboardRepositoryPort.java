package com.gobierno.servicio_auditoria.domain.ports.out;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import java.time.LocalDateTime;
import java.util.List;

public interface AuditorDashboardRepositoryPort {
    long contarEventosDesde(LocalDateTime desde);
    List<Object[]> actividadDiaria(LocalDateTime desde);
    List<Auditoria> ultimosEventosSeguridad(int limite);
    List<Object[]> topIpsHoy(LocalDateTime desde);
}
