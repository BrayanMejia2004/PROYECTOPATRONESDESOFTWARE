package com.gobierno.servicio_reportes.domain.composite;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

/**
 * Interfaz Component del patrón Composite.
 * Define la estructura común para secciones simples y compuestas.
 */
public interface SeccionComponent {
    
    // Genera el contenido de bytes para esta sección
    byte[] generar(ReporteData datos);
    
    // Retorna el tipo de sección (ej: ENCABEZADO, DETALLE)
    String getTipo();
    
    // Indica si la sección está habilitada para generar
    boolean estaHabilitada();
    
    // Orden de generación dentro del reporte
    int getOrden();
}
