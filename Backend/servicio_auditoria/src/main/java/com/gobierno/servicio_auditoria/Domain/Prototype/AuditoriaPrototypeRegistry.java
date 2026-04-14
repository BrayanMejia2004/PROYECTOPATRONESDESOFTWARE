package com.gobierno.servicio_auditoria.domain.prototype;

import java.util.HashMap;
import java.util.Map;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;

public class AuditoriaPrototypeRegistry {

    private static final Map<String, Auditoria> prototipos = new HashMap<>();

    static {
        
        Auditoria basica = new Auditoria();
        basica.setTipo("BASICA");

        Auditoria seguridad = new Auditoria();
        seguridad.setTipo("SEGURIDAD");

        Auditoria completa = new Auditoria();
        completa.setTipo("COMPLETA");

        prototipos.put("BASICA", basica);
        prototipos.put("SEGURIDAD", seguridad);
        prototipos.put("COMPLETA", completa);
    }

    public static Auditoria obtenerPrototipo(String tipo) {
        Auditoria prototipo = prototipos.get(tipo.toUpperCase());

        if (prototipo == null) {
            throw new IllegalArgumentException("Tipo de auditoria invalido");
        }

        return prototipo.clone();
    }
}
