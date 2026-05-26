package com.gobierno.servicio_auditoria.domain.ports.out;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.model.ActividadDiariaDTO;
import java.util.List;

public interface TimelineRepositoryPort {
    List<Auditoria> findByUsuarioIdOrderByFechaAsc(Long usuarioId, int limite);
    List<ActividadDiariaDTO> findActividadCalendario(Long usuarioId, int dias);
}
