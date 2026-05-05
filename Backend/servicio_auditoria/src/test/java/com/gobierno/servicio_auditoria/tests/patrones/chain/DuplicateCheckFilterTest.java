package com.gobierno.servicio_auditoria.tests.patrones.chain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.chain.DuplicateCheckFilter;
import com.gobierno.servicio_auditoria.infrastructure.persistence.repository.AuditoriaJpaRepository;
import java.sql.Timestamp;
import java.util.Arrays;

class DuplicateCheckFilterTest {

    private AuditoriaJpaRepository auditoriaJpaRepository;
    private DuplicateCheckFilter filter;

    @BeforeEach
    void setUp() {
        auditoriaJpaRepository = mock(AuditoriaJpaRepository.class);
        filter = new DuplicateCheckFilter(auditoriaJpaRepository);
    }

    @Test
    @DisplayName("Debe retornar true sin duplicados")
    void debeRetornarTrueSinDuplicados() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(1);
        auditoria.setAccion("LOGIN");

        // Mock: no hay auditorías previas
        when(auditoriaJpaRepository.findByUsuarioId(1))
                .thenReturn(Arrays.asList());

        boolean resultado = filter.doFilter(auditoria);

        assertTrue(resultado);
        verify(auditoriaJpaRepository).findByUsuarioId(1);
    }

    @Test
    @DisplayName("Debe retornar false con duplicado reciente")
    void debeRetornarFalseConDuplicadoReciente() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(1);
        auditoria.setAccion("LOGIN");

        // Crear auditoría duplicada hace 1 segundo
        Auditoria duplicado = new Auditoria();
        duplicado.setAccion("LOGIN");
        duplicado.setFecha(new Timestamp(System.currentTimeMillis() - 1000)); // hace 1 segundo

        when(auditoriaJpaRepository.findByUsuarioId(1))
                .thenReturn(Arrays.asList(duplicado));

        boolean resultado = filter.doFilter(auditoria);

        assertFalse(resultado);
        verify(auditoriaJpaRepository).findByUsuarioId(1);
    }

    @Test
    @DisplayName("Debe retornar true con duplicado antiguo")
    void debeRetornarTrueConDuplicadoAntiguo() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(1);
        auditoria.setAccion("LOGIN");

        // Crear auditoría hace 10 segundos (fuera de ventana de 5 seg)
        Auditoria duplicado = new Auditoria();
        duplicado.setAccion("LOGIN");
        duplicado.setFecha(new Timestamp(System.currentTimeMillis() - 10000)); // hace 10 segundos

        when(auditoriaJpaRepository.findByUsuarioId(1))
                .thenReturn(Arrays.asList(duplicado));

        boolean resultado = filter.doFilter(auditoria);

        assertTrue(resultado);
        verify(auditoriaJpaRepository).findByUsuarioId(1);
    }

    @Test
    @DisplayName("Debe retornar false cuando usuario_id es null")
    void debeRetornarFalseCuandoUsuarioIdNull() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(null);
        auditoria.setAccion("LOGIN");

        boolean resultado = filter.doFilter(auditoria);

        assertFalse(resultado);
        verify(auditoriaJpaRepository, never()).findByUsuarioId(anyInt());
    }

    @Test
    @DisplayName("Debe retornar false cuando acción es null")
    void debeRetornarFalseCuandoAccionNull() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(1);
        auditoria.setAccion(null);

        boolean resultado = filter.doFilter(auditoria);

        assertFalse(resultado);
        verify(auditoriaJpaRepository, never()).findByUsuarioId(anyInt());
    }
}
