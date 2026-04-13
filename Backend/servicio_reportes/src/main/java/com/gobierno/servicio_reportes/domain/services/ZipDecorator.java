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
            String nombreArchivo = "reporte_" + tipo.toLowerCase() + ".pdf";
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
}
