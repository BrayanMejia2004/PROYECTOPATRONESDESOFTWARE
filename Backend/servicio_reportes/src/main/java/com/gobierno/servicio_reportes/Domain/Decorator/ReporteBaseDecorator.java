package com.gobierno.servicio_reportes.Domain.Decorator;

import com.gobierno.servicio_reportes.Domain.ReporteComponent;
import com.gobierno.servicio_reportes.Domain.ReporteData;

// Base Decorator - Clase abstracta del patron Decorator
// Mantiene referencia al siguiente componente en la cadena
// Delega la operacion al siguiente elemento
public abstract class ReporteBaseDecorator implements ReporteComponent {
    
    // Referencia al siguiente reporte en la cadena
    protected ReporteComponent reporte;
    
    // Constructor que recibe el siguiente reporte
    public ReporteBaseDecorator(ReporteComponent reporte) {
        this.reporte = reporte;
    }
    
    // Implementacion base que delega al siguiente objeto envuelto
    @Override
    public byte[] generar(ReporteData datos) {
        return reporte.generar(datos);
    }
}
