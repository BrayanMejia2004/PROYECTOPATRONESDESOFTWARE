package com.gobierno.servicio_identidad.Ports.Output;

import com.gobierno.servicio_identidad.Infrastructure.Dto.SolicitudAutenticacion;

// Se define una interfaz comun para autenticacion
public interface Autenticador {
    
    Object autenticar(SolicitudAutenticacion solicitud);
}
