package com.gobierno.servicio_auditoria.tests.patrones.abstractfactory;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.gobierno.servicio_auditoria.Domain.FactoryConcret.AuditoriaSeguridadFactory;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;

// Test para Abstract Factory - AuditoriaSeguridadFactory
class AuditoriaSeguridadFactoryTest {

    private AuditoriaSeguridadFactory factory;
    private Auditoria auditoria;

    @BeforeEach
    void setUp() {
        factory = new AuditoriaSeguridadFactory();
        auditoria = new Auditoria();
        auditoria.setUsuario_id(2);
        auditoria.setAccion("CAMBIAR_PASSWORD");
        auditoria.setDescripcion("Password modificado");
        auditoria.setIp_origen("192.168.1.100");
    }

    @Test
    @DisplayName("Debe crear auditoria con tipo SEGURIDAD")
    void debeCrearAuditoriaConTipoSeguridad() {
        // Act
        Auditoria resultado = factory.crearAuditoria(auditoria);

        // Assert
        assertNotNull(resultado);
        assertEquals("SEGURIDAD", resultado.getTipo());
    }

    @Test
    @DisplayName("Debe crear respuesta con IP")
    void debeCrearRespuestaConIp() {
        // Arrange
        auditoria.setTipo("SEGURIDAD");

        // Act
        AuditoriaResponse respuesta = factory.crearRespuesta(auditoria);

        // Assert
        assertNotNull(respuesta);
        assertEquals(2, respuesta.getUsuario());
        assertEquals("CAMBIAR_PASSWORD", respuesta.getAccion());
        assertEquals("SEGURIDAD", respuesta.getTipo());
        assertNotNull(respuesta.getIp()); // Debe incluir IP
        assertEquals("192.168.1.100", respuesta.getIp());
    }
}
