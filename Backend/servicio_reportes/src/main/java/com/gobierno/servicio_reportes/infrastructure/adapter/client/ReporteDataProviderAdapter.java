package com.gobierno.servicio_reportes.infrastructure.adapter.client;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import com.gobierno.servicio_reportes.domain.ports.out.ReporteDataProviderPort;
import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

@Component
public class ReporteDataProviderAdapter implements ReporteDataProviderPort {

    private final RestTemplate restTemplate;

    @Value("${servicio.auditoria.url}")
    private String auditoriaUrl;

    @Value("${servicio.identidad.url}")
    private String identidadUrl;

    @Value("${servicio.autorizacion.url}")
    private String autorizacionUrl;

    public ReporteDataProviderAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ReporteData obtenerDatosAuditoria() {
        return obtenerDatosAuditoriaFiltrado(null, null, null, null, null);
    }

    @Override
    public ReporteData obtenerDatosAuditoriaFiltrado(
            Integer usuarioId,
            Timestamp fechaDesde,
            Timestamp fechaHasta,
            String tipo,
            String accion) {
        try {
            StringBuilder urlBuilder = new StringBuilder(auditoriaUrl + "/auditoria/lista?");

            if (usuarioId != null) {
                urlBuilder.append("usuarioId=").append(usuarioId).append("&");
            }
            if (fechaDesde != null) {
                urlBuilder.append("fechaDesde=")
                        .append(URLEncoder.encode(fechaDesde.toString(), StandardCharsets.UTF_8)).append("&");
            }
            if (fechaHasta != null) {
                urlBuilder.append("fechaHasta=")
                        .append(URLEncoder.encode(fechaHasta.toString(), StandardCharsets.UTF_8)).append("&");
            }
            if (tipo != null && !tipo.isBlank()) {
                urlBuilder.append("tipo=").append(URLEncoder.encode(tipo, StandardCharsets.UTF_8)).append("&");
            }
            if (accion != null && !accion.isBlank()) {
                urlBuilder.append("accion=").append(URLEncoder.encode(accion, StandardCharsets.UTF_8)).append("&");
            }

            String url = urlBuilder.toString();
            if (url.endsWith("&")) {
                url = url.substring(0, url.length() - 1);
            }

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });
            List<Map<String, Object>> auditorias = response.getBody();

            if (auditorias == null || auditorias.isEmpty()) {
                return crearVacio("AUDITORIA", "Reporte de Auditoria", "Sin registros");
            }

            String[] headers = { "ID", "Usuario ID", "Accion", "Descripcion", "Fecha", "IP", "Tipo" };
            String[][] filas = new String[auditorias.size()][headers.length];

            for (int i = 0; i < auditorias.size(); i++) {
                Map<String, Object> a = auditorias.get(i);
                filas[i][0] = String.valueOf(a.get("id"));
                filas[i][1] = String.valueOf(a.get("usuario_id"));
                filas[i][2] = String.valueOf(a.get("accion"));
                filas[i][3] = String.valueOf(a.get("descripcion"));
                filas[i][4] = String.valueOf(a.get("fecha"));
                filas[i][5] = String.valueOf(a.get("ip_origen"));
                filas[i][6] = String.valueOf(a.get("tipo"));
            }

            ReporteData datos = new ReporteData();
            datos.setTipo("AUDITORIA");
            datos.setTitulo("Reporte de Auditoria del Sistema");
            datos.setHeaders(headers);
            datos.setFilas(filas);
            datos.setDescripcion("Total: " + auditorias.size() + " registros");
            return datos;
        } catch (Exception e) {
            return crearVacio("AUDITORIA", "Reporte de Auditoria", "Error: " + e.getMessage());
        }
    }

    @Override
    public ReporteData obtenerDatosUsuarios() {
        try {
            String url = identidadUrl + "/usuarios/lista";
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });
            List<Map<String, Object>> usuarios = response.getBody();

            if (usuarios == null || usuarios.isEmpty()) {
                return crearVacio("USUARIOS", "Reporte de Usuarios", "Sin usuarios");
            }

            String[] headers = { "ID", "Username", "Email", "Estado", "Fecha Creacion" };
            String[][] filas = new String[usuarios.size()][headers.length];

            for (int i = 0; i < usuarios.size(); i++) {
                Map<String, Object> u = usuarios.get(i);
                filas[i][0] = String.valueOf(u.get("id"));
                filas[i][1] = String.valueOf(u.get("username"));
                filas[i][2] = String.valueOf(u.get("email"));
                filas[i][3] = formatEstado(u.get("estado"));
                filas[i][4] = String.valueOf(u.get("fechaCreacion"));
            }

            ReporteData datos = new ReporteData();
            datos.setTipo("USUARIOS");
            datos.setTitulo("Reporte de Usuarios del Sistema");
            datos.setHeaders(headers);
            datos.setFilas(filas);
            datos.setDescripcion("Total: " + usuarios.size() + " usuarios");
            return datos;
        } catch (Exception e) {
            return crearVacio("USUARIOS", "Reporte de Usuarios", "Error: " + e.getMessage());
        }
    }

    @Override
    public ReporteData obtenerDatosRoles() {
        try {
            String url = autorizacionUrl + "/roles/lista";
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });
            List<Map<String, Object>> roles = response.getBody();

            if (roles == null || roles.isEmpty()) {
                return crearVacio("ROLES", "Reporte de Roles", "Sin roles");
            }

            String[] headers = { "ID", "Nombre", "Descripcion" };
            String[][] filas = new String[roles.size()][headers.length];

            for (int i = 0; i < roles.size(); i++) {
                Map<String, Object> r = roles.get(i);
                filas[i][0] = String.valueOf(r.get("id"));
                filas[i][1] = String.valueOf(r.get("nombre"));
                filas[i][2] = String.valueOf(r.get("descripcion"));
            }

            ReporteData datos = new ReporteData();
            datos.setTipo("ROLES");
            datos.setTitulo("Reporte de Roles del Sistema");
            datos.setHeaders(headers);
            datos.setFilas(filas);
            datos.setDescripcion("Total: " + roles.size() + " roles");
            return datos;
        } catch (Exception e) {
            return crearVacio("ROLES", "Reporte de Roles", "Error: " + e.getMessage());
        }
    }

    @Override
    public ReporteData obtenerDatos(String tipo) {
        return switch (tipo.toUpperCase()) {
            case "AUDITORIA" -> obtenerDatosAuditoria();
            case "USUARIOS" -> obtenerDatosUsuarios();
            case "ROLES" -> obtenerDatosRoles();
            default -> throw new IllegalArgumentException("Tipo invalido: " + tipo);
        };
    }

    private String formatEstado(Object estado) {
        if (estado == null)
            return "DESCONOCIDO";
        if (estado instanceof Boolean)
            return (Boolean) estado ? "ACTIVO" : "INACTIVO";
        return estado.toString();
    }

    private ReporteData crearVacio(String tipo, String titulo, String descripcion) {
        ReporteData datos = new ReporteData();
        datos.setTipo(tipo);
        datos.setTitulo(titulo);
        datos.setHeaders(new String[] {});
        datos.setFilas(new String[][] {});
        datos.setDescripcion(descripcion);
        return datos;
    }
}
