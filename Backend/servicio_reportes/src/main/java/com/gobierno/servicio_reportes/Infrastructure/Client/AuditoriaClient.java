package com.gobierno.servicio_reportes.Infrastructure.Client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.gobierno.servicio_reportes.Domain.ReporteData;

// Client para consumir datos del servicio de auditoria
@Component
public class AuditoriaClient {
    
    private final RestTemplate restTemplate;
    
    public AuditoriaClient() {
        this.restTemplate = new RestTemplate();
    }
    
    // Obtiene datos de auditoria simulados para el reporte
    public ReporteData obtenerDatosAuditoria() {
        // Headers de la tabla
        String[] headers = {"ID", "Usuario", "Accion", "Descripcion", "Fecha", "Tipo"};
        
        // Filas de datos simulados
        String[][] filas = {
            {"1", "Juan Perez", "LOGIN", "Inicio de sesion", "2026-04-10", "BASICA"},
            {"2", "Maria Lopez", "CREAR_USUARIO", "Usuario creado", "2026-04-10", "SEGURIDAD"},
            {"3", "Carlos Garcia", "ELIMINAR", "Registro eliminado", "2026-04-09", "COMPLETA"},
            {"4", "Ana Martinez", "ACTUALIZAR", "Datos actualizados", "2026-04-09", "BASICA"},
            {"5", "Pedro Sanchez", "CONSULTAR", "Consulta realizada", "2026-04-08", "BASICA"}
        };
        
        ReporteData datos = new ReporteData();
        datos.setTipo("AUDITORIA");
        datos.setTitulo("Reporte de Auditoria del Sistema");
        datos.setHeaders(headers);
        datos.setFilas(filas);
        datos.setDescripcion("Reporte que muestra las acciones registradas en el sistema");
        
        return datos;
    }
}
