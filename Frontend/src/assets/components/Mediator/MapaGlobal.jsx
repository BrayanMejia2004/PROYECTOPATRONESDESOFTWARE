import { forwardRef, useImperativeHandle, useRef, useState } from 'react';
import { MapContainer, TileLayer, CircleMarker, Tooltip, useMap } from 'react-leaflet';
import { DashboardComponent } from './DashboardMediator';
import 'leaflet/dist/leaflet.css';

const COLOR_TIPO = {
  BASICA: '#1d9bf0',
  COMPLETA: '#00ba7c',
  SEGURIDAD: '#f4212e',
};

const MapaGlobal = forwardRef((props, ref) => {
  const [eventos, setEventos] = useState([]);
  const [filtros, setFiltros] = useState({});
  const mediatorRef = useRef(null);
  const mapRef = useRef(null);

  useImperativeHandle(ref, () => ({
    setMediator: (m) => { mediatorRef.current = m; },
    aplicarFiltros: (f) => { setFiltros(f || {}); },
    enfocarEvento: (evento) => {
      if (evento?.latitud && evento?.longitud && mapRef.current) {
        mapRef.current.flyTo([evento.latitud, evento.longitud], 5, { duration: 1.5 });
      }
    },
    agregarEvento: (evento) => {
      setEventos(prev => [...prev, evento]);
    },
    filtrarPorTipo: (tipo) => {
      setFiltros(prev => ({ ...prev, tipo }));
    },
  }));

  const MapController = () => {
    mapRef.current = useMap();
    return null;
  };

  const handlePaisClick = (pais) => {
    mediatorRef.current?.notify(null, 'PAIS_SELECCIONADO', pais);
  };

  const eventosFiltrados = eventos.filter((e) => {
    if (filtros.tipo && e.tipo !== filtros.tipo) return false;
    if (filtros.pais && e.pais !== filtros.pais) return false;
    if (!e.latitud && !e.longitud) return false;
    return true;
  });

  const eventosSinUbicacion = eventos.filter((e) => !e.latitud && !e.longitud).length;

  const agruparPorUbicacion = () => {
    const grupos = {};
    eventosFiltrados.forEach((e) => {
      const key = `${e.latitud},${e.longitud}`;
      if (!grupos[key]) {
        grupos[key] = { ...e, count: 0 };
      }
      grupos[key].count += 1;
    });
    return Object.values(grupos);
  };

  const ubicaciones = agruparPorUbicacion();

  return (
    <div className="mapa-global" style={{ height: '400px', borderRadius: '14px', overflow: 'hidden', position: 'relative' }}>
      {eventosSinUbicacion > 0 && (
        <div style={{
          position: 'absolute', top: 8, right: 8, zIndex: 1000,
          background: 'rgba(0,0,0,0.7)', color: '#8899a6',
          padding: '4px 10px', borderRadius: '8px', fontSize: '0.75rem',
        }}>
          {eventosSinUbicacion} evento(s) sin ubicación
        </div>
      )}
      <MapContainer
        center={[20, 0]}
        zoom={2}
        style={{ height: '100%', width: '100%' }}
        scrollWheelZoom={true}
      >
        <MapController />
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {ubicaciones.map((loc, idx) => (
          <CircleMarker
            key={`${loc.latitud}-${loc.longitud}-${idx}`}
            center={[loc.latitud, loc.longitud]}
            radius={Math.min(8 + loc.count * 2, 24)}
            pathOptions={{
              color: COLOR_TIPO[loc.tipo] || '#8899a6',
              fillColor: COLOR_TIPO[loc.tipo] || '#8899a6',
              fillOpacity: 0.5,
              weight: 2,
            }}
            eventHandlers={{
              click: () => handlePaisClick(loc.pais),
            }}
          >
            <Tooltip>
              <div style={{ fontSize: '0.8rem' }}>
                <strong>{loc.pais || 'Desconocido'}</strong>
                {loc.ciudad && <span> — {loc.ciudad}</span>}
                <br />
                <span style={{ color: '#8899a6' }}>{loc.ipOrigen}</span>
                <br />
                <span>Eventos: <strong>{loc.count}</strong></span>
              </div>
            </Tooltip>
          </CircleMarker>
        ))}
      </MapContainer>
    </div>
  );
});

MapaGlobal.displayName = 'MapaGlobal';
export default MapaGlobal;
