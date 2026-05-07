package com.gobierno.servicio_auditoria.infrastructure.persistence.adapter;

import com.gobierno.servicio_auditoria.domain.ports.out.IpEstadisticasRepositoryPort;
import com.gobierno.servicio_auditoria.infrastructure.persistence.repository.IpEstadisticasRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class IpEstadisticasRepositoryAdapter implements IpEstadisticasRepositoryPort {

    private final IpEstadisticasRepository ipEstadisticasRepository;

    public IpEstadisticasRepositoryAdapter(IpEstadisticasRepository ipEstadisticasRepository) {
        this.ipEstadisticasRepository = ipEstadisticasRepository;
    }

    @Override
    public List<Object[]> agruparPorIp(LocalDateTime desde, LocalDateTime hasta) {
        return ipEstadisticasRepository.agruparPorIp(desde, hasta);
    }

    @Override
    public List<Object[]> obtenerEventosPorIp(String ip, int limite) {
        return ipEstadisticasRepository.obtenerEventosPorIp(ip, limite);
    }
}
