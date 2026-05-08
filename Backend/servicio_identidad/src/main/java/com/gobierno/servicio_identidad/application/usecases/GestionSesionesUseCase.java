package com.gobierno.servicio_identidad.application.usecases;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.domain.entities.SesionActiva;
import com.gobierno.servicio_identidad.domain.entities.TokenRevocado;
import com.gobierno.servicio_identidad.infrastructure.persistence.repository.SesionActivaJpaRepository;
import com.gobierno.servicio_identidad.infrastructure.persistence.repository.TokenRevocadoJpaRepository;

@Service
public class GestionSesionesUseCase {

    private final SesionActivaJpaRepository sesionActivaRepository;
    private final TokenRevocadoJpaRepository tokenRevocadoRepository;

    public GestionSesionesUseCase(SesionActivaJpaRepository sesionActivaRepository,
            TokenRevocadoJpaRepository tokenRevocadoRepository) {
        this.sesionActivaRepository = sesionActivaRepository;
        this.tokenRevocadoRepository = tokenRevocadoRepository;
    }

    public void registrarSesion(Long usuarioId, String username, String ip, String tokenHash) {
        SesionActiva sesion = new SesionActiva.Builder()
                .usuarioId(usuarioId)
                .username(username)
                .ipOrigen(ip)
                .fechaInicio(Timestamp.valueOf(LocalDateTime.now()))
                .tokenHash(tokenHash)
                .activa(true)
                .build();
        sesionActivaRepository.save(sesion);
    }

    public List<SesionActiva> obtenerSesionesActivas() {
        return sesionActivaRepository.findByActivaTrue();
    }

    public void revocarSesion(Long sesionId, String revocadoPor) {
        Optional<SesionActiva> sesionOpt = sesionActivaRepository.findById(sesionId);
        if (sesionOpt.isPresent()) {
            SesionActiva sesion = sesionOpt.get();
            sesion.setActiva(false);
            sesionActivaRepository.save(sesion);

            TokenRevocado tokenRevocado = new TokenRevocado.Builder()
                    .tokenHash(sesion.getTokenHash())
                    .fechaRevocacion(Timestamp.valueOf(LocalDateTime.now()))
                    .revocadoPor(revocadoPor)
                    .build();
            tokenRevocadoRepository.save(tokenRevocado);
        }
    }

    public boolean esTokenRevocado(String tokenHash) {
        return tokenRevocadoRepository.existsByTokenHash(tokenHash);
    }

    public long contarRevocacionesHoy() {
        LocalDateTime inicioHoy = LocalDateTime.now().toLocalDate().atStartOfDay();
        return tokenRevocadoRepository.countByFechaRevocacionAfter(Timestamp.valueOf(inicioHoy));
    }

    public long contarRevocacionesSemana() {
        LocalDateTime inicioSemana = LocalDateTime.now().minusDays(7);
        return tokenRevocadoRepository.countByFechaRevocacionAfter(Timestamp.valueOf(inicioSemana));
    }

    public long contarRevocacionesTotales() {
        return tokenRevocadoRepository.count();
    }

    public long contarSesionesHoy() {
        LocalDateTime inicioHoy = LocalDateTime.now().toLocalDate().atStartOfDay();
        return sesionActivaRepository.countByFechaInicioAfter(Timestamp.valueOf(inicioHoy));
    }
}
