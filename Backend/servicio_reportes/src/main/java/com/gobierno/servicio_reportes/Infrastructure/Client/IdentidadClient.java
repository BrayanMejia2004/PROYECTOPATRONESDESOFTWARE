package com.gobierno.servicio_reportes.Infrastructure.Client;

import com.gobierno.servicio_reportes.Domain.ReporteData;

// Client para consumir datos del servicio de identidad
public class IdentidadClient {
    
    // Obtiene datos de usuarios simulados para el reporte
    public ReporteData obtenerDatosUsuarios() {
        // Headers de la tabla
        String[] headers = {"ID", "Nombre", "Email", "Estado", "Fecha Registro"};
        
        // Filas de datos simulados
        String[][] filas = {
            {"1", "Juan Perez", "juan@correo.com", "ACTIVO", "2026-01-15"},
            {"2", "Maria Lopez", "maria@correo.com", "ACTIVO", "2026-02-20"},
            {"3", "Carlos Garcia", "carlos@correo.com", "INACTIVO", "2026-03-10"},
            {"4", "Ana Martinez", "ana@correo.com", "ACTIVO", "2026-03-25"},
            {"5", "Pedro Sanchez", "pedro@correo.com", "BLOQUEADO", "2026-04-01"}
        };
        
        ReporteData datos = new ReporteData();
        datos.setTipo("USUARIOS");
        datos.setTitulo("Reporte de Usuarios del Sistema");
        datos.setHeaders(headers);
        datos.setFilas(filas);
        datos.setDescripcion("Reporte que muestra los usuarios registrados en el sistema");
        
        return datos;
    }
}
