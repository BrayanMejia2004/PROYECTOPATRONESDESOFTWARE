package com.gobierno.servicio_reportes.domain.composite;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Sección de pie de reporte.
 * Muestra marca de fin, timestamp y total de registros.
 */
public class SeccionPie extends SeccionSimple {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public SeccionPie() {
        super("PIE", 4);
    }
    
    @Override
    public byte[] generar(ReporteData datos) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n");
        sb.append(repetirCaracter('=', 60)).append("\n");
        sb.append("FIN DEL REPORTE\n");
        sb.append("Generado el: ").append(LocalDateTime.now().format(FORMATTER)).append("\n");
        sb.append("Total de registros mostrados: ").append(datos.getFilas() != null ? datos.getFilas().length : 0).append("\n");
        sb.append(repetirCaracter('=', 60)).append("\n");
        sb.append("Sistema de Gestion de Identidad Digital\n");
        sb.append("Gobierno - 2026\n");
        
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
