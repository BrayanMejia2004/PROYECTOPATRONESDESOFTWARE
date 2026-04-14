package com.gobierno.servicio_auditoria.tests.patrones.abstractfactory;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.gobierno.servicio_auditoria.domain.factory.AuditoriaBasicaFactory;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;

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
        Auditoria resultado = factory.crearAuditoria(auditoria);

        assertNotNull(resultado);
        assertEquals("BASICA", resultado.getTipo());
    }

    @Test
    @DisplayName("Debe crear respuesta sin IP ni fecha")
    void debeCrearRespuestaSinIpNiFecha() {
        auditoria.setTipo("BASICA");

        AuditoriaResponse respuesta = factory.crearRespuesta(auditoria);

        assertNotNull(respuesta);
        assertEquals(1, respuesta.getUsuario());
        assertEquals("LOGIN", respuesta.getAccion());
        assertEquals("Inicio de sesion", respuesta.getDescripcion());
        assertEquals("BASICA", respuesta.getTipo());
        assertNull(respuesta.getIp());
        assertNull(respuesta.getFecha());
    }
}
