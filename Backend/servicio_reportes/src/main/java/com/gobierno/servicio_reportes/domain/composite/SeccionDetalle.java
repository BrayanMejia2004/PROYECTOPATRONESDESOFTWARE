package com.gobierno.servicio_reportes.domain.composite;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

import java.nio.charset.StandardCharsets;

public class SeccionDetalle extends SeccionSimple {
    
    private static final int ANCHO_COLUMNA = 15;
    
    public SeccionDetalle() {
        super("DETALLE", 3);
    }
    
    @Override
    public byte[] generar(ReporteData datos) {
        if (!habilitada) {
            return new byte[0];
        }
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("DETALLE DE REGISTROS\n");
        sb.append(repetirCaracter('=', 60)).append("\n");
        
        if (datos.getHeaders() == null || datos.getHeaders().length == 0) {
            sb.append("No hay datos disponibles.\n");
            return sb.toString().getBytes(StandardCharsets.UTF_8);
        }
        
        sb.append(generarEncabezadoTabla(datos.getHeaders()));
        sb.append(repetirCaracter('-', 60)).append("\n");
        
        if (datos.getFilas() != null && datos.getFilas().length > 0) {
            for (String[] fila : datos.getFilas()) {
                sb.append(generarFilaTabla(fila));
            }
        } else {
            sb.append("No hay registros para mostrar.\n");
        }
        
        sb.append(repetirCaracter('=', 60)).append("\n");
        sb.append("Total: ").append(datos.getFilas() != null ? datos.getFilas().length : 0).append(" registros\n");
        sb.append("\n");
        
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
    
    private String generarEncabezadoTabla(String[] headers) {
        StringBuilder sb = new StringBuilder();
        for (String header : headers) {
            sb.append("|").append(formatearCelda(header, ANCHO_COLUMNA));
        }
        sb.append("|\n");
        return sb.toString();
    }
    
    private String generarFilaTabla(String[] fila) {
        StringBuilder sb = new StringBuilder();
        for (String celda : fila) {
            sb.append("|").append(formatearCelda(celda, ANCHO_COLUMNA));
        }
        sb.append("|\n");
        return sb.toString();
    }
    
    private String formatearCelda(String valor, int ancho) {
        if (valor == null) valor = "";
        if (valor.length() > ancho - 2) {
            valor = valor.substring(0, ancho - 5) + "...";
        }
        return formatearLinea(valor, ancho - 1);
    }
}
