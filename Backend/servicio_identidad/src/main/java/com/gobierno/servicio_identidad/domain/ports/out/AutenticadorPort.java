package com.gobierno.servicio_identidad.domain.ports.out;

import com.gobierno.servicio_identidad.infrastructure.adapter.dto.SolicitudAutenticacion;

public interface AutenticadorPort {
    
    Object autenticar(SolicitudAutenticacion solicitud);
}
