package com.gobierno.servicio_auditoria.domain.ports.in;

import com.gobierno.servicio_auditoria.domain.model.AuditorDashboardDTO;

public interface ObtenerAuditorDashboardPort {
    AuditorDashboardDTO obtenerDashboard();
}
