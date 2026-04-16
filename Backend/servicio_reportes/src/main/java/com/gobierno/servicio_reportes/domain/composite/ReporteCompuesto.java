package com.gobierno.servicio_reportes.domain.composite;

import com.gobierno.servicio_reportes.domain.services.ReporteComponent;
import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Composite del patrón Composite.
 * Gestiona colección de secciones y las integra con Decorator (ReporteComponent).
 */
public class ReporteCompuesto implements SeccionComponent, ReporteComponent {
    
    private final String nombre;
    private final List<SeccionComponent> secciones;
    
    public ReporteCompuesto(String nombre) {
        this.nombre = nombre;
        this.secciones = new ArrayList<>();
    }
    
    // Añade sección y reordena por prioridad
    public void agregarSeccion(SeccionComponent seccion) {
        secciones.add(seccion);
        ordenarSecciones();
    }
    
    // Elimina sección específica
    public void quitarSeccion(SeccionComponent seccion) {
        secciones.remove(seccion);
    }
    
    // Elimina todas las secciones de un tipo
    public void quitarSeccionPorTipo(String tipo) {
        secciones.removeIf(s -> s.getTipo().equalsIgnoreCase(tipo));
    }
    
    public List<SeccionComponent> getSecciones() {
        return new ArrayList<>(secciones);
    }
    
    // Ordena secciones por su campo 'orden'
    private void ordenarSecciones() {
        secciones.sort(Comparator.comparingInt(SeccionComponent::getOrden));
    }
    
    // Genera contenido secuencial de secciones habilitadas
    @Override
    public byte[] generar(ReporteData datos) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Filtra y ordena secciones habilitadas
        List<SeccionComponent> seccionesHabilitadas = secciones.stream()
                .filter(SeccionComponent::estaHabilitada)
                .sorted(Comparator.comparingInt(SeccionComponent::getOrden))
                .collect(Collectors.toList());
        
        // Concatena salida de cada sección
        for (SeccionComponent seccion : seccionesHabilitadas) {
            byte[] contenido = seccion.generar(datos);
            if (contenido != null && contenido.length > 0) {
                try {
                    baos.write(contenido);
                } catch (Exception e) {
                    throw new RuntimeException("Error al generar seccion: " + seccion.getTipo(), e);
                }
            }
        }
        
        return baos.toByteArray();
    }
    
    @Override
    public String getTipo() {
        return "REPORTE_COMPUESTO";
    }
    
    @Override
    public boolean estaHabilitada() {
        return true;
    }
    
    @Override
    public int getOrden() {
        return 0;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public int getTotalSecciones() {
        return secciones.size();
    }
    
    public int getTotalSeccionesHabilitadas() {
        return (int) secciones.stream().filter(SeccionComponent::estaHabilitada).count();
    }
}
