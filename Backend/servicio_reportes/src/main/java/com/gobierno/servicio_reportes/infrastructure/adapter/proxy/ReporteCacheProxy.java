package com.gobierno.servicio_reportes.infrastructure.adapter.proxy;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.gobierno.servicio_reportes.domain.entities.Reporte;
import com.gobierno.servicio_reportes.domain.ports.in.GenerarReportePort;
import com.gobierno.servicio_reportes.domain.ports.out.ReporteCachePort;

@Component
@Primary
public class ReporteCacheProxy implements GenerarReportePort, ReporteCachePort {

    private static final long TTL_MINUTES = 5;
    private static final int MAX_SIZE = 100;

    private final ConcurrentHashMap<String, CacheEntry> cache;
    private final GenerarReportePort reporteUseCase;
    private final ScheduledExecutorService scheduler;

    public ReporteCacheProxy(GenerarReportePort reporteUseCase) {
        this.reporteUseCase = reporteUseCase;
        this.cache = new ConcurrentHashMap<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduler.scheduleAtFixedRate(this::limpiarExpirados, TTL_MINUTES, TTL_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public byte[] generarReporte(String tipo, String formato, String usuarioSolicitante) {
        return generarReporte(tipo, formato, usuarioSolicitante, null, null, null, null, null, null);
    }

    @Override
    public byte[] generarReporte(String tipo, String formato, String usuarioSolicitante,
            Integer usuarioId, Timestamp fechaDesde, Timestamp fechaHasta,
            String accion, String tipoAuditoria, String secciones) {
        String clave = generarClave(tipo, formato, usuarioId, fechaDesde, fechaHasta, accion, tipoAuditoria, secciones);

        byte[] contenido = obtener(clave);
        if (contenido != null) {
            return contenido;
        }

        contenido = reporteUseCase.generarReporte(tipo, formato, usuarioSolicitante,
                usuarioId, fechaDesde, fechaHasta, accion, tipoAuditoria, secciones);

        guardar(clave, contenido);

        return contenido;
    }

    @Override
    public Reporte guardarReporte(String tipo, String titulo, String descripcion,
            byte[] contenido, String formato, String usuarioSolicitante) {
        return reporteUseCase.guardarReporte(tipo, titulo, descripcion, contenido, formato, usuarioSolicitante);
    }

    private String generarClave(String tipo, String formato, Integer usuarioId,
            Timestamp fechaDesde, Timestamp fechaHasta,
            String accion, String tipoAuditoria, String secciones) {
        StringBuilder sb = new StringBuilder();
        sb.append(tipo != null ? tipo.toUpperCase() : "NULL");
        sb.append("_");
        sb.append(formato != null ? formato.toUpperCase() : "NULL");
        sb.append("_");
        sb.append(usuarioId != null ? usuarioId : "NULL");
        sb.append("_");
        sb.append(fechaDesde != null ? fechaDesde.toString() : "NULL");
        sb.append("_");
        sb.append(fechaHasta != null ? fechaHasta.toString() : "NULL");
        sb.append("_");
        sb.append(accion != null ? accion : "NULL");
        sb.append("_");
        sb.append(tipoAuditoria != null ? tipoAuditoria : "NULL");
        sb.append("_");
        sb.append(secciones != null ? secciones : "NULL");
        return sb.toString();
    }

    @Override
    public void guardar(String clave, byte[] contenido) {
        if (cache.size() >= MAX_SIZE) {
            evictLRU();
        }
        cache.put(clave, new CacheEntry(contenido, System.currentTimeMillis()));
    }

    @Override
    public byte[] obtener(String clave) {
        CacheEntry entry = cache.get(clave);
        if (entry == null) {
            return null;
        }

        if (estaExpirado(entry)) {
            cache.remove(clave);
            return null;
        }

        entry.actualizarUltimoAcceso();
        return entry.getContenido();
    }

    @Override
    public void eliminar(String clave) {
        cache.remove(clave);
    }

    @Override
    public void limpiar() {
        cache.clear();
    }

    @Override
    public int tamaño() {
        return cache.size();
    }

    private boolean estaExpirado(CacheEntry entry) {
        long tiempoActual = System.currentTimeMillis();
        long tiempoExpiracion = entry.getTimestamp() + (TTL_MINUTES * 60 * 1000);
        return tiempoActual > tiempoExpiracion;
    }

    private void limpiarExpirados() {
        Iterator<Map.Entry<String, CacheEntry>> iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, CacheEntry> entry = iterator.next();
            if (estaExpirado(entry.getValue())) {
                iterator.remove();
            }
        }
    }

    private void evictLRU() {
        if (cache.isEmpty()) {
            return;
        }

        String claveLRU = null;
        long tiempoMinimo = Long.MAX_VALUE;

        for (Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
            long tiempoAcceso = entry.getValue().getUltimoAcceso();
            if (tiempoAcceso < tiempoMinimo) {
                tiempoMinimo = tiempoAcceso;
                claveLRU = entry.getKey();
            }
        }

        if (claveLRU != null) {
            cache.remove(claveLRU);
        }
    }

    private static class CacheEntry {
        private final byte[] contenido;
        private final long timestamp;
        private long ultimoAcceso;

        public CacheEntry(byte[] contenido, long timestamp) {
            this.contenido = contenido;
            this.timestamp = timestamp;
            this.ultimoAcceso = timestamp;
        }

        public byte[] getContenido() {
            return contenido;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public long getUltimoAcceso() {
            return ultimoAcceso;
        }

        public void actualizarUltimoAcceso() {
            this.ultimoAcceso = System.currentTimeMillis();
        }
    }
}