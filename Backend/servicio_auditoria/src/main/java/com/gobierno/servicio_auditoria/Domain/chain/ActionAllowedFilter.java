package com.gobierno.servicio_auditoria.domain.chain;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import java.util.Arrays;
import java.util.List;

public class ActionAllowedFilter extends AbstractAuditoriaFilter {  // Valida que la acción esté permitida
    
    // Acciones permitidas (basadas en las acciones usadas en el proyecto)
    private final List<String> accionesPermitidas = Arrays.asList(
        "LOGIN",
        "REGISTRO_USUARIO", 
        "REGISTRAR_PERFIL",
        "ACTUALIZAR_PERFIL",
        "ELIMINAR_USUARIO",
        "CREAR_ROL",
        "ACTUALIZAR_ROL",
        "ELIMINAR_ROL",
        "EDITAR_USUARIO",
        "GET /auditoria/lista",
        "POST /auditoria/registrar/BASICA",
        "POST /auditoria/registrar/COMPLETA",
        "POST /auditoria/registrar/SEGURIDAD"
    );
    
    @Override
    public boolean doFilter(Auditoria auditoria) {  // Valida que la acción esté permitida
        if (auditoria.getAccion() == null || auditoria.getAccion().isEmpty()) {
            return false;  // Acción inválida
        }
        
        // Verifica si la acción está en la lista de permitidas
        for (String accionPermitida : accionesPermitidas) {
            if (accionPermitida.equals(auditoria.getAccion())) {
                return doNext(auditoria);  // Acción válida, pasa al siguiente filtro
            }
        }
        
        return false;  // Acción no permitida
    }
}
