package com.gobierno.servicio_auditoria.infrastructure.adapter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimularResponse {

    private String simulacionId;
    private String mensaje;

    public SimularResponse() {}

    public SimularResponse(String simulacionId, String mensaje) {
        this.simulacionId = simulacionId;
        this.mensaje = mensaje;
    }

    public String getSimulacionId() { return simulacionId; }
    public void setSimulacionId(String simulacionId) { this.simulacionId = simulacionId; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
