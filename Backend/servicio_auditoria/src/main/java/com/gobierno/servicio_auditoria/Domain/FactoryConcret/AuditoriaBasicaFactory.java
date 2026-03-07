package com.gobierno.servicio_auditoria.Domain.FactoryConcret;

import com.gobierno.servicio_auditoria.Domain.AbsFactory.AuditoriaFactory;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;

public class AuditoriaBasicaFactory extends AuditoriaFactory {

    @Override
    public Auditoria creAuditoria(Auditoria auditoria) {
       
        auditoria.setIp_origen(null);

        return auditoria;
    }

}
