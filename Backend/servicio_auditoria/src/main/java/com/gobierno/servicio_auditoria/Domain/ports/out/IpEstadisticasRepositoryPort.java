package com.gobierno.servicio_auditoria.domain.ports.out;

import java.time.LocalDateTime;
import java.util.List;

public interface IpEstadisticasRepositoryPort {
    List<Object[]> agruparPorIp(LocalDateTime desde, LocalDateTime hasta);
    List<Object[]> obtenerEventosPorIp(String ip, int limite);
}
