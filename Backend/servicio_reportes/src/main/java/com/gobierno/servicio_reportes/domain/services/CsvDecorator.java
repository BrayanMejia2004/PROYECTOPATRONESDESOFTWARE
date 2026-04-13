package com.gobierno.servicio_reportes.domain.services;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

public class CsvDecorator extends ReporteBaseDecorator {
    
    public CsvDecorator(ReporteComponent reporte) {
        super(reporte);
    }
    
    @Override
    public byte[] generar(ReporteData datos) {
        return convertirACsv(datos);
    }
    
    private byte[] convertirACsv(ReporteData datos) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("# Reporte: ").append(datos.getTitulo()).append("\n");
            sb.append("# Generado: ").append(java.time.LocalDateTime.now()).append("\n");
            if (datos.getDescripcion() != null) sb.append("# ").append(datos.getDescripcion()).append("\n");
            sb.append("\n");
            if (datos.getHeaders() != null) {
                for (int i = 0; i < datos.getHeaders().length; i++) {
                    sb.append(datos.getHeaders()[i]);
                    if (i < datos.getHeaders().length - 1) sb.append(",");
                }
                sb.append("\n");
            }
            if (datos.getFilas() != null) {
                for (String[] fila : datos.getFilas()) {
                    for (int i = 0; i < fila.length; i++) {
                        String celda = fila[i];
                        if (celda != null && (celda.contains(",") || celda.contains("\"") || celda.contains("\n"))) {
                            sb.append("\"").append(celda.replace("\"", "\"\"")).append("\"");
                        } else {
                            sb.append(celda);
                        }
                        if (i < fila.length - 1) sb.append(",");
                    }
                    sb.append("\n");
                }
            }
            return sb.toString().getBytes("UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Error al generar CSV", e);
        }
    }
}
