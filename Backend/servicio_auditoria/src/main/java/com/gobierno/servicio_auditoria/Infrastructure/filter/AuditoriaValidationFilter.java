package com.gobierno.servicio_auditoria.infrastructure.filter;

import com.gobierno.servicio_auditoria.domain.chain.AuditoriaFilter;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class AuditoriaValidationFilter extends HttpFilter { // Filtro Servlet que usa la cadena de responsabilidad

    private AuditoriaFilter auditoriaFilterChain; // Cadena de filtros

    public void setAuditoriaFilterChain(AuditoriaFilter auditoriaFilterChain) { // Setter para inyeccion manual
        this.auditoriaFilterChain = auditoriaFilterChain;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException { // Intercepta cada petición HTTP

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // Excluir GET /auditoria/lista, /auditoria/usuario/*/timeline, /auditoria/estadisticas, /auditoria/dashboard y POST /auditoria/registrar/** de la validación
        if (("GET".equals(method) && (requestURI.endsWith("/lista") || requestURI.contains("/timeline") || requestURI.contains("/estadisticas") || requestURI.endsWith("/dashboard"))) ||
            ("POST".equals(method) && requestURI.contains("/registrar/"))) {
            chain.doFilter(request, response);  // Continuar sin validación
            return;
        }

        // Extraer datos de la petición
        String ipOrigen = request.getRemoteAddr();
        String accion = request.getMethod() + " " + request.getRequestURI();

        // Crear objeto Auditoria para validar
        Auditoria auditoria = new Auditoria();
        auditoria.setIp_origen(ipOrigen);
        auditoria.setAccion(accion);
        auditoria.setFecha(Timestamp.valueOf(LocalDateTime.now()));

        // Extraer usuario_id del header o query param
        String usuarioIdStr = request.getHeader("X-Usuario-Id");
        if (usuarioIdStr == null || usuarioIdStr.isEmpty()) {
            usuarioIdStr = request.getParameter("usuarioId");
        }

        if (usuarioIdStr != null && !usuarioIdStr.isEmpty()) {
            try {
                auditoria.setUsuario_id(Integer.parseInt(usuarioIdStr));
            } catch (NumberFormatException e) {
                // ID no valido, se manejara en el filtro
            }
        }

        // Ejecutar la cadena de validacion
        boolean esValido = auditoriaFilterChain.doFilter(auditoria);

        if (!esValido) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            response.getWriter().write("{\"error\": \"Solicitud no valida para auditoria\"}");
            return;
        }

        // Si pasa la validacion, continuar con la peticion
        chain.doFilter(request, response);
    }
}
