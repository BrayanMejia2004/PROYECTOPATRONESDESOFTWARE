package com.gobierno.servicio_identidad.infrastructure.adapter.dto;

import java.util.List;
import java.util.Map;

public class MiActividadResponse {

    private String username;
    private String email;
    private String nombre;
    private String apellido;
    private String fechaCreacion;
    private int totalSesiones;
    private List<String> ipsUtilizadas;
    private String ultimaSesion;
    private List<Map<String, Object>> ultimosEventos;
    private int scoreSeguridad;

    public MiActividadResponse() {}

    public MiActividadResponse(String username, String email, String nombre, String apellido,
            String fechaCreacion, int totalSesiones, List<String> ipsUtilizadas,
            String ultimaSesion, List<Map<String, Object>> ultimosEventos, int scoreSeguridad) {
        this.username = username;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaCreacion = fechaCreacion;
        this.totalSesiones = totalSesiones;
        this.ipsUtilizadas = ipsUtilizadas;
        this.ultimaSesion = ultimaSesion;
        this.ultimosEventos = ultimosEventos;
        this.scoreSeguridad = scoreSeguridad;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public int getTotalSesiones() { return totalSesiones; }
    public void setTotalSesiones(int totalSesiones) { this.totalSesiones = totalSesiones; }
    public List<String> getIpsUtilizadas() { return ipsUtilizadas; }
    public void setIpsUtilizadas(List<String> ipsUtilizadas) { this.ipsUtilizadas = ipsUtilizadas; }
    public String getUltimaSesion() { return ultimaSesion; }
    public void setUltimaSesion(String ultimaSesion) { this.ultimaSesion = ultimaSesion; }
    public List<Map<String, Object>> getUltimosEventos() { return ultimosEventos; }
    public void setUltimosEventos(List<Map<String, Object>> ultimosEventos) { this.ultimosEventos = ultimosEventos; }
    public int getScoreSeguridad() { return scoreSeguridad; }
    public void setScoreSeguridad(int scoreSeguridad) { this.scoreSeguridad = scoreSeguridad; }
}
