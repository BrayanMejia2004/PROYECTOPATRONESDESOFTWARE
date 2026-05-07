package com.gobierno.servicio_auditoria.infrastructure.adapter.dto;

import java.time.LocalDateTime;

public class IpEstadisticaDTO {
    private String ipOrigen;
    private Long totalEventos;
    private Long totalUsuariosDistintos;
    private LocalDateTime primeraVez;
    private LocalDateTime ultimaVez;
    private Integer nivelIntensidad;
    private Boolean esSospechosa;

    public IpEstadisticaDTO() {}

    public IpEstadisticaDTO(String ipOrigen, Long totalEventos, Long totalUsuariosDistintos,
                            LocalDateTime primeraVez, LocalDateTime ultimaVez,
                            Integer nivelIntensidad, Boolean esSospechosa) {
        this.ipOrigen = ipOrigen;
        this.totalEventos = totalEventos;
        this.totalUsuariosDistintos = totalUsuariosDistintos;
        this.primeraVez = primeraVez;
        this.ultimaVez = ultimaVez;
        this.nivelIntensidad = nivelIntensidad;
        this.esSospechosa = esSospechosa;
    }

    public String getIpOrigen() { return ipOrigen; }
    public void setIpOrigen(String ipOrigen) { this.ipOrigen = ipOrigen; }
    public Long getTotalEventos() { return totalEventos; }
    public void setTotalEventos(Long totalEventos) { this.totalEventos = totalEventos; }
    public Long getTotalUsuariosDistintos() { return totalUsuariosDistintos; }
    public void setTotalUsuariosDistintos(Long totalUsuariosDistintos) { this.totalUsuariosDistintos = totalUsuariosDistintos; }
    public LocalDateTime getPrimeraVez() { return primeraVez; }
    public void setPrimeraVez(LocalDateTime primeraVez) { this.primeraVez = primeraVez; }
    public LocalDateTime getUltimaVez() { return ultimaVez; }
    public void setUltimaVez(LocalDateTime ultimaVez) { this.ultimaVez = ultimaVez; }
    public Integer getNivelIntensidad() { return nivelIntensidad; }
    public void setNivelIntensidad(Integer nivelIntensidad) { this.nivelIntensidad = nivelIntensidad; }
    public Boolean getEsSospechosa() { return esSospechosa; }
    public void setEsSospechosa(Boolean esSospechosa) { this.esSospechosa = esSospechosa; }
}
