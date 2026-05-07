package com.gobierno.servicio_auditoria.domain.ports.out;

import java.util.List;
import java.util.Map;

public interface IdentidadClientPort {
    List<Long> obtenerTodosLosUsuarioIds();
    Map<Long, String> obtenerMapaUsuarios();
}
