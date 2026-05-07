package com.gobierno.servicio_auditoria.domain.ports.in;

import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.IpDetalleEventoDTO;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.IpEstadisticaDTO;
import java.time.LocalDate;
import java.util.List;

public interface ObtenerMapaCalorPort {
    List<IpEstadisticaDTO> obtenerEstadisticasPorIp(LocalDate desde, LocalDate hasta);
    List<IpDetalleEventoDTO> obtenerDetalleDeIp(String ip, int limite);
}
