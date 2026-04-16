package com.gobierno.servicio_reportes.domain.services;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;


public class CsvDecorator extends ReporteBaseDecorator {
    
    public CsvDecorator(ReporteComponent reporte) {
        super(reporte);
    }
    
    @Override
    public byte[] generar(ReporteData datos) {
        return reporte.generar(datos);
    }
}
