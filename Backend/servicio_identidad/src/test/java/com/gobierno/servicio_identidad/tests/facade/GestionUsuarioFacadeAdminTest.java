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
import com.gobierno.servicio_identidad.application.usecases.EliminarUsuarioUseCase;
import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AutorizacionClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AuditoriaClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioListaResponse;

import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class GestionUsuarioFacadeAdminTest {

    @Mock
    private UsuarioRepositorioPort usuarioRepositorioPort;

    @Mock
    private AutorizacionClient autorizacionClient;

    @Mock
    private AuditoriaClient auditoriaClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EliminarUsuarioUseCase eliminarUsuarioUseCase;

    private GestionUsuarioFacadeImpl facade;

    @BeforeEach
    void setUp() {
        facade = new GestionUsuarioFacadeImpl(
                usuarioRepositorioPort,
                null,
                null,
                null,
                eliminarUsuarioUseCase,
                autorizacionClient,
                auditoriaClient,
                passwordEncoder);
    }

    // Test 1: Verifica que el admin puede eliminar usuario y se registra auditoría
    @Test
    void eliminarUsuario_adminEliminaUsuario_registraAuditoriaYElimina() {
        Usuario usuario = TestDataFactory.crearUsuario(1L, "testuser", "test@test.com");

        when(usuarioRepositorioPort.buscarPorUsername("testuser")).thenReturn(Optional.of(usuario));
        doNothing().when(eliminarUsuarioUseCase).ejecutar("testuser");

        facade.eliminarUsuario("testuser");

        verify(auditoriaClient).registrarAuditoria(eq(1), eq("ELIMINAR_USUARIO"), anyString(), eq("SEGURIDAD"));
        verify(eliminarUsuarioUseCase).ejecutar("testuser");
    }

    // Test 2: Verifica que no falla si el usuario no existe
    @Test
    void eliminarUsuario_usuarioNoExiste_noLanzaExcepcion() {
        when(usuarioRepositorioPort.buscarPorUsername("nonexistent")).thenReturn(Optional.empty());
        doNothing().when(eliminarUsuarioUseCase).ejecutar("nonexistent");

        assertDoesNotThrow(() -> facade.eliminarUsuario("nonexistent"));
    }

    // Test 3: Verifica que el admin puede eliminar usuario de otro usuario
    @Test
    void eliminarUsuarioAdmin_adminEliminaOtroUsuario_registraAuditoriaYElimina() {
        Usuario usuario = TestDataFactory.crearUsuario(2L, "targetuser", "target@test.com");

        when(usuarioRepositorioPort.buscarPorUsername("targetuser")).thenReturn(Optional.of(usuario));
        doNothing().when(autorizacionClient).quitarTodosLosRolesDeUsuario("targetuser");
        doNothing().when(eliminarUsuarioUseCase).ejecutar("targetuser");

        facade.eliminarUsuarioAdmin("admin", "targetuser");

        verify(auditoriaClient).registrarAuditoria(eq(2), eq("ELIMINAR_USUARIO"), anyString(), eq("SEGURIDAD"));
        verify(autorizacionClient).quitarTodosLosRolesDeUsuario("targetuser");
        verify(eliminarUsuarioUseCase).ejecutar("targetuser");
    }

    // Test 4: Verifica la asignación de rol a un usuario
    @Test
    void asignarRol_usuarioValido_asignaRolCorrectamente() {
        doNothing().when(autorizacionClient).asignarRolAUsuario("testuser", "ADMIN");

        facade.asignarRol("testuser", "ADMIN");

        verify(autorizacionClient).asignarRolAUsuario("testuser", "ADMIN");
    }

    // Test 5: Verifica la remoción de rol de un usuario
    @Test
    void quitarRol_usuarioConRol_quitaRolExitosamente() {
        doNothing().when(autorizacionClient).quitarRolAUsuario("testuser", "ADMIN");

        facade.quitarRol("testuser", "ADMIN");

        verify(autorizacionClient).quitarRolAUsuario("testuser", "ADMIN");
    }

    // Test 6: Verifica que obtiene los roles del usuario
    @Test
    void obtenerRoles_retornaListaDeRoles() {
        when(autorizacionClient.obtenerRolesDeUsuario("testuser")).thenReturn(List.of("USER", "ADMIN"));

        List<String> roles = facade.obtenerRoles("testuser");

        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.contains("USER"));
        assertTrue(roles.contains("ADMIN"));
    }

    // Test 7: Verifica que retorna la lista de roles disponibles
    @Test
    void listarRolesDisponibles_retornaListaRoles() {
        when(autorizacionClient.obtenerListaRoles()).thenReturn(List.of("ADMIN", "USER", "AUDITOR"));

        List<String> roles = facade.listarRolesDisponibles();

        assertNotNull(roles);
        assertEquals(3, roles.size());
    }

    // Test 8: Verifica que el admin puede actualizar datos de otro usuario
    @Test
    void actualizarUsuarioAdmin_datosValidos_actualizaUsuario() {
        Usuario usuario = TestDataFactory.crearUsuario(1L, "testuser", "test@test.com");

        when(usuarioRepositorioPort.buscarPorUsername("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("newpass")).thenReturn("$2a$10$encoded");
        when(usuarioRepositorioPort.guardar(any(Usuario.class))).thenReturn(usuario);

        UsuarioListaResponse response = facade.actualizarUsuarioAdmin("admin", "testuser", "newuser", "new@test.com", "newpass");

        assertNotNull(response);
        verify(usuarioRepositorioPort).guardar(any(Usuario.class));
    }

    // Test 9: Verifica que el admin no puede editarse a sí mismo
    @Test
    void actualizarUsuarioAdmin_editarseASiMismo_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () -> 
            facade.actualizarUsuarioAdmin("admin", "admin", "newname", "new@test.com", "pass"));
    }

    // Test 10: Verifica que lanza excepción cuando el usuario a editar no existe
    @Test
    void actualizarUsuarioAdmin_usuarioNoExiste_lanzaExcepcion() {
        when(usuarioRepositorioPort.buscarPorUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            facade.actualizarUsuarioAdmin("admin", "nonexistent", "newname", "new@test.com", "pass"));
    }

    // Test 11: Verifica que no permite username duplicado al actualizar
    @Test
    void actualizarUsuarioAdmin_usernameDuplicado_lanzaExcepcion() {
        Usuario usuario = TestDataFactory.crearUsuario(1L, "testuser", "test@test.com");

        when(usuarioRepositorioPort.buscarPorUsername("testuser")).thenReturn(Optional.of(usuario));
        when(usuarioRepositorioPort.existePorUsername("existing")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> 
            facade.actualizarUsuarioAdmin("admin", "testuser", "existing", null, null));
    }
}