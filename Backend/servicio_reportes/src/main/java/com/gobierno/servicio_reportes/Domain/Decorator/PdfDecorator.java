package com.gobierno.servicio_reportes.Domain.Decorator;

import com.gobierno.servicio_reportes.Domain.ReporteComponent;
import com.gobierno.servicio_reportes.Domain.ReporteData;

// Concrete Decorator - Genera reporte en formato PDF
public class PdfDecorator extends ReporteBaseDecorator {
    
    public PdfDecorator(ReporteComponent reporte) {
        super(reporte);
    }
    
    @Override
    public byte[] generar(ReporteData datos) {
        // Obtiene los datos del siguiente en la cadena
        byte[] datosPrevios = super.generar(datos);
        
        // Convierte a PDF
        return convertirAPdf(datos);
    }
    
    // Convierte los datos a formato PDF simple
    private byte[] convertirAPdf(ReporteData datos) {
        try {
            // Usa StringBuilder para crear el contenido del PDF
            StringBuilder sb = new StringBuilder();
            
            // Encabezado del PDF
            sb.append("%PDF-1.4\n");
            
            // Titulo
            sb.append("1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\n");
            sb.append("2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj\n");
            sb.append("3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] >> endobj\n");
            
            // Agregar titulo del reporte
            sb.append("4 0 obj << /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >> endobj\n");
            
            // Contenido del reporte como texto
            sb.append("5 0 obj << /Type /Page /Parent 2 0 R\n");
            sb.append("   /MediaBox [0 0 612 792]\n");
            sb.append("   /Contents << /Length " + (datos.toString().length() + 100) + " >>\n");
            sb.append(">>\n");
            
            // Trailer
            sb.append("xref\n");
            sb.append("0 6\n");
            sb.append("trailer << /Size 6 /Root 1 0 R >>\n");
            sb.append("startxref\n");
            sb.append("0\n");
            sb.append("%%EOF\n");
            
            // Agregar datos del reporte
            sb.append("\n--- REPORTE ---\n");
            sb.append(datos.getTitulo()).append("\n\n");
            
            // Agregar headers
            if (datos.getHeaders() != null) {
                for (String header : datos.getHeaders()) {
                    sb.append(header).append("\t");
                }
                sb.append("\n");
            }
            
            // Agregar filas
            if (datos.getFilas() != null) {
                for (String[] fila : datos.getFilas()) {
                    for (String celda : fila) {
                        sb.append(celda).append("\t");
                    }
                    sb.append("\n");
                }
            }
            
            return sb.toString().getBytes("UTF-8");
            
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF", e);
        }
    }
}
