package com.gobierno.servicio_auditoria.tests.patrones.prototype;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Domain.Prototype.AuditoriaPrototypeRegistry;

// Test para el patron Prototype - AuditoriaPrototypeRegistry
class AuditoriaPrototypeRegistryTest {

    @Test
    @DisplayName("Debe obtener prototipo BASICA")
    void debeObtenerPrototipoBasica() {
        // Act
        Auditoria resultado = AuditoriaPrototypeRegistry.obtenerPrototipo("BASICA");

        // Assert
        assertNotNull(resultado);
        assertEquals("BASICA", resultado.getTipo());
    }

    @Test
    @DisplayName("Debe obtener prototipo SEGURIDAD")
    void debeObtenerPrototipoSeguridad() {
        // Act
        Auditoria resultado = AuditoriaPrototypeRegistry.obtenerPrototipo("SEGURIDAD");

        // Assert
        assertNotNull(resultado);
        assertEquals("SEGURIDAD", resultado.getTipo());
    }

    @Test
    @DisplayName("Debe obtener prototipo COMPLETA")
    void debeObtenerPrototipoCompleta() {
        // Act
        Auditoria resultado = AuditoriaPrototypeRegistry.obtenerPrototipo("COMPLETA");

        // Assert
        assertNotNull(resultado);
        assertEquals("COMPLETA", resultado.getTipo());
    }

    @Test
    @DisplayName("Debe retornar copia no instancia original")
    void debeRetornarCopiaNoInstanciaOriginal() {
        // Act
        Auditoria prototipo1 = AuditoriaPrototypeRegistry.obtenerPrototipo("BASICA");
        Auditoria prototipo2 = AuditoriaPrototypeRegistry.obtenerPrototipo("BASICA");

        // Modifica el primer prototipo
        prototipo1.setUsuario_id(999);

        // Assert - El segundo prototipo no debe verse afectado
        assertNotEquals(prototipo1, prototipo2);
        assertNull(prototipo2.getUsuario_id());
        assertEquals(999, prototipo1.getUsuario_id());
    }

    @Test
    @DisplayName("Debe lanzar excepcion para tipo invalido")
    void debeLanzarExcepcionParaTipoInvalido() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> AuditoriaPrototypeRegistry.obtenerPrototipo("INVALIDA")
        );

        assertEquals("Tipo de auditoria invalido", exception.getMessage());
    }
}
