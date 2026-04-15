package com.gobierno.servicio_autorizacion.infrastructure.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gobierno.servicio_autorizacion.domain.entities.Permiso;
import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.ports.out.PermisoRepositoryPort;
import com.gobierno.servicio_autorizacion.domain.ports.out.RolesPermisosPort;
import com.gobierno.servicio_autorizacion.domain.ports.out.RolRepositoryPort;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initPermisosYRoles(PermisoRepositoryPort permisoRepositoryPort,
                                         RolesPermisosPort rolesPermisosPort,
                                         RolRepositoryPort rolRepositoryPort) {
        return args -> {
            crearPermisoSiNoExiste(permisoRepositoryPort, "VER_DASHBOARD", 
                    "Permite ver el dashboard", "/dashboard", "GET");
            crearPermisoSiNoExiste(permisoRepositoryPort, "GENERAR_REPORTE", 
                    "Permite generar reportes", "/reportes", "POST");
            crearPermisoSiNoExiste(permisoRepositoryPort, "DESCARGAR_REPORTE", 
                    "Permite descargar reportes", "/reportes", "GET");
            crearPermisoSiNoExiste(permisoRepositoryPort, "ADMINISTRAR_USUARIOS", 
                    "Permite administrar usuarios", "/usuarios", "*");
            crearPermisoSiNoExiste(permisoRepositoryPort, "ADMINISTRAR_ROLES", 
                    "Permite administrar roles y permisos", "/roles", "*");
            crearPermisoSiNoExiste(permisoRepositoryPort, "VER_AUDITORIA", 
                    "Permite ver el registro de auditoría", "/auditoria", "GET");
            crearPermisoSiNoExiste(permisoRepositoryPort, "TODOS", 
                    "Permiso total sobre todos los recursos", "*", "*");

            Rol rolAdmin = crearRolSiNoExiste(rolRepositoryPort, "ADMIN", "Administrador con control total del sistema");
            Rol rolUser = crearRolSiNoExiste(rolRepositoryPort, "USER", "Usuario con acceso basico");
            Rol rolAuditor = crearRolSiNoExiste(rolRepositoryPort, "AUDITOR", "Usuario con permisos de auditoria");

            rolesPermisosPort.eliminarPorRol(rolAdmin);
            rolesPermisosPort.eliminarPorRol(rolUser);
            rolesPermisosPort.eliminarPorRol(rolAuditor);

            asignarPermisoSiExiste(permisoRepositoryPort, rolesPermisosPort, 
                    rolAdmin, "TODOS");
            
            asignarPermisoSiExiste(permisoRepositoryPort, rolesPermisosPort, 
                    rolUser, "VER_DASHBOARD");
            
            asignarPermisoSiExiste(permisoRepositoryPort, rolesPermisosPort, 
                    rolAuditor, "VER_DASHBOARD");
            asignarPermisoSiExiste(permisoRepositoryPort, rolesPermisosPort, 
                    rolAuditor, "GENERAR_REPORTE");
            asignarPermisoSiExiste(permisoRepositoryPort, rolesPermisosPort, 
                    rolAuditor, "DESCARGAR_REPORTE");
        };
    }

    private void crearPermisoSiNoExiste(PermisoRepositoryPort repository,
                                         String nombre, String descripcion,
                                         String recurso, String accion) {
        if (!repository.existePorNombre(nombre)) {
            Permiso permiso = new Permiso(nombre, descripcion, recurso, accion);
            repository.guardar(permiso);
        }
    }

    private Rol crearRolSiNoExiste(RolRepositoryPort repository, String nombre, String descripcion) {
        return repository.findByNombre(nombre)
                .orElseGet(() -> {
                    Rol rol = new Rol(nombre, descripcion);
                    return repository.guardar(rol);
                });
    }

    private void asignarPermisoSiExiste(PermisoRepositoryPort permisoRepository,
                                         RolesPermisosPort rolesPermisosPort,
                                         Rol rol, String nombrePermiso) {
        permisoRepository.buscarPorNombre(nombrePermiso)
                .ifPresent(permiso -> rolesPermisosPort.asignarPermiso(rol, permiso));
    }
}
