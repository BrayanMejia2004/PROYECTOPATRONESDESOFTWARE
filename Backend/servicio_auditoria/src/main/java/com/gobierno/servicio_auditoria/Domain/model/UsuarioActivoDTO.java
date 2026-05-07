package com.gobierno.servicio_auditoria.domain.model;

public class UsuarioActivoDTO {
    private Long usuarioId;
    private String username;
    private Long totalAcciones;

    public UsuarioActivoDTO() {}

    public UsuarioActivoDTO(Long usuarioId, String username, Long totalAcciones) {
        this.usuarioId = usuarioId;
        this.username = username;
        this.totalAcciones = totalAcciones;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Long getTotalAcciones() { return totalAcciones; }
    public void setTotalAcciones(Long totalAcciones) { this.totalAcciones = totalAcciones; }
}
