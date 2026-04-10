package com.gobierno.servicio_auditoria.tests.patrones.abstractfactory;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.gobierno.servicio_auditoria.Domain.FactoryConcret.AuditoriaBasicaFactory;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;

// Test para Abstract Factory - AuditoriaBasicaFactory
class AuditoriaBasicaFactoryTest {

    private AuditoriaBasicaFactory factory;
    private Auditoria auditoria;

    @BeforeEach
    void setUp() {
        factory = new AuditoriaBasicaFactory();
        auditoria = new Auditoria();
        auditoria.setUsuario_id(1);
        auditoria.setAccion("LOGIN");
        auditoria.setDescripcion("Inicio de sesion");
        auditoria.setIp_origen("192.168.1.1");
    }

    @Test
    @DisplayName("Debe crear auditoria con tipo BASICA")
    void debeCrearAuditoriaConTipoBasica() {
        // Act
        Auditoria resultado = factory.crearAuditoria(auditoria);

        // Assert
        assertNotNull(resultado);
        assertEquals("BASICA", resultado.getTipo());
    }

    @Test
    @DisplayName("Debe crear respuesta sin IP ni fecha")
    void debeCrearRespuestaSinIpNiFecha() {
        // Arrange
        auditoria.setTipo("BASICA");

        // Act
        AuditoriaResponse respuesta = factory.crearRespuesta(auditoria);

        // Assert
        assertNotNull(respuesta);
        assertEquals(1, respuesta.getUsuario());
        assertEquals("LOGIN", respuesta.getAccion());
        assertEquals("Inicio de sesion", respuesta.getDescripcion());
        assertEquals("BASICA", respuesta.getTipo());
        assertNull(respuesta.getIp()); // No debe incluir IP
        assertNull(respuesta.getFecha()); // No debe incluir fecha
    }
}
