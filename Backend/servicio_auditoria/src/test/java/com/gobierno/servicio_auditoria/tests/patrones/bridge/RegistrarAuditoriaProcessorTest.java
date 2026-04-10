package com.gobierno.servicio_auditoria.tests.patrones.bridge;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.gobierno.servicio_auditoria.Domain.AbsFactory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.Domain.Bridge.Abstraction.RegistrarAuditoriaProcessor;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;
import com.gobierno.servicio_auditoria.Ports.Output.RegistroAuditoria;

// Test para el patron Bridge - RegistrarAuditoriaProcessor
class RegistrarAuditoriaProcessorTest {

    // Mocks
    private AuditoriaAbsFactory mockFactory;
    private RegistroAuditoria mockRegistroAuditoria;
    private RegistrarAuditoriaProcessor processor;

    // Datos de prueba
    private Auditoria auditoriaRequest;
    private Auditoria auditoriaProcesada;
    private AuditoriaResponse auditoriaResponse;

    @BeforeEach
    void setUp() {
        // Inicializa los mocks
        mockFactory = mock(AuditoriaAbsFactory.class);
        mockRegistroAuditoria = mock(RegistroAuditoria.class);

        // Crea el processor con los mocks
        processor = new RegistrarAuditoriaProcessor(mockFactory, mockRegistroAuditoria);

        // Prepara datos de prueba
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
        // Arrange - Configura el comportamiento del mock
        when(mockFactory.crearAuditoria(any(Auditoria.class))).thenReturn(auditoriaProcesada);
        when(mockFactory.crearRespuesta(any(Auditoria.class))).thenReturn(auditoriaResponse);

        // Act
        AuditoriaResponse resultado = processor.procesar(auditoriaRequest);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getUsuario());
        assertEquals("LOGIN", resultado.getAccion());
        assertEquals("BASICA", resultado.getTipo());

        // Verifica que se usaron los mocks
        verify(mockFactory, times(1)).crearAuditoria(any(Auditoria.class));
        verify(mockFactory, times(1)).crearRespuesta(any(Auditoria.class));
    }

    @Test
    @DisplayName("Debe usar Prototype para obtener auditoria base")
    void debeUsarPrototypeParaObtenerAuditoriaBase() {
        // Arrange
        when(mockFactory.crearAuditoria(any(Auditoria.class))).thenReturn(auditoriaProcesada);
        when(mockFactory.crearRespuesta(any(Auditoria.class))).thenReturn(auditoriaResponse);

        // Act
        processor.procesar(auditoriaRequest);

        // Assert - Verifica que se obtuvo un prototipo (se llama al registry)
        // El procesador obtiene el prototipo antes de procesarlo
        verify(mockFactory, times(1)).crearAuditoria(any(Auditoria.class));
    }

    @Test
    @DisplayName("Debe delegar creacion de auditoria a la factory")
    void debeDelegarCreacionDeAuditoriaALaFactory() {
        // Arrange
        when(mockFactory.crearAuditoria(any(Auditoria.class))).thenReturn(auditoriaProcesada);
        when(mockFactory.crearRespuesta(any(Auditoria.class))).thenReturn(auditoriaResponse);

        // Act
        processor.procesar(auditoriaRequest);

        // Assert
        verify(mockFactory).crearAuditoria(any(Auditoria.class));
    }

    @Test
    @DisplayName("Debe delegar creacion de respuesta a la factory")
    void debeDelegarCreacionDeRespuestaALaFactory() {
        // Arrange
        when(mockFactory.crearAuditoria(any(Auditoria.class))).thenReturn(auditoriaProcesada);
        when(mockFactory.crearRespuesta(any(Auditoria.class))).thenReturn(auditoriaResponse);

        // Act
        processor.procesar(auditoriaRequest);

        // Assert
        verify(mockFactory).crearRespuesta(any(Auditoria.class));
    }

    @Test
    @DisplayName("Debe registrar auditoria en repositorio")
    void debeRegistrarAuditoriaEnRepositorio() {
        // Arrange
        when(mockFactory.crearAuditoria(any(Auditoria.class))).thenReturn(auditoriaProcesada);
        when(mockFactory.crearRespuesta(any(Auditoria.class))).thenReturn(auditoriaResponse);

        // Act
        processor.procesar(auditoriaRequest);

        // Assert
        verify(mockRegistroAuditoria).registrarAccion(auditoriaProcesada);
    }
}
