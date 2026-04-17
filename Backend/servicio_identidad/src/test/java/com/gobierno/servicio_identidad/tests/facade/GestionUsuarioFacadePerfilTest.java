package com.gobierno.servicio_identidad.tests.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gobierno.servicio_identidad.application.facade.GestionUsuarioFacadeImpl;
import com.gobierno.servicio_identidad.application.usecases.ActualizarPerfilUseCase;
import com.gobierno.servicio_identidad.application.usecases.RegistrarPerfilUseCase;
import com.gobierno.servicio_identidad.domain.entities.PerfilUsuario;
import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AutorizacionClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AuditoriaClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioCompletoResponse;

import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class GestionUsuarioFacadePerfilTest {

    @Mock
    private UsuarioRepositorioPort usuarioRepositorioPort;

    @Mock
    private RegistrarPerfilUseCase registrarPerfilUseCase;

    @Mock
    private ActualizarPerfilUseCase actualizarPerfilUseCase;

    @Mock
    private AutorizacionClient autorizacionClient;

    @Mock
    private AuditoriaClient auditoriaClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    private GestionUsuarioFacadeImpl facade;

    @BeforeEach
    void setUp() {
        facade = new GestionUsuarioFacadeImpl(
                usuarioRepositorioPort,
                registrarPerfilUseCase,
                actualizarPerfilUseCase,
                null,
                null,
                autorizacionClient,
                auditoriaClient,
                passwordEncoder);
    }

    // Test 1: Verifica que retorna el perfil cuando el usuario tiene uno registrado
    @Test
    void obtenerPerfil_usuarioExiste_retornaPerfil() {
        Usuario usuario = TestDataFactory.crearUsuario(1L, "testuser", "test@test.com");
        PerfilUsuario perfil = TestDataFactory.crearPerfil("Juan", "Perez", "123456789");

        when(usuarioRepositorioPort.buscarPorUsername("testuser")).thenReturn(Optional.of(usuario));
        when(registrarPerfilUseCase.obtenerPerfilPorUsername("testuser")).thenReturn(perfil);

        PerfilResponse response = facade.obtenerPerfil("testuser");

        assertNotNull(response);
        assertEquals("Juan", response.getNombre());
        assertEquals("Perez", response.getApellido());
        assertEquals("123456789", response.getTelefono());
        assertEquals("test@test.com", response.getEmail());
    }

    // Test 2: Verifica que retorna null cuando el usuario no existe
    @Test
    void obtenerPerfil_usuarioNoExiste_retornaNull() {
        when(usuarioRepositorioPort.buscarPorUsername("nonexistent")).thenReturn(Optional.empty());

        PerfilResponse response = facade.obtenerPerfil("nonexistent");

        assertNull(response);
    }

    // Test 3: Verifica que retorna null cuando el usuario no tiene perfil
    @Test
    void obtenerPerfil_sinPerfil_retornaNull() {
        Usuario usuario = TestDataFactory.crearUsuario(1L, "testuser", "test@test.com");

        when(usuarioRepositorioPort.buscarPorUsername("testuser")).thenReturn(Optional.of(usuario));
        when(registrarPerfilUseCase.obtenerPerfilPorUsername("testuser")).thenReturn(null);

        PerfilResponse response = facade.obtenerPerfil("testuser");

        assertNull(response);
    }

    // Test 4: Verifica el registro exitoso de un perfil de usuario
    @Test
    void registrarPerfil_datosValidos_retornaPerfilResponse() {
        Usuario usuario = TestDataFactory.crearUsuario(1L, "testuser", "test@test.com");
        PerfilUsuario perfil = TestDataFactory.crearPerfil(1, "Juan", "Perez", "123456789");
        PerfilRequest request = new PerfilRequest("Juan", "Perez", "123456789");

        when(usuarioRepositorioPort.buscarPorUsername("testuser")).thenReturn(Optional.of(usuario));
        when(registrarPerfilUseCase.ejecutar(1, "Juan", "Perez", "123456789")).thenReturn(perfil);

        PerfilResponse response = facade.registrarPerfil("testuser", request);

        assertNotNull(response);
        assertEquals("Juan", response.getNombre());
        assertEquals("Perez", response.getApellido());
        verify(auditoriaClient).registrarAuditoria(eq(1), eq("REGISTRAR_PERFIL"), anyString(), eq("COMPLETA"));
    }

    // Test 5: Verifica que lanza excepción cuando el usuario no existe
    @Test
    void registrarPerfil_usuarioNoExiste_lanzaExcepcion() {
        PerfilRequest request = new PerfilRequest("Juan", "Perez", "123456789");

        when(usuarioRepositorioPort.buscarPorUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> facade.registrarPerfil("nonexistent", request));
    }

    // Test 6: Verifica la actualización exitosa del perfil
    @Test
    void actualizarPerfil_datosValidos_retornaPerfilActualizado() {
        Usuario usuario = TestDataFactory.crearUsuario(1L, "testuser", "test@test.com");
        PerfilUsuario perfil = TestDataFactory.crearPerfil(1, "Juan", "Perez", "987654321");
        PerfilRequest request = new PerfilRequest("Juan", "Perez", "987654321");

        when(usuarioRepositorioPort.buscarPorUsername("testuser")).thenReturn(Optional.of(usuario));
        when(actualizarPerfilUseCase.ejecutar(1, "Juan", "Perez", "987654321")).thenReturn(perfil);

        PerfilResponse response = facade.actualizarPerfil("testuser", request);

        assertNotNull(response);
        assertEquals("987654321", response.getTelefono());
        verify(auditoriaClient).registrarAuditoria(eq(1), eq("ACTUALIZAR_PERFIL"), anyString(), eq("COMPLETA"));
    }

    // Test 7: Verifica que retorna información completa del usuario
    @Test
    void obtenerUsuarioCompleto_conPerfilYRoles_retornaRespuestaCompleta() {
        Usuario usuario = TestDataFactory.crearUsuario(1L, "testuser", "test@test.com");
        PerfilUsuario perfil = TestDataFactory.crearPerfil("Juan", "Perez", "123456789");

        when(usuarioRepositorioPort.buscarPorUsername("testuser")).thenReturn(Optional.of(usuario));
        when(autorizacionClient.obtenerRolesDeUsuario("testuser")).thenReturn(List.of("USER", "ADMIN"));
        when(registrarPerfilUseCase.obtenerPerfilPorUsername("testuser")).thenReturn(perfil);

        UsuarioCompletoResponse response = facade.obtenerUsuarioCompleto("testuser");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals(2, response.getRoles().size());
        assertNotNull(response.getPerfil());
        assertEquals("Juan", response.getPerfil().getNombre());
    }
}