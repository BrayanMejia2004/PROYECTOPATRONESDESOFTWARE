package com.gobierno.servicio_reportes.domain.services;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipDecorator extends ReporteBaseDecorator {
    
    private final String tipo;
    
    public ZipDecorator(ReporteComponent reporte, String tipo) {
        super(reporte);
        this.tipo = tipo;
    }
    
    @Override
    public byte[] generar(ReporteData datos) {
        byte[] datosPrevios = super.generar(datos);
        return comprimirZip(datosPrevios);
    }
    
    private byte[] comprimirZip(byte[] contenido) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            String extension = detectarExtension(contenido);
            String nombreArchivo = "reporte_" + tipo.toLowerCase() + extension;
            ZipEntry entry = new ZipEntry(nombreArchivo);
            zos.putNextEntry(entry);
            zos.write(contenido);
            zos.closeEntry();
            zos.finish();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al comprimir ZIP", e);
        }
    }
    
    private String detectarExtension(byte[] contenido) {
        if (contenido != null && contenido.length >= 4) {
            String header = new String(contenido, 0, 4);
            if (header.startsWith("%PDF")) {
                return ".pdf";
            }
        }
        return ".csv";
    }
}
