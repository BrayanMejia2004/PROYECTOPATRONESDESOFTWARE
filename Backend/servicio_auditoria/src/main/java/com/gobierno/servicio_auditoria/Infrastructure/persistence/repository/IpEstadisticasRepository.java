package com.gobierno.servicio_auditoria.infrastructure.persistence.repository;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface IpEstadisticasRepository extends JpaRepository<Auditoria, Long> {

    @Query(value = """
        SELECT ip_origen, COUNT(*) AS total_eventos,
               COUNT(DISTINCT usuario_id) AS total_usuarios,
               MIN(fecha) AS primera_vez, MAX(fecha) AS ultima_vez
        FROM auditoria
        WHERE ip_origen IS NOT NULL
          AND fecha >= COALESCE(:desde, fecha)
          AND fecha <= COALESCE(:hasta, fecha)
        GROUP BY ip_origen
        ORDER BY total_eventos DESC
        """, nativeQuery = true)
    List<Object[]> agruparPorIp(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);

    @Query(value = """
        SELECT usuario_id, accion, descripcion, fecha, tipo
        FROM auditoria
        WHERE ip_origen = :ip
        ORDER BY fecha DESC
        LIMIT :limite
        """, nativeQuery = true)
    List<Object[]> obtenerEventosPorIp(
            @Param("ip") String ip,
            @Param("limite") int limite);
}
