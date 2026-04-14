package com.gobierno.servicio_auditoria.tests.patrones.abstractfactory;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.gobierno.servicio_auditoria.domain.factory.AuditoriaSeguridadFactory;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;

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
        Auditoria resultado = factory.crearAuditoria(auditoria);

        assertNotNull(resultado);
        assertEquals("SEGURIDAD", resultado.getTipo());
    }

    @Test
    @DisplayName("Debe crear respuesta con IP")
    void debeCrearRespuestaConIp() {
        auditoria.setTipo("SEGURIDAD");

        AuditoriaResponse respuesta = factory.crearRespuesta(auditoria);

        assertNotNull(respuesta);
        assertEquals(2, respuesta.getUsuario());
        assertEquals("CAMBIAR_PASSWORD", respuesta.getAccion());
        assertEquals("SEGURIDAD", respuesta.getTipo());
        assertNotNull(respuesta.getIp());
        assertEquals("192.168.1.100", respuesta.getIp());
    }
}
