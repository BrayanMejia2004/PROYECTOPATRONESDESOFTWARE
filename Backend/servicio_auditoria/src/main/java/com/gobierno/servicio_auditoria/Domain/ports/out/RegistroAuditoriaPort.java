package com.gobierno.servicio_auditoria.domain.ports.out;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;

public interface RegistroAuditoriaPort {

    void registrarAccion(Auditoria auditoria);
}
