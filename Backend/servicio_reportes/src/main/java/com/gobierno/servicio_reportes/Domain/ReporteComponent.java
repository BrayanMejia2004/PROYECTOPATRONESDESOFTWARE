package com.gobierno.servicio_reportes.Domain;

// Component - Interfaz comun para todos los reportes
// Define el metodo que todos los decorators deben implementar
public interface ReporteComponent {
    
    // Metodo para generar el reporte en bytes
    byte[] generar(ReporteData datos);
}
