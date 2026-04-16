package com.gobierno.servicio_reportes.domain.composite;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Sección de encabezado para reportes.
 * Muestra título, fecha de generación y usuario solicitante.
 */
public class SeccionEncabezado extends SeccionSimple {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public SeccionEncabezado() {
        super("ENCABEZADO", 1);
    }
    
    @Override
    public byte[] generar(ReporteData datos) {
        StringBuilder sb = new StringBuilder();
        
        // Línea de título centrada
        sb.append(repetirCaracter('=', 60)).append("\n");
        sb.append("|").append(formatearCentrado(datos.getTitulo() != null ? datos.getTitulo() : "REPORTE", 58)).append("|\n");
        sb.append(repetirCaracter('=', 60)).append("\n");
        
        // Fecha y usuario
        sb.append("| Generado: ").append(formatearLinea(LocalDateTime.now().format(FORMATTER), 40));
        sb.append("Usuario: ").append(formatearLinea(datos.getUsuarioSolicitante() != null ? datos.getUsuarioSolicitante() : "SYSTEM", 12)).append("|\n");
        sb.append(repetirCaracter('=', 60)).append("\n");
        sb.append("\n");
        
        // Descripción opcional
        if (datos.getDescripcion() != null && !datos.getDescripcion().isBlank()) {
            sb.append("Descripcion: ").append(datos.getDescripcion()).append("\n");
            sb.append("\n");
        }
        
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
    
    // Centra el texto dentro del ancho especificado
    private String formatearCentrado(String texto, int ancho) {
        if (texto == null) texto = "";
        int espacios = (ancho - texto.length()) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < espacios; i++) {
            sb.append(' ');
        }
        sb.append(formatearLinea(texto, ancho));
        while (sb.length() < ancho + 1) {
            sb.append(' ');
        }
        return sb.toString();
    }
}
