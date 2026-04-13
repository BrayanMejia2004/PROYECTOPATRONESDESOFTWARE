package com.gobierno.servicio_reportes.infrastructure.adapter.controller;

import com.gobierno.servicio_reportes.domain.entities.Reporte;
import com.gobierno.servicio_reportes.domain.ports.in.ConsultarReportesPort;
import com.gobierno.servicio_reportes.domain.ports.in.GenerarReportePort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/reportes")
public class ReporteController {
    
    private final GenerarReportePort generarReportePort;
    private final ConsultarReportesPort consultarReportesPort;
    
    public ReporteController(
            GenerarReportePort generarReportePort,
            ConsultarReportesPort consultarReportesPort) {
        this.generarReportePort = generarReportePort;
        this.consultarReportesPort = consultarReportesPort;
    }
    
    @GetMapping("/{tipo}")
    public ResponseEntity<byte[]> generarReporte(
            @PathVariable String tipo,
            @RequestParam(defaultValue = "PDF") String formato,
            @RequestParam(required = false) String usuario) {
        
        String usuarioSolicitante = (usuario != null && !usuario.isBlank()) ? usuario : "SYSTEM";
        byte[] contenido = generarReportePort.generarReporte(tipo, formato, usuarioSolicitante);
        
        String extension;
        MediaType mediaType;
        
        if (formato.toUpperCase().contains("ZIP")) {
            extension = ".zip";
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        } else if (formato.toUpperCase().contains("CSV")) {
            extension = ".csv";
            mediaType = MediaType.parseMediaType("text/csv");
        } else {
            extension = ".pdf";
            mediaType = MediaType.APPLICATION_PDF;
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDispositionFormData("attachment", "reporte_" + tipo.toLowerCase() + extension);
        
        return ResponseEntity.ok().headers(headers).body(contenido);
    }
    
    @GetMapping("/historial")
    public ResponseEntity<List<Reporte>> obtenerHistorial(
            @RequestParam(required = false) String tipo) {
        List<Reporte> reportes;
        if (tipo != null && !tipo.isBlank()) {
            reportes = consultarReportesPort.obtenerHistorialPorTipo(tipo);
        } else {
            reportes = consultarReportesPort.obtenerHistorial();
        }
        return ResponseEntity.ok(reportes);
    }
}
