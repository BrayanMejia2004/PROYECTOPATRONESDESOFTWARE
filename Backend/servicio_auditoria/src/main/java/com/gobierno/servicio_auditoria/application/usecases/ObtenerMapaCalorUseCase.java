package com.gobierno.servicio_auditoria.application.usecases;

import com.gobierno.servicio_auditoria.domain.ports.in.ObtenerMapaCalorPort;
import com.gobierno.servicio_auditoria.domain.ports.out.IpEstadisticasRepositoryPort;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.IpDetalleEventoDTO;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.IpEstadisticaDTO;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ObtenerMapaCalorUseCase implements ObtenerMapaCalorPort {

    private final IpEstadisticasRepositoryPort ipEstadisticasRepositoryPort;

    public ObtenerMapaCalorUseCase(IpEstadisticasRepositoryPort ipEstadisticasRepositoryPort) {
        this.ipEstadisticasRepositoryPort = ipEstadisticasRepositoryPort;
    }

    @Override
    public List<IpEstadisticaDTO> obtenerEstadisticasPorIp(LocalDate desde, LocalDate hasta) {
        LocalDateTime desdeDt = (desde != null) ? desde.atStartOfDay() : null;
        LocalDateTime hastaDt = (hasta != null) ? hasta.atTime(LocalTime.MAX) : null;

        List<Object[]> resultados = ipEstadisticasRepositoryPort.agruparPorIp(desdeDt, hastaDt);

        if (resultados == null || resultados.isEmpty()) {
            return new ArrayList<>();
        }

        long maxEventos = 0;
        for (Object[] row : resultados) {
            Long total = ((Number) row[1]).longValue();
            if (total > maxEventos) {
                maxEventos = total;
            }
        }

        List<IpEstadisticaDTO> estadisticas = new ArrayList<>();
        for (Object[] row : resultados) {
            String ipOrigen = (String) row[0];
            Long totalEventos = ((Number) row[1]).longValue();
            Long totalUsuarios = ((Number) row[2]).longValue();
            LocalDateTime primeraVez = row[3] != null ? ((Timestamp) row[3]).toLocalDateTime() : null;
            LocalDateTime ultimaVez = row[4] != null ? ((Timestamp) row[4]).toLocalDateTime() : null;

            Integer nivelIntensidad = (maxEventos > 0)
                    ? (int) Math.ceil((totalEventos * 10.0) / maxEventos)
                    : 0;
            Boolean esSospechosa = totalUsuarios > 3;

            estadisticas.add(new IpEstadisticaDTO(ipOrigen, totalEventos, totalUsuarios,
                    primeraVez, ultimaVez, nivelIntensidad, esSospechosa));
        }

        return estadisticas;
    }

    @Override
    public List<IpDetalleEventoDTO> obtenerDetalleDeIp(String ip, int limite) {
        List<Object[]> resultados = ipEstadisticasRepositoryPort.obtenerEventosPorIp(ip, limite);

        if (resultados == null || resultados.isEmpty()) {
            return new ArrayList<>();
        }

        List<IpDetalleEventoDTO> detalle = new ArrayList<>();
        for (Object[] row : resultados) {
            Long usuarioId = row[0] != null ? ((Number) row[0]).longValue() : null;
            String accion = (String) row[1];
            String descripcion = (String) row[2];
            LocalDateTime fecha = row[3] != null ? ((Timestamp) row[3]).toLocalDateTime() : null;
            String tipo = (String) row[4];

            detalle.add(new IpDetalleEventoDTO(usuarioId, accion, descripcion, fecha, tipo));
        }

        return detalle;
    }
}
