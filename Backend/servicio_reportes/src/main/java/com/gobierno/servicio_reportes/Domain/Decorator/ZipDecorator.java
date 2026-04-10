package com.gobierno.servicio_reportes.Domain.Decorator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.gobierno.servicio_reportes.Domain.ReporteComponent;
import com.gobierno.servicio_reportes.Domain.ReporteData;

// Concrete Decorator - Comprime el reporte en formato ZIP
public class ZipDecorator extends ReporteBaseDecorator {
    
    public ZipDecorator(ReporteComponent reporte) {
        super(reporte);
    }
    
    @Override
    public byte[] generar(ReporteData datos) {
        // Obtiene los datos del siguiente en la cadena
        byte[] datosPrevios = super.generar(datos);
        
        // Comprime a ZIP
        return comprimirZip(datosPrevios, datos);
    }
    
    // Comprime los datos en formato ZIP
    private byte[] comprimirZip(byte[] datos, ReporteData reporteData) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            
            // Determinar extension segun el tipo de datos
            String extension = reporteData.getTipo().toLowerCase().contains("pdf") ? ".pdf" : ".dat";
            String nombreArchivo = "reporte_" + reporteData.getTipo() + extension;
            
            // Crear entrada ZIP
            ZipEntry entry = new ZipEntry(nombreArchivo);
            zos.putNextEntry(entry);
            
            // Escribir datos
            zos.write(datos);
            zos.closeEntry();
            
            return baos.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Error al comprimir ZIP", e);
        }
    }
}
