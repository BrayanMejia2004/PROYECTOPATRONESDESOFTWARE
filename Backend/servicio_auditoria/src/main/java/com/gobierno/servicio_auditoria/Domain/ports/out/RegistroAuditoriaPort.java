package com.gobierno.servicio_auditoria.domain.ports.out;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;

public interface RegistroAuditoriaPort {  // Puerto de salida para registrar auditorías
    
    void registrarAccion(Auditoria auditoria);  // Método para persistir una auditoría
}