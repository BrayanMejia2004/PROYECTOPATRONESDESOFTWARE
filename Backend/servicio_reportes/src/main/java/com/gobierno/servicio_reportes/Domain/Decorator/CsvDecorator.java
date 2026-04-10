package com.gobierno.servicio_reportes.Domain.Decorator;

import com.gobierno.servicio_reportes.Domain.ReporteComponent;
import com.gobierno.servicio_reportes.Domain.ReporteData;

// Concrete Decorator - Genera reporte en formato CSV
public class CsvDecorator extends ReporteBaseDecorator {
    
    public CsvDecorator(ReporteComponent reporte) {
        super(reporte);
    }
    
    @Override
    public byte[] generar(ReporteData datos) {
        return convertirACsv(datos);
    }
    
    // Convierte los datos a formato CSV
    private byte[] convertirACsv(ReporteData datos) {
        StringBuilder sb = new StringBuilder();
        
        try {
            // Agregar titulo como comentario
            sb.append("# ").append(datos.getTitulo()).append("\n");
            sb.append("# Tipo: ").append(datos.getTipo()).append("\n");
            sb.append("# Generado: ").append(java.time.LocalDateTime.now()).append("\n\n");
            
            // Agregar headers (primera linea)
            if (datos.getHeaders() != null) {
                for (int i = 0; i < datos.getHeaders().length; i++) {
                    sb.append(escapeCsv(datos.getHeaders()[i]));
                    if (i < datos.getHeaders().length - 1) {
                        sb.append(",");
                    }
                }
                sb.append("\n");
            }
            
            // Agregar filas de datos
            if (datos.getFilas() != null) {
                for (String[] fila : datos.getFilas()) {
                    for (int i = 0; i < fila.length; i++) {
                        sb.append(escapeCsv(fila[i]));
                        if (i < fila.length - 1) {
                            sb.append(",");
                        }
                    }
                    sb.append("\n");
                }
            }
            
            return sb.toString().getBytes("UTF-8");
            
        } catch (Exception e) {
            throw new RuntimeException("Error al generar CSV", e);
        }
    }
    
    // Escapa caracteres especiales para CSV
    private String escapeCsv(String valor) {
        if (valor == null) {
            return "";
        }
        // Si contiene coma, comilla o salto de linea, envolver en comillas
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
