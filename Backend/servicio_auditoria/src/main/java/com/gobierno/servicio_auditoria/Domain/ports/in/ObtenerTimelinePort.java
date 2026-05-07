package com.gobierno.servicio_auditoria.domain.ports.in;

import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.TimelineEventoDTO;
import java.util.List;

public interface ObtenerTimelinePort {
    List<TimelineEventoDTO> obtenerTimelinePorUsuario(Long usuarioId, Integer limite);
}
