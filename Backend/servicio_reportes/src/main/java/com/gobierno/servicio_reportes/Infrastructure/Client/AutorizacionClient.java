package com.gobierno.servicio_reportes.Infrastructure.Client;

import com.gobierno.servicio_reportes.Domain.ReporteData;

// Client para consumir datos del servicio de autorizacion
public class AutorizacionClient {
    
    // Obtiene datos de roles simulados para el reporte
    public ReporteData obtenerDatosRoles() {
        // Headers de la tabla
        String[] headers = {"ID", "Nombre Rol", "Descripcion", "Nivel Acceso", "Estado"};
        
        // Filas de datos simulados
        String[][] filas = {
            {"1", "ADMIN", "Administrador del sistema", "TOTAL", "ACTIVO"},
            {"2", "AUDITOR", "Usuario auditor", "LECTURA", "ACTIVO"},
            {"3", "USER", "Usuario basico", "LIMITADO", "ACTIVO"},
            {"4", "SUPERVISOR", "Supervisor de area", "MEDIO", "ACTIVO"},
            {"5", "INVITADO", "Usuario invitado", "MINIMO", "INACTIVO"}
        };
        
        ReporteData datos = new ReporteData();
        datos.setTipo("ROLES");
        datos.setTitulo("Reporte de Roles del Sistema");
        datos.setHeaders(headers);
        datos.setFilas(filas);
        datos.setDescripcion("Reporte que muestra los roles definidos en el sistema");
        
        return datos;
    }
}
