package com.gobierno.servicio_auditoria.tests.patrones.bridge;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.gobierno.servicio_auditoria.domain.factory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.domain.bridge.RegistrarAuditoriaProcessor;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;
import com.gobierno.servicio_auditoria.domain.ports.out.RegistroAuditoriaPort;

class RegistrarAuditoriaProcessorTest {

    private AuditoriaAbsFactory mockFactory;
    private RegistroAuditoriaPort mockRegistroAuditoria;
    private RegistrarAuditoriaProcessor processor;

    private Auditoria auditoriaRequest;
    private Auditoria auditoriaProcesada;
    private AuditoriaResponse auditoriaResponse;

    @BeforeEach
    void setUp() {
        mockFactory = mock(AuditoriaAbsFactory.class);
        mockRegistroAuditoria = mock(RegistroAuditoriaPort.class);

        processor = new RegistrarAuditoriaProcessor(mockFactory, mockRegistroAuditoria);

        auditoriaRequest = new Auditoria();
        auditoriaRequest.setUsuario_id(1);
        auditoriaRequest.setAccion("LOGIN");
        auditoriaRequest.setDescripcion("Usuario inicio sesion");
        auditoriaRequest.setIp_origen("192.168.1.1");
        auditoriaRequest.setTipo("BASICA");

        auditoriaProcesada = new Auditoria();
        auditoriaProcesada.setUsuario_id(1);
        auditoriaProcesada.setAccion("LOGIN");
        auditoriaProcesada.setDescripcion("Usuario inicio sesion");
        auditoriaProcesada.setIp_origen("192.168.1.1");
        auditoriaProcesada.setTipo("BASICA");

        auditoriaResponse = new AuditoriaResponse();
        auditoriaResponse.setUsuario(1);
        auditoriaResponse.setAccion("LOGIN");
        auditoriaResponse.setDescripcion("Usuario inicio sesion");
        auditoriaResponse.setTipo("BASICA");
    }

    @Test
    @DisplayName("Debe procesar auditoria con factory correctamente")
    void debeProcesarAuditoriaConFactoryCorrectamente() {
        when(mockFactory.crearAuditoria(any(Auditoria.class))).thenReturn(auditoriaProcesada);
        when(mockFactory.crearRespuesta(any(Auditoria.class))).thenReturn(auditoriaResponse);

        AuditoriaResponse resultado = processor.procesar(auditoriaRequest);

        assertNotNull(resultado);
        assertEquals(1, resultado.getUsuario());
        assertEquals("LOGIN", resultado.getAccion());
        assertEquals("BASICA", resultado.getTipo());

        verify(mockFactory, times(1)).crearAuditoria(any(Auditoria.class));
        verify(mockFactory, times(1)).crearRespuesta(any(Auditoria.class));
    }

    @Test
    @DisplayName("Debe usar Prototype para obtener auditoria base")
    void debeUsarPrototypeParaObtenerAuditoriaBase() {
        when(mockFactory.crearAuditoria(any(Auditoria.class))).thenReturn(auditoriaProcesada);
        when(mockFactory.crearRespuesta(any(Auditoria.class))).thenReturn(auditoriaResponse);

        processor.procesar(auditoriaRequest);

        verify(mockFactory, times(1)).crearAuditoria(any(Auditoria.class));
    }

    @Test
    @DisplayName("Debe delegar creacion de auditoria a la factory")
    void debeDelegarCreacionDeAuditoriaALaFactory() {
        when(mockFactory.crearAuditoria(any(Auditoria.class))).thenReturn(auditoriaProcesada);
        when(mockFactory.crearRespuesta(any(Auditoria.class))).thenReturn(auditoriaResponse);

        processor.procesar(auditoriaRequest);

        verify(mockFactory).crearAuditoria(any(Auditoria.class));
    }

    @Test
    @DisplayName("Debe delegar creacion de respuesta a la factory")
    void debeDelegarCreacionDeRespuestaALaFactory() {
        when(mockFactory.crearAuditoria(any(Auditoria.class))).thenReturn(auditoriaProcesada);
        when(mockFactory.crearRespuesta(any(Auditoria.class))).thenReturn(auditoriaResponse);

        processor.procesar(auditoriaRequest);

        verify(mockFactory).crearRespuesta(any(Auditoria.class));
    }

    @Test
    @DisplayName("Debe registrar auditoria en repositorio")
    void debeRegistrarAuditoriaEnRepositorio() {
        when(mockFactory.crearAuditoria(any(Auditoria.class))).thenReturn(auditoriaProcesada);
        when(mockFactory.crearRespuesta(any(Auditoria.class))).thenReturn(auditoriaResponse);

        processor.procesar(auditoriaRequest);

        verify(mockRegistroAuditoria).registrarAccion(auditoriaProcesada);
    }
}
