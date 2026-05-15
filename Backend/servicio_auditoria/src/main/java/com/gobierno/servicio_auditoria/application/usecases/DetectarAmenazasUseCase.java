package com.gobierno.servicio_auditoria.application.usecases;

import com.gobierno.servicio_auditoria.domain.model.ThreatEventDTO;
import com.gobierno.servicio_auditoria.domain.ports.in.DetectarAmenazasPort;
import com.gobierno.servicio_auditoria.domain.ports.out.ThreatRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DetectarAmenazasUseCase implements DetectarAmenazasPort {

    private static final Logger log = LoggerFactory.getLogger(DetectarAmenazasUseCase.class);
    private static final int VENTANA_FUERZA_BRUTA_MIN = 5;
    private static final int UMBRAL_FUERZA_BRUTA = 5;
    private static final int VENTANA_RAFAGA_MIN = 1;
    private static final int UMBRAL_RAFAGA = 20;
    private static final int VENTANA_IP_SOSPECHOSA_HORAS = 24;
    private static final int UMBRAL_USUARIOS_DISTINTOS = 3;
    private static final int HORA_INICIO_ATIPICO = 0;
    private static final int HORA_FIN_ATIPICO = 5;
    private static final int VENTANA_HORARIO_ATIPICO_DIAS = 7;

    private final ThreatRepositoryPort threatRepositoryPort;
    private final ConcurrentHashMap<Long, ThreatEventDTO> amenazasActivas = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ThreatEventDTO> historialAmenazas = new ConcurrentHashMap<>();
    private final Set<String> amenazasReportadas = new HashSet<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public DetectarAmenazasUseCase(ThreatRepositoryPort threatRepositoryPort) {
        this.threatRepositoryPort = threatRepositoryPort;
    }

    @Override
    public List<ThreatEventDTO> obtenerAmenazasActivas() {
        return new ArrayList<>(amenazasActivas.values());
    }

    @Override
    public List<ThreatEventDTO> obtenerHistorialAmenazas(LocalDate desde, LocalDate hasta) {
        if (desde == null && hasta == null) {
            return new ArrayList<>(historialAmenazas.values());
        }
        List<ThreatEventDTO> filtradas = new ArrayList<>();
        LocalDateTime desdeDt = (desde != null) ? desde.atStartOfDay() : LocalDateTime.MIN;
        LocalDateTime hastaDt = (hasta != null) ? hasta.atTime(23, 59, 59) : LocalDateTime.MAX;
        for (ThreatEventDTO t : historialAmenazas.values()) {
            if (t.getFecha() != null && !t.getFecha().isBefore(desdeDt) && !t.getFecha().isAfter(hastaDt)) {
                filtradas.add(t);
            }
        }
        return filtradas;
    }

    @Override
    public synchronized List<ThreatEventDTO> ejecutarDeteccion() {
        List<ThreatEventDTO> nuevasAmenazas = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();

        nuevasAmenazas.addAll(detectarFuerzaBruta(ahora));
        nuevasAmenazas.addAll(detectarRafaga(ahora));
        nuevasAmenazas.addAll(detectarIpSospechosa(ahora));
        nuevasAmenazas.addAll(detectarHorarioAtipico(ahora));

        for (ThreatEventDTO amenaza : nuevasAmenazas) {
            String clave = amenaza.getTipo() + ":" + amenaza.getIpOrigen() + ":" + amenaza.getUsuarioId();
            if (amenazasReportadas.add(clave)) {
                amenazasActivas.put(amenaza.getId(), amenaza);
                historialAmenazas.put(amenaza.getId(), amenaza);
                log.info("Amenaza detectada: {} (severidad={}, ip={})",
                        amenaza.getTipo(), amenaza.getSeveridad(), amenaza.getIpOrigen());
            }
        }

        return nuevasAmenazas.stream()
                .filter(t -> amenazasReportadas.contains(t.getTipo() + ":" + t.getIpOrigen() + ":" + t.getUsuarioId()))
                .toList();
    }

    @Override
    public void resolverAmenaza(Long id) {
        ThreatEventDTO amenaza = amenazasActivas.remove(id);
        if (amenaza != null) {
            amenaza.setActiva(false);
            log.info("Amenaza resuelta: id={}, tipo={}", id, amenaza.getTipo());
        }
    }

    private List<ThreatEventDTO> detectarFuerzaBruta(LocalDateTime ahora) {
        List<ThreatEventDTO> amenazas = new ArrayList<>();
        LocalDateTime desde = ahora.minusMinutes(VENTANA_FUERZA_BRUTA_MIN);
        List<Object[]> resultados = threatRepositoryPort.contarIntentosLoginPorIp(desde);
        for (Object[] row : resultados) {
            String ip = (String) row[0];
            Long total = ((Number) row[1]).longValue();
            if (total >= UMBRAL_FUERZA_BRUTA) {
                Map<String, Object> metricas = new HashMap<>();
                metricas.put("intentos", total);
                metricas.put("ventana_minutos", VENTANA_FUERZA_BRUTA_MIN);
                String severidad = calcularSeveridad(total, UMBRAL_FUERZA_BRUTA, 15, 30);
                amenazas.add(new ThreatEventDTO(
                        idGenerator.getAndIncrement(),
                        "FUERZA_BRUTA", severidad,
                        "Múltiples intentos de login desde " + ip + " (" + total + " en " + VENTANA_FUERZA_BRUTA_MIN + "min)",
                        null, ip, ahora, "LOGIN", metricas, true
                ));
            }
        }
        return amenazas;
    }

    private List<ThreatEventDTO> detectarRafaga(LocalDateTime ahora) {
        List<ThreatEventDTO> amenazas = new ArrayList<>();
        LocalDateTime desde = ahora.minusMinutes(VENTANA_RAFAGA_MIN);
        List<Object[]> resultados = threatRepositoryPort.contarEventosPorIp(desde);
        for (Object[] row : resultados) {
            String ip = (String) row[0];
            Long total = ((Number) row[1]).longValue();
            if (total >= UMBRAL_RAFAGA) {
                Map<String, Object> metricas = new HashMap<>();
                metricas.put("eventos", total);
                metricas.put("ventana_minutos", VENTANA_RAFAGA_MIN);
                String severidad = calcularSeveridad(total, UMBRAL_RAFAGA, 50, 100);
                amenazas.add(new ThreatEventDTO(
                        idGenerator.getAndIncrement(),
                        "RAFAGA", severidad,
                        "Ráfaga de actividad desde " + ip + " (" + total + " eventos en " + VENTANA_RAFAGA_MIN + "min)",
                        null, ip, ahora, "MULTIPLE", metricas, true
                ));
            }
        }
        return amenazas;
    }

    private List<ThreatEventDTO> detectarIpSospechosa(LocalDateTime ahora) {
        List<ThreatEventDTO> amenazas = new ArrayList<>();
        LocalDateTime desde = ahora.minusHours(VENTANA_IP_SOSPECHOSA_HORAS);
        List<Object[]> resultados = threatRepositoryPort.contarUsuariosDistintosPorIp(desde);
        for (Object[] row : resultados) {
            String ip = (String) row[0];
            Long totalUsuarios = ((Number) row[1]).longValue();
            if (totalUsuarios >= UMBRAL_USUARIOS_DISTINTOS) {
                Map<String, Object> metricas = new HashMap<>();
                metricas.put("usuarios_distintos", totalUsuarios);
                metricas.put("ventana_horas", VENTANA_IP_SOSPECHOSA_HORAS);
                String severidad = calcularSeveridad(totalUsuarios, UMBRAL_USUARIOS_DISTINTOS, 6, 10);
                amenazas.add(new ThreatEventDTO(
                        idGenerator.getAndIncrement(),
                        "IP_SOSPECHOSA", severidad,
                        "IP " + ip + " asociada a " + totalUsuarios + " usuarios distintos en 24h",
                        null, ip, ahora, "LOGIN", metricas, true
                ));
            }
        }
        return amenazas;
    }

    private List<ThreatEventDTO> detectarHorarioAtipico(LocalDateTime ahora) {
        List<ThreatEventDTO> amenazas = new ArrayList<>();
        LocalDateTime desde = ahora.minusDays(VENTANA_HORARIO_ATIPICO_DIAS);
        LocalDateTime hasta = ahora;
        List<Object[]> resultados = threatRepositoryPort.eventosEnRangoHorario(desde, hasta);
        for (Object[] row : resultados) {
            Integer usuarioId = row[0] != null ? ((Number) row[0]).intValue() : null;
            String ip = (String) row[1];
            if (usuarioId != null && ip != null) {
                LocalDateTime hace24h = ahora.minusHours(24);
                boolean ipNueva = !threatRepositoryPort.ipUsadaPorUsuario(ip, usuarioId, hace24h);
                if (ipNueva) {
                    Map<String, Object> metricas = new HashMap<>();
                    metricas.put("usuario_id", usuarioId);
                    metricas.put("ventana_dias", VENTANA_HORARIO_ATIPICO_DIAS);
                    amenazas.add(new ThreatEventDTO(
                            idGenerator.getAndIncrement(),
                            "HORARIO_ATIPICO", "MEDIA",
                            "Acceso de " + usuarioId + " desde IP no habitual " + ip + " en horario " +
                                    HORA_INICIO_ATIPICO + ":00-" + HORA_FIN_ATIPICO + ":00",
                            usuarioId, ip, ahora, "LOGIN", metricas, true
                    ));
                }
            }
        }
        return amenazas;
    }

    private String calcularSeveridad(long valor, long umbralMin, long umbralAlta, long umbralCritica) {
        if (valor >= umbralCritica) return "CRITICA";
        if (valor >= umbralAlta) return "ALTA";
        if (valor >= umbralMin) return "MEDIA";
        return "BAJA";
    }
}