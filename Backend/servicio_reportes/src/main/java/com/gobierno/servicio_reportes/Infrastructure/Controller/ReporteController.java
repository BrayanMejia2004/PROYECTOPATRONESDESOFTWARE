package com.gobierno.servicio_reportes.Infrastructure.Controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_reportes.Application.UseCase.GenerarReporteUseCase;

// Controlador REST para generar reportes
@RestController
@RequestMapping("/reportes")
public class ReporteController {
    
    // Caso de uso para generar reportes
    private final GenerarReporteUseCase generarReporteUseCase;
    
    // Constructor
    public ReporteController() {
        this.generarReporteUseCase = new GenerarReporteUseCase();
    }
    
    // Endpoint para generar reportes
    // Ejemplo: GET /reportes/auditoria?formato=PDF
    @GetMapping("/{tipo}")
    public ResponseEntity<byte[]> generarReporte(
            @PathVariable String tipo,
            @RequestParam(defaultValue = "PDF") String formato) {
        
        // Genera el reporte
        byte[] contenido = generarReporteUseCase.ejecutar(tipo, formato);
        
        // Determina el tipo de contenido y extension
        String extension;
        MediaType mediaType;
        
        if (formato.toUpperCase().contains("ZIP")) {
            extension = ".zip";
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        } else if (formato.toUpperCase().contains("CSV")) {
            extension = ".csv";
            mediaType = MediaType.parseMediaType("text/csv");
        } else {
            extension = ".txt";
            mediaType = MediaType.TEXT_PLAIN;
        }
        
        // Configura las cabeceras de la respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDispositionFormData("attachment", 
            "reporte_" + tipo.toLowerCase() + extension);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(contenido);
    }
}
