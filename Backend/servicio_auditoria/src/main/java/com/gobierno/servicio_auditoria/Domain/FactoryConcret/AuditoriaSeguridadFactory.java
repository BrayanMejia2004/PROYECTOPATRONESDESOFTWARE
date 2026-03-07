package com.gobierno.servicio_auditoria.Domain.FactoryConcret;

import com.gobierno.servicio_auditoria.Domain.AbsFactory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;

public class AuditoriaSeguridadFactory extends AuditoriaAbsFactory {

    @Override
    public Auditoria creAuditoria(Auditoria auditoria) {
       
        auditoria.setDescripcion(
                auditoria.getDescripcion() + " | EVENTO DE SEGURIDAD");

        return auditoria;
    }

}
