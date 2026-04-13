package com.gobierno.servicio_reportes.domain.services;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

public class PdfDecorator extends ReporteBaseDecorator {
    
    public PdfDecorator(ReporteComponent reporte) {
        super(reporte);
    }
    
    @Override
    public byte[] generar(ReporteData datos) {
        return convertirAPdf(datos);
    }
    
    private byte[] convertirAPdf(ReporteData datos) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("%PDF-1.4\n");
            sb.append("1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\n");
            sb.append("2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj\n");
            sb.append("3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] >> endobj\n");
            sb.append("xref\n0 4\n");
            sb.append("trailer << /Size 4 /Root 1 0 R >>\n");
            sb.append("startxref\n0\n");
            sb.append("%%EOF\n");
            sb.append("\n--- REPORTE ---\n");
            sb.append(datos.getTitulo()).append("\n\n");
            if (datos.getHeaders() != null) {
                for (String header : datos.getHeaders()) sb.append(header).append("\t");
                sb.append("\n");
            }
            if (datos.getFilas() != null) {
                for (String[] fila : datos.getFilas()) {
                    for (String celda : fila) sb.append(celda).append("\t");
                    sb.append("\n");
                }
            }
            return sb.toString().getBytes("UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF", e);
        }
    }
}
