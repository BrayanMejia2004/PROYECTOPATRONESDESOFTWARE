package com.gobierno.servicio_auditoria.Domain.Prototype;

import java.util.HashMap;
import java.util.Map;

import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;

public class AuditoriaPrototypeRegistry {

    //Se almacena los protototipos base de cada auditoria
    private static final Map<String, Auditoria> prototipos = new HashMap<>();

    static {

        Auditoria basica = new Auditoria();
        basica.setIp_origen(null);
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

        //Se busca el prototipo segun el tipo solicitado
        Auditoria prototipo = prototipos.get(tipo.toUpperCase());

        if (prototipo == null) {
            throw new IllegalArgumentException("Tipo de auditoria invalido");
        }

        return prototipo.clone();
    }

}
