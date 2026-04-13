package com.gobierno.servicio_reportes.domain.services;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

public class ReporteConcreteComponent implements ReporteComponent {
    @Override
    public byte[] generar(ReporteData datos) {
        return datos.toString().getBytes();
    }
}
