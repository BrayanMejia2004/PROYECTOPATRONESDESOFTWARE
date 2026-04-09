package com.gobierno.servicio_auditoria.Ports.Output;

import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;

// Interfaz para el servicio de registro de auditoria (Puerto Output)
public interface RegistroAuditoria {

    // Metodo para registrar una accion de auditoria en base de datos
    void registrarAccion(Auditoria auditoria);
}
