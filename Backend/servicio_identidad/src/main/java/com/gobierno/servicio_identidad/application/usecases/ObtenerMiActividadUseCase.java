package com.gobierno.servicio_identidad.application.usecases;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.domain.entities.PerfilUsuario;
import com.gobierno.servicio_identidad.domain.entities.SesionActiva;
import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AuditoriaConsultaClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.MiActividadResponse;
import com.gobierno.servicio_identidad.infrastructure.persistence.repository.PerfilUsuarioJpaRepository;
import com.gobierno.servicio_identidad.infrastructure.persistence.repository.SesionActivaJpaRepository;
import com.gobierno.servicio_identidad.infrastructure.persistence.repository.UsuarioJpaRepository;

@Service
public class ObtenerMiActividadUseCase {

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final PerfilUsuarioJpaRepository perfilUsuarioJpaRepository;
    private final SesionActivaJpaRepository sesionActivaJpaRepository;
    private final AuditoriaConsultaClient auditoriaConsultaClient;

    public ObtenerMiActividadUseCase(UsuarioJpaRepository usuarioJpaRepository,
            PerfilUsuarioJpaRepository perfilUsuarioJpaRepository,
            SesionActivaJpaRepository sesionActivaJpaRepository,
            AuditoriaConsultaClient auditoriaConsultaClient) {
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.perfilUsuarioJpaRepository = perfilUsuarioJpaRepository;
        this.sesionActivaJpaRepository = sesionActivaJpaRepository;
        this.auditoriaConsultaClient = auditoriaConsultaClient;
    }

    public MiActividadResponse ejecutar(String username) {
        Optional<Usuario> usuarioOpt = usuarioJpaRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioOpt.get();
        Integer usuarioId = usuario.getId().intValue();

        String nombre = null;
        String apellido = null;
        Optional<PerfilUsuario> perfilOpt = perfilUsuarioJpaRepository.findByUsuarioId(usuarioId);
        if (perfilOpt.isPresent()) {
            PerfilUsuario perfil = perfilOpt.get();
            nombre = perfil.getNombre();
            apellido = perfil.getApellido();
        }

        List<SesionActiva> sesiones = sesionActivaJpaRepository.findByActivaTrue();
        List<SesionActiva> sesionesUsuario = sesiones.stream()
                .filter(s -> s.getUsuarioId().equals(usuario.getId()))
                .collect(Collectors.toList());

        List<SesionActiva> todasSesiones = sesionActivaJpaRepository.findAll().stream()
                .filter(s -> s.getUsuarioId().equals(usuario.getId()))
                .collect(Collectors.toList());

        int totalSesiones = todasSesiones.size();

        List<String> ipsUtilizadas = todasSesiones.stream()
                .map(SesionActiva::getIpOrigen)
                .filter(ip -> ip != null && !ip.isBlank())
                .distinct()
                .collect(Collectors.toList());
        Collections.reverse(ipsUtilizadas);

        String ultimaSesion = null;
        if (!todasSesiones.isEmpty()) {
            Timestamp ultimaFecha = todasSesiones.get(todasSesiones.size() - 1).getFechaInicio();
            ultimaSesion = ultimaFecha != null ? ultimaFecha.toLocalDateTime().toString() : null;
        }

        List<Map<String, Object>> ultimosEventos = auditoriaConsultaClient.obtenerEventosRecientes(usuarioId, 10);

        int scoreSeguridad = calcularScoreSeguridad(
                ipsUtilizadas.size() <= 1,
                perfilOpt.isPresent() && perfilOpt.get().getNombre() != null,
                false,
                false,
                !sesionesUsuario.isEmpty()
        );

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String fechaCreacion = usuario.getFechaCreacion() != null
                ? usuario.getFechaCreacion().toLocalDateTime().format(fmt)
                : null;

        return new MiActividadResponse(
                usuario.getUsername(),
                usuario.getEmail(),
                nombre,
                apellido,
                fechaCreacion,
                totalSesiones,
                ipsUtilizadas,
                ultimaSesion,
                ultimosEventos,
                scoreSeguridad
        );
    }

    private int calcularScoreSeguridad(boolean mismaIp, boolean perfilCompleto,
            boolean sinIntentosFallidos, boolean cambioPasswordReciente,
            boolean sesionActiva) {
        int score = 0;
        if (mismaIp) score += 30;
        if (perfilCompleto) score += 20;
        if (sinIntentosFallidos) score += 20;
        if (cambioPasswordReciente) score += 15;
        if (sesionActiva) score += 15;
        return Math.min(score, 100);
    }
}
