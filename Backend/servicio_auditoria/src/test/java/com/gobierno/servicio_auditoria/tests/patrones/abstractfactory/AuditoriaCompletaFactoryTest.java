package com.gobierno.servicio_auditoria.tests.patrones.abstractfactory;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.gobierno.servicio_auditoria.Domain.FactoryConcret.AuditoriaCompletaFactory;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;

// Test para Abstract Factory - AuditoriaCompletaFactory
class AuditoriaCompletaFactoryTest {

    private AuditoriaCompletaFactory factory;
    private Auditoria auditoria;

    @BeforeEach
    void setUp() {
        factory = new AuditoriaCompletaFactory();
        auditoria = new Auditoria();
        auditoria.setUsuario_id(3);
        auditoria.setAccion("ELIMINAR_USUARIO");
        auditoria.setDescripcion("Usuario eliminado");
        auditoria.setIp_origen("192.168.1.200");
    }

    @Test
    @DisplayName("Debe crear auditoria con tipo COMPLETA")
    void debeCrearAuditoriaConTipoCompleta() {
        // Act
        Auditoria resultado = factory.crearAuditoria(auditoria);

        // Assert
        assertNotNull(resultado);
        assertEquals("COMPLETA", resultado.getTipo());
    }

    @Test
    @DisplayName("Debe crear auditoria con fecha actual")
    void debeCrearAuditoriaConFechaActual() {
        // Act
        Timestamp antes = new Timestamp(System.currentTimeMillis());
        Auditoria resultado = factory.crearAuditoria(auditoria);
        Timestamp despues = new Timestamp(System.currentTimeMillis());

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getFecha());
        assertTrue(resultado.getFecha().compareTo(antes) >= 0);
        assertTrue(resultado.getFecha().compareTo(despues) <= 0);
    }

    @Test
    @DisplayName("Debe crear respuesta con IP y fecha")
    void debeCrearRespuestaConIpYFecha() {
        // Arrange
        auditoria.setTipo("COMPLETA");
        auditoria.setFecha(new Timestamp(System.currentTimeMillis()));

        // Act
        AuditoriaResponse respuesta = factory.crearRespuesta(auditoria);

        // Assert
        assertNotNull(respuesta);
        assertEquals(3, respuesta.getUsuario());
        assertEquals("ELIMINAR_USUARIO", respuesta.getAccion());
        assertEquals("COMPLETA", respuesta.getTipo());
        assertNotNull(respuesta.getIp()); // Debe incluir IP
        assertEquals("192.168.1.200", respuesta.getIp());
        assertNotNull(respuesta.getFecha()); // Debe incluir fecha
    }
}
