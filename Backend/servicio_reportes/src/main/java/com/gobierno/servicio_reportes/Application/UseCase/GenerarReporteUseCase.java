package com.gobierno.servicio_reportes.Application.UseCase;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_reportes.Domain.ReporteComponent;
import com.gobierno.servicio_reportes.Domain.ReporteConcreteComponent;
import com.gobierno.servicio_reportes.Domain.ReporteData;
import com.gobierno.servicio_reportes.Domain.Decorator.CsvDecorator;
import com.gobierno.servicio_reportes.Domain.Decorator.PdfDecorator;
import com.gobierno.servicio_reportes.Domain.Decorator.ZipDecorator;
import com.gobierno.servicio_reportes.Infrastructure.Client.AuditoriaClient;
import com.gobierno.servicio_reportes.Infrastructure.Client.AutorizacionClient;
import com.gobierno.servicio_reportes.Infrastructure.Client.IdentidadClient;

// Caso de uso que orquesta la generacion de reportes
// Aplica el patron Decorator para formatear el reporte
@Service
public class GenerarReporteUseCase {
    
    // Clients para obtener datos de otros servicios
    private final AuditoriaClient auditoriaClient;
    private final IdentidadClient identidadClient;
    private final AutorizacionClient autorizacionClient;
    
    // Constructor con inyeccion de dependencias
    public GenerarReporteUseCase() {
        this.auditoriaClient = new AuditoriaClient();
        this.identidadClient = new IdentidadClient();
        this.autorizacionClient = new AutorizacionClient();
    }
    
    // Ejecuta la generacion del reporte segun tipo y formato
    public byte[] ejecutar(String tipo, String formato) {
        
        // Obtener datos segun el tipo
        ReporteData datos = obtenerDatos(tipo);
        
        // Crear reporte base
        ReporteComponent reporte = new ReporteConcreteComponent();
        
        // Aplicar decorators segun formato
        reporte = aplicarDecorators(reporte, formato);
        
        // Generar y retornar
        return reporte.generar(datos);
    }
    
    // Obtiene los datos segun el tipo de reporte
    private ReporteData obtenerDatos(String tipo) {
        return switch (tipo.toUpperCase()) {
            case "AUDITORIA" -> auditoriaClient.obtenerDatosAuditoria();
            case "USUARIOS" -> identidadClient.obtenerDatosUsuarios();
            case "ROLES" -> autorizacionClient.obtenerDatosRoles();
            default -> throw new IllegalArgumentException("Tipo de reporte invalido: " + tipo);
        };
    }
    
    // Aplica los decorators segun el formato solicitado
    private ReporteComponent aplicarDecorators(ReporteComponent reporte, String formato) {
        String formatoUpper = formato.toUpperCase();
        
        // Determinar si necesita compression ZIP
        boolean conZip = formatoUpper.contains("ZIP");
        
        // Determinar formato base
        if (formatoUpper.contains("CSV")) {
            reporte = new CsvDecorator(reporte);
        } else if (formatoUpper.contains("PDF")) {
            reporte = new PdfDecorator(reporte);
        }
        
        // Aplicar ZIP si se pidio
        if (conZip) {
            reporte = new ZipDecorator(reporte);
        }
        
        return reporte;
    }
}
