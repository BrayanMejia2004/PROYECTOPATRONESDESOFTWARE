package com.gobierno.servicio_auditoria.application.usecases;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.model.AnalisisRequestDTO;
import com.gobierno.servicio_auditoria.domain.model.AnalisisResponseDTO;
import com.gobierno.servicio_auditoria.infrastructure.persistence.repository.AuditoriaJpaRepository;

@Service
public class ObtenerAnalisisUseCase {

    private final AuditoriaJpaRepository auditoriaJpaRepository;

    public ObtenerAnalisisUseCase(AuditoriaJpaRepository auditoriaJpaRepository) {
        this.auditoriaJpaRepository = auditoriaJpaRepository;
    }

    public AnalisisResponseDTO ejecutarAnalisis(AnalisisRequestDTO request) {
        List<Auditoria> todos = auditoriaJpaRepository.findAll();

        List<Auditoria> filtrados = filtrar(todos, request.getFechaDesde(), request.getFechaHasta(),
                request.getFiltros());

        switch (request.getEstrategia()) {
            case "VOLUMEN":
                return analisisVolumen(filtrados);
            case "SEVERIDAD":
                return analisisSeveridad(filtrados);
            case "TENDENCIA":
                return analisisTendencia(filtrados);
            case "COMPARATIVA":
                return analisisComparativa(filtrados);
            default:
                return new AnalisisResponseDTO(request.getEstrategia(), new ArrayList<>(), new HashMap<>(),
                        List.of("Estrategia no reconocida: " + request.getEstrategia()));
        }
    }

    public List<Map<String, Object>> listarEstrategias() {
        List<Map<String, Object>> lista = new ArrayList<>();

        Map<String, Object> volumen = new LinkedHashMap<>();
        volumen.put("tipo", "VOLUMEN");
        volumen.put("nombre", "Volumen de Actividad");
        volumen.put("descripcion", "Eventos agrupados por día y hora para identificar picos de actividad");
        lista.add(volumen);

        Map<String, Object> severidad = new LinkedHashMap<>();
        severidad.put("tipo", "SEVERIDAD");
        severidad.put("nombre", "Severidad de Eventos");
        severidad.put("descripcion", "Clasificación de eventos por tipo de auditoría y nivel de criticidad");
        lista.add(severidad);

        Map<String, Object> tendencia = new LinkedHashMap<>();
        tendencia.put("tipo", "TENDENCIA");
        tendencia.put("nombre", "Tendencia Temporal");
        tendencia.put("descripcion", "Comparación entre períodos consecutivos para detectar tendencias al alza o baja");
        lista.add(tendencia);

        Map<String, Object> comparativa = new LinkedHashMap<>();
        comparativa.put("tipo", "COMPARATIVA");
        comparativa.put("nombre", "Ranking Comparativo");
        comparativa.put("descripcion", "Usuarios más activos y acciones más frecuentes en el período seleccionado");
        lista.add(comparativa);

        return lista;
    }

    public List<String> obtenerInsights() {
        List<Auditoria> todos = auditoriaJpaRepository.findAll();
        List<String> insights = new ArrayList<>();

        long total = todos.size();
        if (total == 0) {
            insights.add("No hay eventos de auditoría registrados en el sistema");
            return insights;
        }

        long seguridadCount = todos.stream().filter(a -> "SEGURIDAD".equalsIgnoreCase(a.getTipo())).count();
        if (seguridadCount > 0) {
            double pct = (seguridadCount * 100.0) / total;
            insights.add(String.format("Eventos de seguridad: %d (%.1f%% del total)", seguridadCount, pct));
        }

        Map<String, Long> accionCount = todos.stream()
                .collect(Collectors.groupingBy(a -> a.getAccion() != null ? a.getAccion() : "DESCONOCIDA",
                        Collectors.counting()));
        if (!accionCount.isEmpty()) {
            Map.Entry<String, Long> topAccion = accionCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue()).get();
            double pctAccion = (topAccion.getValue() * 100.0) / total;
            insights.add(String.format("La acción más frecuente es %s con %d ocurrencias (%.1f%% del total)",
                    topAccion.getKey(), topAccion.getValue(), pctAccion));
        }

