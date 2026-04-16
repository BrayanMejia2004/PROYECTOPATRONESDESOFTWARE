package com.gobierno.servicio_reportes.application.usecases;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gobierno.servicio_reportes.domain.entities.Reporte;
import com.gobierno.servicio_reportes.domain.ports.in.GenerarReportePort;
import com.gobierno.servicio_reportes.domain.ports.out.ReporteDataProviderPort;
import com.gobierno.servicio_reportes.domain.ports.out.ReporteRepositoryPort;
import com.gobierno.servicio_reportes.domain.services.CsvDecorator;
import com.gobierno.servicio_reportes.domain.services.PdfDecorator;
import com.gobierno.servicio_reportes.domain.services.ReporteComponent;
import com.gobierno.servicio_reportes.domain.services.ZipDecorator;
import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;
import com.gobierno.servicio_reportes.domain.composite.ReporteBuilder;
import com.gobierno.servicio_reportes.domain.composite.ReporteCompuesto;

@Service
public class GenerarReporteUseCase implements GenerarReportePort {
    
    private final ReporteRepositoryPort reporteRepository;
    private final ReporteDataProviderPort dataProvider;
    
    public GenerarReporteUseCase(
            ReporteRepositoryPort reporteRepository,
            ReporteDataProviderPort dataProvider) {
        this.reporteRepository = reporteRepository;
        this.dataProvider = dataProvider;
    }
    
    @Override
    @Transactional
    public byte[] generarReporte(String tipo, String formato, String usuarioSolicitante) {
        return generarReporte(tipo, formato, usuarioSolicitante, null, null, null, null, null, null);
    }
    
    @Transactional
    public byte[] generarReporte(String tipo, String formato, String usuarioSolicitante,
                                  Integer usuarioId, Timestamp fechaDesde, Timestamp fechaHasta,
                                  String accion, String tipoAuditoria) {
        return generarReporte(tipo, formato, usuarioSolicitante, usuarioId, fechaDesde, fechaHasta, accion, tipoAuditoria, null);
    }
    
    @Override
    @Transactional
    public byte[] generarReporte(String tipo, String formato, String usuarioSolicitante,
                                  Integer usuarioId, Timestamp fechaDesde, Timestamp fechaHasta,
                                  String accion, String tipoAuditoria, String secciones) {
        ReporteData datos = obtenerDatosConFiltros(tipo, usuarioId, fechaDesde, fechaHasta, accion, tipoAuditoria);
        datos.setUsuarioSolicitante(usuarioSolicitante);
        byte[] contenido = generarContenido(datos, formato, tipo, secciones);
        guardarReporte(tipo, datos.getTitulo(), datos.getDescripcion(), contenido, formato, usuarioSolicitante);
        return contenido;
    }
    
    private ReporteData obtenerDatosConFiltros(String tipo, Integer usuarioId, Timestamp fechaDesde, 
                                                Timestamp fechaHasta, String accion, String tipoAuditoria) {
        if ("AUDITORIA".equalsIgnoreCase(tipo)) {
            if (usuarioId != null || fechaDesde != null || fechaHasta != null || 
                (accion != null && !accion.isBlank()) || (tipoAuditoria != null && !tipoAuditoria.isBlank())) {
                return dataProvider.obtenerDatosAuditoriaFiltrado(usuarioId, fechaDesde, fechaHasta, tipoAuditoria, accion);
            }
        }
        return dataProvider.obtenerDatos(tipo);
    }
    
    @Override
    @Transactional
    public Reporte guardarReporte(String tipo, String titulo, String descripcion,
                                   byte[] contenido, String formato, String usuarioSolicitante) {
        String contenidoBase64 = Base64.getEncoder().encodeToString(contenido);
        Reporte reporte = new Reporte(
            tipo.toUpperCase(), titulo, descripcion, contenidoBase64,
            formato.toUpperCase(), usuarioSolicitante
        );
        return reporteRepository.guardar(reporte);
    }
    
    private byte[] generarContenido(ReporteData datos, String formato, String tipo, String secciones) {
        ReporteComponent reporte = construirReporteDinamico(secciones);
        reporte = aplicarDecorators(reporte, formato, tipo);
        return reporte.generar(datos);
    }
    
    private ReporteCompuesto construirReporteDinamico(String secciones) {
        ReporteBuilder builder = ReporteBuilder.crear("Reporte Dinamico");
        
        if (secciones == null || secciones.isBlank()) {
            return ReporteBuilder.reporteConResumen();
        }
        
        List<String> seccionesSeleccionadas = Arrays.asList(secciones.split(","));
        
        if (seccionesSeleccionadas.contains("ENCABEZADO")) {
            builder.conEncabezado();
        }
        if (seccionesSeleccionadas.contains("RESUMEN")) {
            builder.conResumen();
        }
        if (seccionesSeleccionadas.contains("DETALLE")) {
            builder.conDetalle();
        }
        if (seccionesSeleccionadas.contains("PIE")) {
            builder.conPie();
        }
        
        return builder.construir();
    }
    
    private ReporteComponent aplicarDecorators(ReporteComponent reporte, String formato, String tipo) {
        String formatoUpper = formato.toUpperCase();
        boolean conZip = formatoUpper.contains("ZIP");
        
        if (formatoUpper.contains("CSV")) {
            reporte = new CsvDecorator(reporte);
        } else if (formatoUpper.contains("PDF")) {
            reporte = new PdfDecorator(reporte);
        }
        if (conZip) {
            reporte = new ZipDecorator(reporte, tipo);
        }
        return reporte;
    }
}
