package com.gobierno.servicio_auditoria.tests.patrones.prototype;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.prototype.AuditoriaPrototypeRegistry;

class AuditoriaPrototypeRegistryTest {

    @Test
    @DisplayName("Debe obtener prototipo BASICA")
    void debeObtenerPrototipoBasica() {
        Auditoria resultado = AuditoriaPrototypeRegistry.obtenerPrototipo("BASICA");

        assertNotNull(resultado);
        assertEquals("BASICA", resultado.getTipo());
    }

    @Test
    @DisplayName("Debe obtener prototipo SEGURIDAD")
    void debeObtenerPrototipoSeguridad() {
        Auditoria resultado = AuditoriaPrototypeRegistry.obtenerPrototipo("SEGURIDAD");

        assertNotNull(resultado);
        assertEquals("SEGURIDAD", resultado.getTipo());
    }

    @Test
    @DisplayName("Debe obtener prototipo COMPLETA")
    void debeObtenerPrototipoCompleta() {
        Auditoria resultado = AuditoriaPrototypeRegistry.obtenerPrototipo("COMPLETA");

        assertNotNull(resultado);
        assertEquals("COMPLETA", resultado.getTipo());
    }

    @Test
    @DisplayName("Debe retornar copia no instancia original")
    void debeRetornarCopiaNoInstanciaOriginal() {
        Auditoria prototipo1 = AuditoriaPrototypeRegistry.obtenerPrototipo("BASICA");
        Auditoria prototipo2 = AuditoriaPrototypeRegistry.obtenerPrototipo("BASICA");

        prototipo1.setUsuario_id(999);

        assertNotEquals(prototipo1, prototipo2);
        assertNull(prototipo2.getUsuario_id());
        assertEquals(999, prototipo1.getUsuario_id());
    }

    @Test
    @DisplayName("Debe lanzar excepcion para tipo invalido")
    void debeLanzarExcepcionParaTipoInvalido() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> AuditoriaPrototypeRegistry.obtenerPrototipo("INVALIDA")
        );

        assertEquals("Tipo de auditoria invalido", exception.getMessage());
    }
}
