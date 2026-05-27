const cacheGeo = new Map();

function esIPPrivada(ip) {
  if (!ip || typeof ip !== 'string') return true;
  if (ip === '::1' || ip === 'localhost') return true;
  const partes = ip.split('.');
  if (partes.length !== 4) return true;
  const [a, b] = partes.map(Number);
  if (isNaN(a) || isNaN(b)) return true;
  if (a === 10) return true;
  if (a === 127) return true;
  if (a === 169 && b === 254) return true;
  if (a === 192 && b === 168) return true;
  if (a === 172 && b >= 16 && b <= 31) return true;
  return false;
}

async function consultarAPI(ip) {
  try {
    const respuesta = await fetch(`http://ip-api.com/json/${ip}?fields=status,lat,lon,country,city`);
    if (!respuesta.ok) return null;
    const datos = await respuesta.json();
    if (datos.status !== 'success') return null;
    return {
      lat: datos.lat,
      lon: datos.lon,
      pais: datos.country,
      ciudad: datos.city,
    };
  } catch {
    return null;
  }
}

const FALLBACK = { lat: 0, lon: 0, pais: 'Desconocido', ciudad: '' };

export async function buscarUbicacionPorIP(ip) {
  if (esIPPrivada(ip)) return { ...FALLBACK };
  const cache = cacheGeo.get(ip);
  if (cache) return cache;
  const resultado = await consultarAPI(ip);
  const geo = resultado || { ...FALLBACK };
  cacheGeo.set(ip, geo);
  return geo;
}

export async function enriquecerEventosConGeo(eventos) {
  if (!eventos || eventos.length === 0) return eventos;
  const ipsUnicas = [...new Set(eventos.map((e) => e.ipOrigen).filter(Boolean))];
  const ipsPublicas = ipsUnicas.filter((ip) => !esIPPrivada(ip));

  const geoMap = new Map();
  for (const ip of ipsPublicas) {
    if (!cacheGeo.has(ip)) {
      const geo = await consultarAPI(ip);
      const resultado = geo || { ...FALLBACK };
      cacheGeo.set(ip, resultado);
      geoMap.set(ip, resultado);
      await new Promise((r) => setTimeout(r, 1500));
    } else {
      geoMap.set(ip, cacheGeo.get(ip));
    }
  }

  return eventos.map((e) => {
    if (!e.ipOrigen) {
      return { ...e, ...FALLBACK };
    }
    if (esIPPrivada(e.ipOrigen)) {
      return { ...e, ...FALLBACK };
    }
    const geo = geoMap.get(e.ipOrigen) || cacheGeo.get(e.ipOrigen) || FALLBACK;
    return {
      ...e,
      latitud: geo.lat,
      longitud: geo.lon,
      pais: geo.pais,
      ciudad: geo.ciudad,
    };
  });
}

export function limpiarCacheGeo() {
  cacheGeo.clear();
}
