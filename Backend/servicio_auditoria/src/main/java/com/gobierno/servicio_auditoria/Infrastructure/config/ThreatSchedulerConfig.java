package com.gobierno.servicio_auditoria.infrastructure.config;

import com.gobierno.servicio_auditoria.application.usecases.DetectarAmenazasUseCase;
import com.gobierno.servicio_auditoria.domain.model.ThreatEventDTO;
import com.gobierno.servicio_auditoria.infrastructure.adapter.controller.ThreatSseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
public class ThreatSchedulerConfig {

    private static final Logger log = LoggerFactory.getLogger(ThreatSchedulerConfig.class);
    private final DetectarAmenazasUseCase detectarAmenazasUseCase;
    private final ThreatSseService threatSseService;

    public ThreatSchedulerConfig(DetectarAmenazasUseCase detectarAmenazasUseCase,
                                  ThreatSseService threatSseService) {
        this.detectarAmenazasUseCase = detectarAmenazasUseCase;
        this.threatSseService = threatSseService;
    }

    @Scheduled(fixedRate = 30000)
    public void detectarYAvisarAmenazas() {
        List<ThreatEventDTO> nuevasAmenazas = detectarAmenazasUseCase.ejecutarDeteccion();
        for (ThreatEventDTO amenaza : nuevasAmenazas) {
            log.info("Emitiendo amenaza por SSE: {} (severidad={})", amenaza.getTipo(), amenaza.getSeveridad());
            threatSseService.broadcast(amenaza);
        }
    }
}