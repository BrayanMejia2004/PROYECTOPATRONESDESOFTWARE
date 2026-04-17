package com.gobierno.servicio_reportes.domain.composite;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SeccionResumen extends SeccionSimple {
    
    public SeccionResumen() {
        super("RESUMEN", 2);
    }
    
    @Override
    public byte[] generar(ReporteData datos) {
        if (!habilitada || datos.getFilas() == null || datos.getFilas().length == 0) {
            return new byte[0];
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("RESUMEN\n");
        sb.append(repetirCaracter('-', 60)).append("\n");
        
        int totalRegistros = datos.getFilas().length;
        sb.append("Total de registros: ").append(totalRegistros).append("\n\n");
        
        Map<String, Integer> resumenPorTipo = new HashMap<>();
        Map<String, Integer> resumenPorAccion = new HashMap<>();
        
        String[] headers = datos.getHeaders();
        int indiceTipo = encontrarIndice(headers, "tipo", "Tipo");
        int indiceAccion = encontrarIndice(headers, "accion", "Accion");
        
        for (String[] fila : datos.getFilas()) {
            if (indiceTipo >= 0 && indiceTipo < fila.length && fila[indiceTipo] != null) {
                String tipo = fila[indiceTipo].toUpperCase();
                resumenPorTipo.put(tipo, resumenPorTipo.getOrDefault(tipo, 0) + 1);
            }
            if (indiceAccion >= 0 && indiceAccion < fila.length && fila[indiceAccion] != null) {
                String accion = fila[indiceAccion].toUpperCase();
                resumenPorAccion.put(accion, resumenPorAccion.getOrDefault(accion, 0) + 1);
            }
        }
        
        sb.append("Por Tipo:\n");
        for (Map.Entry<String, Integer> entry : resumenPorTipo.entrySet()) {
            sb.append("  - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        sb.append("\nPor Accion:\n");
        for (Map.Entry<String, Integer> entry : resumenPorAccion.entrySet()) {
            sb.append("  - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        sb.append("\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
    
    private int encontrarIndice(String[] headers, String... nombres) {
        if (headers == null) return -1;
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].toLowerCase();
            for (String nombre : nombres) {
                if (header.contains(nombre.toLowerCase())) {
                    return i;
                }
            }
        }
        return -1;
    }
}
