import { forwardRef, useRef, useImperativeHandle, useState, useEffect } from 'react';
import { DashboardComponent } from './DashboardMediator';

const COLOR_TIPO = {
  BASICA: '#1d9bf0',
  COMPLETA: '#00ba7c',
  SEGURIDAD: '#f4212e',
};

const ITEMS_POR_PAGINA = 20;

const formatearFecha = (fecha) => {
  if (!fecha) return '-';
  return new Date(fecha).toLocaleString('es-ES');
};

const LineaTiempoFilterable = forwardRef(({ eventos: eventosProp = [] }, ref) => {
  const [pagina, setPagina] = useState(1);
  const [filtroLocal, setFiltroLocal] = useState({});
  const [eventos, setEventos] = useState([]);
  const mediatorRef = useRef(null);

  useEffect(() => {
    if (eventosProp.length > 0) {
      setEventos(eventosProp);
    }
  }, [eventosProp]);

  useImperativeHandle(ref, () => ({
    setMediator: (m) => { mediatorRef.current = m; },
    aplicarFiltros: (f) => {
      setFiltroLocal(f || {});
      setPagina(1);
    },
    filtrarPorPais: (pais) => {
      setFiltroLocal(prev => ({ ...prev, pais }));
      setPagina(1);
    },
    agregarEvento: (evento) => {
      setEventos(prev => [evento, ...prev]);
    },
    filtrarPorTipo: (tipo) => {
      setFiltroLocal(prev => ({ ...prev, tipo }));
      setPagina(1);
    },
  }));

  const handleEventoClick = (evento) => {
    mediatorRef.current?.notify(null, 'EVENTO_SELECCIONADO', evento);
  };

  const eventosFiltrados = eventos
    .filter((e) => {
      if (filtroLocal.tipo && e.tipo !== filtroLocal.tipo) return false;
      if (filtroLocal.pais && e.pais !== filtroLocal.pais) return false;
      return true;
    })
    .sort((a, b) => new Date(b.fecha) - new Date(a.fecha));

  const eventosPaginados = eventosFiltrados.slice(0, pagina * ITEMS_POR_PAGINA);
  const hayMas = eventosPaginados.length < eventosFiltrados.length;

  return (
    <div className="linea-tiempo-filterable">
      {eventosPaginados.length === 0 ? (
        <div className="empty-message">No hay eventos para mostrar</div>
      ) : (
        <div className="timeline-list">
          {eventosPaginados.map((evento, idx) => (
            <div
              key={evento.id || idx}
              className="timeline-item"
              onClick={() => handleEventoClick(evento)}
              style={{ borderLeftColor: COLOR_TIPO[evento.tipo] || '#8899a6' }}
            >
              <div className="timeline-item-header">
                <span
                  className="timeline-tipo-badge"
                  style={{ background: `${COLOR_TIPO[evento.tipo] || '#8899a6'}22`, color: COLOR_TIPO[evento.tipo] || '#8899a6' }}
                >
                  {evento.tipo || 'N/A'}
                </span>
                <span className="timeline-fecha">{formatearFecha(evento.fecha)}</span>
              </div>
              <div className="timeline-item-body">
                <strong>{evento.accion}</strong>
                <p>{evento.descripcion || '-'}</p>
              </div>
              <div className="timeline-item-footer">
                <span className="timeline-ip">{evento.ipOrigen}</span>
                {evento.pais && <span className="timeline-pais">{evento.pais}</span>}
                {evento.usuarioId && <span className="timeline-usuario">ID: {evento.usuarioId}</span>}
              </div>
            </div>
          ))}
        </div>
      )}
      {hayMas && (
        <button className="btn-ver-mas" onClick={() => setPagina(p => p + 1)}>
          Ver más ({eventosFiltrados.length - eventosPaginados.length} restantes)
        </button>
      )}
    </div>
  );
});

LineaTiempoFilterable.displayName = 'LineaTiempoFilterable';
export default LineaTiempoFilterable;
