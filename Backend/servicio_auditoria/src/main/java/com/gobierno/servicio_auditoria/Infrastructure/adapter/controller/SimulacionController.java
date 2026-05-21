package com.gobierno.servicio_auditoria.infrastructure.adapter.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_auditoria.application.usecases.SimularEventosUseCase;
import com.gobierno.servicio_auditoria.domain.model.EscenarioSimulacionDTO;
import com.gobierno.servicio_auditoria.domain.model.EventoSimuladoDTO;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.SimularResponse;

@RestController
@RequestMapping("/auditoria/simular")
public class SimulacionController {

    private final SimularEventosUseCase simularEventosUseCase;

    public SimulacionController(SimularEventosUseCase simularEventosUseCase) {
        this.simularEventosUseCase = simularEventosUseCase;
    }

    @PostMapping("/evento")
    public ResponseEntity<SimularResponse> simularEvento(@RequestBody EventoSimuladoDTO evento) {
        return ResponseEntity.ok(simularEventosUseCase.simularEvento(evento));
    }

    @PostMapping("/lote")
    public ResponseEntity<SimularResponse> simularLote(@RequestBody List<EventoSimuladoDTO> eventos) {
        return ResponseEntity.ok(simularEventosUseCase.simularLote(eventos));
    }

    @PostMapping("/escenario")
    public ResponseEntity<SimularResponse> ejecutarEscenario(@RequestBody EscenarioSimulacionDTO escenario) {
        return ResponseEntity.ok(simularEventosUseCase.simularEscenario(escenario));
    }

    @DeleteMapping("/{simulacionId}")
    public ResponseEntity<Void> deshacerSimulacion(@PathVariable String simulacionId) {
        simularEventosUseCase.deshacerSimulacion(simulacionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/escenarios")
    public ResponseEntity<List<EscenarioSimulacionInfo>> listarEscenarios() {
        List<EscenarioSimulacionInfo> escenarios = Arrays.asList(
            new EscenarioSimulacionInfo("Dia Normal",
                "10 eventos variados (LOGIN, REGISTRO, CONSULTA) para demostración básica", 10,
                Arrays.asList("LOGIN", "REGISTRO", "CONSULTA")),
            new EscenarioSimulacionInfo("Ataque Fuerza Bruta",
                "30 LOGIN fallidos desde misma IP en 1 minuto", 30,
                Arrays.asList("LOGIN")),
            new EscenarioSimulacionInfo("Pico de Accesos",
                "50 LOGIN exitosos desde IPs distintas", 50,
                Arrays.asList("LOGIN")),
            new EscenarioSimulacionInfo("Actividad Sospechosa",
                "15 eventos desde IP desconocida en horario nocturno", 15,
                Arrays.asList("LOGIN", "CONSULTA")),
            new EscenarioSimulacionInfo("Multiusuario",
                "20 eventos de 5 usuarios diferentes desde 3 IPs", 20,
                Arrays.asList("LOGIN", "REGISTRO", "CONSULTA", "LOGOUT"))
        );
        return ResponseEntity.ok(escenarios);
    }

    public static class EscenarioSimulacionInfo {
        private String nombre;
        private String descripcion;
        private int totalEventos;
        private List<String> tiposEvento;

        public EscenarioSimulacionInfo() {}

        public EscenarioSimulacionInfo(String nombre, String descripcion, int totalEventos, List<String> tiposEvento) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.totalEventos = totalEventos;
            this.tiposEvento = tiposEvento;
        }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public int getTotalEventos() { return totalEventos; }
        public void setTotalEventos(int totalEventos) { this.totalEventos = totalEventos; }
        public List<String> getTiposEvento() { return tiposEvento; }
        public void setTiposEvento(List<String> tiposEvento) { this.tiposEvento = tiposEvento; }
    }
}
