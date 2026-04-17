package com.gobierno.servicio_reportes.domain.services;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

public interface ReporteComponent {
    byte[] generar(ReporteData datos);
}