package com.gobierno.servicio_identidad.domain.ports.out;

import com.gobierno.servicio_identidad.infrastructure.adapter.dto.SolicitudAutenticacion;

public interface AutenticadorPort { // Interfaz para el servicio de autenticación

    Object autenticar(SolicitudAutenticacion solicitud); // Autentica al usuario y retorna el resultado (token o usuario
                                                         // autenticado)
}