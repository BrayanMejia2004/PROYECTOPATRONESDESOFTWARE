package com.gobierno.servicio_reportes.domain.composite;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

public interface SeccionComponent {
    
    byte[] generar(ReporteData datos);
    
    String getTipo();
    
    boolean estaHabilitada();
    
    int getOrden();
}
