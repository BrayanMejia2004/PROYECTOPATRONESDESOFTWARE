package com.gobierno.servicio_auditoria.domain.model;

public class IpCountDTO {
    private String ip;
    private Long total;

    public IpCountDTO() {}

    public IpCountDTO(String ip, Long total) {
        this.ip = ip;
        this.total = total;
    }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
}
