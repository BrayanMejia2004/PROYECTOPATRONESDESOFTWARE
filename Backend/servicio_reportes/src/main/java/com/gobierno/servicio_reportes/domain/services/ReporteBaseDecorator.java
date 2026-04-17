package com.gobierno.servicio_reportes.domain.services;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

public abstract class ReporteBaseDecorator implements ReporteComponent {
    protected ReporteComponent reporte;
    
    public ReporteBaseDecorator(ReporteComponent reporte) {
        this.reporte = reporte;
    }
    
    @Override
    public byte[] generar(ReporteData datos) {
        return reporte.generar(datos);
    }
}