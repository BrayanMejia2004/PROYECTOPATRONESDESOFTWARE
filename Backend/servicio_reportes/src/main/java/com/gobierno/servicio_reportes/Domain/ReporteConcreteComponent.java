package com.gobierno.servicio_reportes.Domain;

// Concrete Component - Implementacion base del reporte
// Convierte los datos a texto plano sin ninguna decoracion
public class ReporteConcreteComponent implements ReporteComponent {
    
    @Override
    public byte[] generar(ReporteData datos) {
        // Convierte los datos a texto plano
        return datos.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}
