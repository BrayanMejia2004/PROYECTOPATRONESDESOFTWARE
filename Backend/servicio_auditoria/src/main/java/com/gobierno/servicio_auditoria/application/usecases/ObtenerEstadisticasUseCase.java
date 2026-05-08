package com.gobierno.servicio_auditoria.application.usecases;

import com.gobierno.servicio_auditoria.domain.model.EstadisticasDTO;
import com.gobierno.servicio_auditoria.domain.model.UsuarioActivoDTO;
import com.gobierno.servicio_auditoria.domain.model.UsuarioInactivoDTO;
import com.gobierno.servicio_auditoria.domain.ports.in.ObtenerEstadisticasPort;
import com.gobierno.servicio_auditoria.domain.ports.out.EstadisticasRepositoryPort;
import com.gobierno.servicio_auditoria.domain.ports.out.IdentidadClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ObtenerEstadisticasUseCase implements ObtenerEstadisticasPort {

    private static final Logger log = LoggerFactory.getLogger(ObtenerEstadisticasUseCase.class);

    private final EstadisticasRepositoryPort estadisticasRepositoryPort;
    private final IdentidadClientPort identidadClientPort;

    public ObtenerEstadisticasUseCase(EstadisticasRepositoryPort estadisticasRepositoryPort,
                                      IdentidadClientPort identidadClientPort) {
        this.estadisticasRepositoryPort = estadisticasRepositoryPort;
        this.identidadClientPort = identidadClientPort;
    }

    @Override
    public EstadisticasDTO obtenerEstadisticas() {
        Map<String, Long> eventosPorTipo = estadisticasRepositoryPort.contarEventosPorTipo();
        Map<Integer, Long> actividadPorHora = estadisticasRepositoryPort.contarEventosPorHora();

        List<UsuarioActivoDTO> top5 = new ArrayList<>();
        List<Object[]> top5Raw = estadisticasRepositoryPort.obtenerTop5Activos();
        Map<Long, String> mapaUsuarios = identidadClientPort.obtenerMapaUsuarios();
        for (Object[] row : top5Raw) {
            Long usuarioId = row[0] != null ? ((Number) row[0]).longValue() : 0L;
            Long total = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            String username = mapaUsuarios.get(usuarioId);
            if (username != null) {
                top5.add(new UsuarioActivoDTO(usuarioId, username, total));
            }
        }

        List<UsuarioInactivoDTO> usuariosSinActividad = calcularUsuariosSinActividad();

        return new EstadisticasDTO(eventosPorTipo, actividadPorHora, top5, usuariosSinActividad);
    }

    private List<UsuarioInactivoDTO> calcularUsuariosSinActividad() {
        try {
            List<Long> todosLosIds = identidadClientPort.obtenerTodosLosUsuarioIds();
            if (todosLosIds == null || todosLosIds.isEmpty()) {
                return new ArrayList<>();
            }

            List<Integer> idsConActividad = estadisticasRepositoryPort.obtenerTodosLosUsuarioIds();
            Set<Long> idsConActividadSet = new HashSet<>();
            for (Integer id : idsConActividad) {
                if (id != null) {
                    idsConActividadSet.add(id.longValue());
                }
            }

            List<Long> idsSinActividad = todosLosIds.stream()
                    .filter(id -> !idsConActividadSet.contains(id))
                    .collect(Collectors.toList());

            if (idsSinActividad.isEmpty()) {
                return new ArrayList<>();
            }

            Map<Long, String> mapaUsuarios = identidadClientPort.obtenerMapaUsuarios();
            Map<String, List<String>> rolesPorUsuario = identidadClientPort.obtenerRolesPorUsuario();

            List<UsuarioInactivoDTO> resultado = new ArrayList<>();
            for (Long id : idsSinActividad) {
                String username = mapaUsuarios.get(id);
                if (username == null) continue;
                List<String> roles = rolesPorUsuario.getOrDefault(username, List.of());
                resultado.add(new UsuarioInactivoDTO(id, username, roles));
            }

            return resultado;
        } catch (Exception e) {
            log.warn("No se pudieron calcular usuarios sin actividad: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
