package com.gobierno.servicio_auditoria.Domain.AbsFactory;

import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;

// Interfaz para la fábrica abstracta de auditoría
public abstract class AuditoriaAbsFactory {

    public abstract Auditoria creAuditoria(Auditoria auditoria);
}
