package com.gobierno.servicio_auditoria.infrastructure.adapter.controller;

import com.gobierno.servicio_auditoria.domain.model.ThreatEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ThreatSseService {

    private static final Logger log = LoggerFactory.getLogger(ThreatSseService.class);
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        log.info("Cliente SSE conectado. Total: {}", emitters.size());
        return emitter;
    }

    public void broadcast(ThreatEventDTO threat) {
        List<SseEmitter> muertos = new java.util.ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("threat")
                        .data(threat));
            } catch (IOException e) {
                muertos.add(emitter);
            }
        }
        emitters.removeAll(muertos);
    }

    public int getConnectedClients() {
        return emitters.size();
    }
}