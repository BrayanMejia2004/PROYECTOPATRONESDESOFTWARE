package com.gobierno.servicio_auditoria.domain.ports.out;

import java.util.List;
import java.util.Map;

public interface EstadisticasRepositoryPort {
    Map<String, Long> contarEventosPorTipo();
    Map<Integer, Long> contarEventosPorHora();
    List<Object[]> obtenerTop5Activos();
    List<Integer> obtenerTodosLosUsuarioIds();
}
