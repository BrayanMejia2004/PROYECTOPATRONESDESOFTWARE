package com.gobierno.servicio_auditoria.domain.prototype;

import java.util.HashMap;
import java.util.Map;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;

public class AuditoriaPrototypeRegistry {  // Registro de prototipos (patrón Prototype)

    private static final Map<String, Auditoria> prototipos = new HashMap<>();  // Mapa de prototipos

    static {  // Inicialización estática de los prototipos
        Auditoria basica = new Auditoria();  // Crea prototipo básico
        basica.setTipo("BASICA");  // Establece el tipo

        Auditoria seguridad = new Auditoria();  // Crea prototipo de seguridad
        seguridad.setTipo("SEGURIDAD");  // Establece el tipo

        Auditoria completa = new Auditoria();  // Crea prototipo completo
        completa.setTipo("COMPLETA");  // Establece el tipo

        prototipos.put("BASICA", basica);  // Registra el prototipo básico
        prototipos.put("SEGURIDAD", seguridad);  // Registra el prototipo de seguridad
        prototipos.put("COMPLETA", completa);  // Registra el prototipo completo
    }

    public static Auditoria obtenerPrototipo(String tipo) {  // Obtiene un prototipo por su tipo
        Auditoria prototipo = prototipos.get(tipo.toUpperCase());  // Busca el prototipo en el mapa

        if (prototipo == null) {  // Si el tipo no existe
            throw new IllegalArgumentException("Tipo de auditoria invalido");  // Lanza excepción
        }

        return prototipo.clone();  // Retorna una copia del prototipo (patrón Prototype)
    }
}