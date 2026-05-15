package com.gobierno.servicio_auditoria.domain.ports.out;

import java.time.LocalDateTime;
import java.util.List;

public interface ThreatRepositoryPort {
    List<Object[]> contarIntentosLoginPorIp(LocalDateTime desde);
    List<Object[]> contarEventosPorIp(LocalDateTime desde);
    List<Object[]> contarUsuariosDistintosPorIp(LocalDateTime desde);
    List<Object[]> eventosEnRangoHorario(LocalDateTime desde, LocalDateTime hasta);
    boolean ipUsadaPorUsuario(String ip, Integer usuarioId, LocalDateTime desde);
}