package com.gobierno.servicio_auditoria.application.usecases;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.ports.in.ObtenerTimelinePort;
import com.gobierno.servicio_auditoria.domain.ports.out.TimelineRepositoryPort;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.TimelineEventoDTO;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObtenerTimelineUseCase implements ObtenerTimelinePort {

    private final TimelineRepositoryPort timelineRepositoryPort;

    public ObtenerTimelineUseCase(TimelineRepositoryPort timelineRepositoryPort) {
        this.timelineRepositoryPort = timelineRepositoryPort;
    }

    @Override
    public List<TimelineEventoDTO> obtenerTimelinePorUsuario(Long usuarioId, Integer limite) {
        int limiteReal = (limite != null && limite > 0) ? limite : 50;

        List<Auditoria> auditorias = timelineRepositoryPort.findByUsuarioIdOrderByFechaAsc(usuarioId, limiteReal);

        return auditorias.stream()
                .map(this::mapearATimelineEventoDTO)
                .collect(Collectors.toList());
    }

    private TimelineEventoDTO mapearATimelineEventoDTO(Auditoria auditoria) {
        String icono = determinarIcono(auditoria.getAccion());

        LocalDateTime fechaLocalDateTime = null;
        if (auditoria.getFecha() != null) {
            fechaLocalDateTime = auditoria.getFecha().toLocalDateTime();
        }

        return new TimelineEventoDTO(
                auditoria.getId(),
                auditoria.getAccion(),
                auditoria.getDescripcion(),
                fechaLocalDateTime,
                auditoria.getIp_origen(),
                auditoria.getTipo(),
                icono);
    }

    private String determinarIcono(String accion) {
        if (accion == null)
            return "info";

        return switch (accion.toUpperCase()) {
            case "LOGIN" -> "lock";
            case "REGISTRO_USUARIO" -> "person_add";
            case "REGISTRAR_PERFIL" -> "badge";
            case "ACTUALIZAR_PERFIL" -> "edit";
            case "ACTUALIZAR_USUARIO" -> "manage_accounts";
            case "ELIMINAR_USUARIO" -> "delete";
            case "EDITAR_USUARIO" -> "admin_panel_settings";
            case "CREAR_ROL" -> "star";
            case "ELIMINAR_ROL" -> "remove_circle";
            default -> "info";
        };
    }
}
