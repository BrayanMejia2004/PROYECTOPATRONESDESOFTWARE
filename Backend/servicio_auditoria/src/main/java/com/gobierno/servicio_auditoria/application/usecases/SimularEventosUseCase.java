package com.gobierno.servicio_auditoria.application.usecases;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.model.EscenarioSimulacionDTO;
import com.gobierno.servicio_auditoria.domain.model.EventoSimuladoDTO;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.SimularResponse;
import com.gobierno.servicio_auditoria.infrastructure.persistence.repository.AuditoriaJpaRepository;

@Service
public class SimularEventosUseCase {

    private final AuditoriaJpaRepository auditoriaJpaRepository;
    private static final String[] IPS_EJEMPLO = {
        "192.168.1.10", "10.0.0.5", "172.16.0.20", "200.100.50.1", "45.33.22.11"
    };
    private static final String[] ACCIONES = {
        "LOGIN", "REGISTRO_USUARIO", "CONSULTA", "ACTUALIZAR_PERFIL", "LOGOUT"
    };
    private static final Random random = new Random();

    public SimularEventosUseCase(AuditoriaJpaRepository auditoriaJpaRepository) {
        this.auditoriaJpaRepository = auditoriaJpaRepository;
    }

    public SimularResponse simularEvento(EventoSimuladoDTO dto) {
        String simulacionId = UUID.randomUUID().toString();
        Auditoria auditoria = construirAuditoria(dto, simulacionId);
        auditoriaJpaRepository.save(auditoria);
        return new SimularResponse(simulacionId, "Evento simulado exitosamente");
    }

    public SimularResponse simularLote(List<EventoSimuladoDTO> eventos) {
        String simulacionId = UUID.randomUUID().toString();
        List<Auditoria> auditorias = new ArrayList<>();
        for (EventoSimuladoDTO dto : eventos) {
            auditorias.add(construirAuditoria(dto, simulacionId));
        }
        auditoriaJpaRepository.saveAll(auditorias);
        return new SimularResponse(simulacionId, eventos.size() + " eventos simulados exitosamente");
    }

    public SimularResponse simularEscenario(EscenarioSimulacionDTO escenario) {
        String simulacionId = UUID.randomUUID().toString();
        List<Auditoria> auditorias = new ArrayList<>();
        for (EventoSimuladoDTO dto : escenario.getEventos()) {
            auditorias.add(construirAuditoria(dto, simulacionId));
        }
        auditoriaJpaRepository.saveAll(auditorias);
        return new SimularResponse(simulacionId,
                escenario.getEventos().size() + " eventos simulados para escenario: " + escenario.getNombre());
    }

    public void deshacerSimulacion(String simulacionId) {
        List<Auditoria> eventosSimulados = auditoriaJpaRepository.findAll().stream()
                .filter(a -> simulacionId.equals(a.getSimulacion_id()))
                .toList();
        auditoriaJpaRepository.deleteAll(eventosSimulados);
    }

    private Auditoria construirAuditoria(EventoSimuladoDTO dto, String simulacionId) {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(dto.getUsuarioId() != null ? dto.getUsuarioId() : random.nextInt(10) + 1);
        auditoria.setAccion(dto.getAccion());
        auditoria.setDescripcion(dto.getDescripcion());
        auditoria.setIp_origen(dto.getIpOrigen() != null ? dto.getIpOrigen() : IPS_EJEMPLO[random.nextInt(IPS_EJEMPLO.length)]);
        auditoria.setTipo(dto.getTipo() != null ? dto.getTipo() : "BASICA");
        auditoria.setFecha(parsearFecha(dto.getFecha()));
        auditoria.setSimulacion_id(simulacionId);
        return auditoria;
    }

    private Timestamp parsearFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.isBlank()) {
            return Timestamp.valueOf(LocalDateTime.now());
        }
        try {
            LocalDateTime fecha = LocalDateTime.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return Timestamp.valueOf(fecha);
        } catch (Exception e) {
            return Timestamp.valueOf(LocalDateTime.now());
        }
    }
}
