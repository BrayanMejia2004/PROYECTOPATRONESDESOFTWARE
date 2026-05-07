package com.gobierno.servicio_auditoria.application.usecases;

import com.gobierno.servicio_auditoria.domain.model.ActividadDiariaDTO;
import com.gobierno.servicio_auditoria.domain.model.AuditorDashboardDTO;
import com.gobierno.servicio_auditoria.domain.model.IpCountDTO;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.ports.in.ObtenerAuditorDashboardPort;
import com.gobierno.servicio_auditoria.domain.ports.out.AuditorDashboardRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ObtenerAuditorDashboardUseCase implements ObtenerAuditorDashboardPort {

    private final AuditorDashboardRepositoryPort repositoryPort;

    public ObtenerAuditorDashboardUseCase(AuditorDashboardRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public AuditorDashboardDTO obtenerDashboard() {
        LocalDateTime inicioHoy = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
        LocalDateTime inicioSemana = inicioHoy.minusDays(7);
        LocalDateTime inicioMes = inicioHoy.minusDays(30);

        long eventosHoy = repositoryPort.contarEventosDesde(inicioHoy);
        long eventoSemana = repositoryPort.contarEventosDesde(inicioSemana);
        long eventosMes = repositoryPort.contarEventosDesde(inicioMes);

        List<ActividadDiariaDTO> actividadDiaria = new ArrayList<>();
        for (Object[] row : repositoryPort.actividadDiaria(inicioMes)) {
            LocalDate fecha = row[0] != null ? ((java.sql.Date) row[0]).toLocalDate() : null;
            Long total = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            if (fecha != null) {
                actividadDiaria.add(new ActividadDiariaDTO(fecha, total));
            }
        }

        List<Auditoria> ultimosSeguridad = repositoryPort.ultimosEventosSeguridad(10);

        List<IpCountDTO> topIps = new ArrayList<>();
        for (Object[] row : repositoryPort.topIpsHoy(inicioHoy)) {
            String ip = (String) row[0];
            Long total = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            if (ip != null) {
                topIps.add(new IpCountDTO(ip, total));
            }
        }

        return new AuditorDashboardDTO(eventosHoy, eventoSemana, eventosMes,
                actividadDiaria, ultimosSeguridad, topIps);
    }
}