        long simulados = todos.stream().filter(a -> a.getSimulacion_id() != null).count();
        if (simulados > 0) {
            insights.add(String.format("Eventos simulados detectados: %d (%.1f%% del total)", simulados,
                    (simulados * 100.0) / total));
        }

        return insights;
    }

    private List<Auditoria> filtrar(List<Auditoria> lista, LocalDate desde, LocalDate hasta,
            Map<String, String> filtros) {
        return lista.stream()
                .filter(a -> {
                    if (desde != null && a.getFecha() != null) {
                        LocalDate fechaEvento = a.getFecha().toLocalDateTime().toLocalDate();
                        if (fechaEvento.isBefore(desde))
                            return false;
                    }
                    if (hasta != null && a.getFecha() != null) {
                        LocalDate fechaEvento = a.getFecha().toLocalDateTime().toLocalDate();
                        if (fechaEvento.isAfter(hasta))
                            return false;
                    }
                    if (filtros != null) {
                        if (filtros.containsKey("usuario") && !filtros.get("usuario").isBlank()
                                && a.getUsuario_id() != null) {
                            try {
                                long uid = Long.parseLong(filtros.get("usuario"));
                                if (a.getUsuario_id() != uid)
                                    return false;
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        }
                        if (filtros.containsKey("accion") && !filtros.get("accion").isBlank()
                                && a.getAccion() != null) {
                            if (!a.getAccion().toLowerCase().contains(filtros.get("accion").toLowerCase()))
                                return false;
                        }
                        if (filtros.containsKey("tipo") && !filtros.get("tipo").isBlank()
                                && a.getTipo() != null) {
                            if (!a.getTipo().equalsIgnoreCase(filtros.get("tipo")))
                                return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private AnalisisResponseDTO analisisVolumen(List<Auditoria> eventos) {
        Map<String, Long> conteo = eventos.stream()
                .filter(a -> a.getFecha() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getFecha().toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        LinkedHashMap::new,
                        Collectors.counting()));

        List<Map<String, Object>> datos = new ArrayList<>();
        for (Map.Entry<String, Long> entry : conteo.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("fecha", entry.getKey());
            item.put("total", entry.getValue());
            datos.add(item);
        }

        Map<String, Object> metricas = new LinkedHashMap<>();
        metricas.put("total", eventos.size());
        metricas.put("diasConActividad", datos.size());
        metricas.put("promedioDiario", datos.size() > 0 ? Math.round((double) eventos.size() / datos.size()) : 0);

        List<String> insights = new ArrayList<>();
        if (!datos.isEmpty()) {
            Map<String, Object> maxItem = datos.stream()
                    .max((a, b) -> Long.compare((Long) a.get("total"), (Long) b.get("total")))
                    .orElse(null);
            if (maxItem != null) {
                insights.add("Pico de actividad detectado: " + maxItem.get("fecha") + " con "
                        + maxItem.get("total") + " eventos");
            }
        }

        return new AnalisisResponseDTO("VOLUMEN", datos, metricas, insights);
    }

    private AnalisisResponseDTO analisisSeveridad(List<Auditoria> eventos) {
        Map<String, Long> porTipo = eventos.stream()
                .filter(a -> a.getTipo() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getTipo().toUpperCase(),
                        LinkedHashMap::new,
                        Collectors.counting()));

        List<Map<String, Object>> datos = new ArrayList<>();
        for (Map.Entry<String, Long> entry : porTipo.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("tipo", entry.getKey());
            item.put("total", entry.getValue());
            datos.add(item);
        }

        Map<String, Object> metricas = new LinkedHashMap<>();
        metricas.put("total", eventos.size());
        long seguridad = porTipo.getOrDefault("SEGURIDAD", 0L);
        metricas.put("porcentajeSeguridad",
                eventos.size() > 0 ? Math.round((seguridad * 100.0) / eventos.size()) : 0);

        List<String> insights = new ArrayList<>();
        if (seguridad > 0) {
            insights.add("Eventos de seguridad representan el "
                    + metricas.get("porcentajeSeguridad") + "% del total analizado");
        }

        return new AnalisisResponseDTO("SEVERIDAD", datos, metricas, insights);
    }

    private AnalisisResponseDTO analisisTendencia(List<Auditoria> eventos) {
        List<Auditoria> ordenados = eventos.stream()
                .filter(a -> a.getFecha() != null)
                .sorted((a, b) -> a.getFecha().compareTo(b.getFecha()))
                .collect(Collectors.toList());

        Map<String, Long> porSemana = new LinkedHashMap<>();
        DateTimeFormatter semanaFormatter = DateTimeFormatter.ofPattern("yyyy-'W'ww");

        for (Auditoria a : ordenados) {
            String semana = a.getFecha().toLocalDateTime().format(semanaFormatter);
            porSemana.merge(semana, 1L, Long::sum);
        }

        List<Map<String, Object>> datos = new ArrayList<>();
        List<Long> valores = new ArrayList<>(porSemana.values());

        int idx = 0;
        for (Map.Entry<String, Long> entry : porSemana.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("periodo", entry.getKey());
            item.put("actual", entry.getValue());
            item.put("anterior", idx > 0 ? valores.get(idx - 1) : 0);
            item.put("variacion", idx > 0 && valores.get(idx - 1) > 0
                    ? Math.round(((entry.getValue() - valores.get(idx - 1)) * 100.0) / valores.get(idx - 1))
                    : 0);
            datos.add(item);
            idx++;
        }

        Map<String, Object> metricas = new LinkedHashMap<>();
        metricas.put("total", eventos.size());

        List<String> insights = new ArrayList<>();
        for (Map<String, Object> d : datos) {
            int variacion = ((Number) d.get("variacion")).intValue();
            if (variacion > 20) {
                insights.add("Tendencia al alza detectada en " + d.get("periodo") + ": +" + variacion + "% vs semana anterior");
            } else if (variacion < -20) {
                insights.add("Descenso significativo en " + d.get("periodo") + ": " + variacion + "% vs semana anterior");
            }
        }

        return new AnalisisResponseDTO("TENDENCIA", datos, metricas, insights);
    }

    private AnalisisResponseDTO analisisComparativa(List<Auditoria> eventos) {
        Map<Integer, Long> porUsuario = eventos.stream()
                .filter(a -> a.getUsuario_id() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getUsuario_id(),
                        LinkedHashMap::new,
                        Collectors.counting()));

        Map<String, Long> porAccion = eventos.stream()
                .filter(a -> a.getAccion() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getAccion(),
                        LinkedHashMap::new,
                        Collectors.counting()));

        List<Map<String, Object>> topUsuarios = porUsuario.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", e.getKey());
                    item.put("usuario", "Usuario #" + e.getKey());
                    item.put("total", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());

        List<Map<String, Object>> topAcciones = porAccion.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("accion", e.getKey());
                    item.put("total", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> metricas = new LinkedHashMap<>();
        metricas.put("total", eventos.size());
        metricas.put("usuariosUnicos", porUsuario.size());
        metricas.put("accionesUnicas", porAccion.size());
        metricas.put("topUsuarios", topUsuarios);
        metricas.put("topAcciones", topAcciones);

        List<String> insights = new ArrayList<>();
        if (!topUsuarios.isEmpty()) {
            Map<String, Object> topUser = topUsuarios.get(0);
            insights.add("Usuario más activo: " + topUser.get("usuario") + " con " + topUser.get("total") + " eventos");
        }
        if (!topAcciones.isEmpty()) {
            Map<String, Object> topAct = topAcciones.get(0);
            insights.add("Acción más frecuente: " + topAct.get("accion") + " (" + topAct.get("total") + " veces)");
        }

        List<Map<String, Object>> datos = new ArrayList<>();
        eventos.forEach(e -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("accion", e.getAccion());
            item.put("tipo", e.getTipo());
            item.put("usuarioId", e.getUsuario_id());
            datos.add(item);
        });

        return new AnalisisResponseDTO("COMPARATIVA", datos, metricas, insights);
    }
}
