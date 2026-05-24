import { useState, useCallback } from 'react';
import DashboardMediator from './DashboardMediator';

const TIPOS_AUDITORIA = ['', 'BASICA', 'COMPLETA', 'SEGURIDAD'];

const FiltrosPanel = ({ mediator, onFiltrosCambiados }) => {
  const [filtros, setFiltros] = useState({
    fechaDesde: '',
    fechaHasta: '',
    tipo: '',
    pais: '',
    usuarioId: '',
  });

  const handleChange = useCallback((campo, valor) => {
    setFiltros(prev => ({ ...prev, [campo]: valor }));
  }, []);

  const handleAplicar = useCallback(() => {
    const filtrosActivos = {};
    Object.entries(filtros).forEach(([k, v]) => {
      if (v !== '' && v !== null && v !== undefined) {
        filtrosActivos[k] = v;
      }
    });
    mediator?.notify(null, 'FILTROS_CAMBIADOS', filtrosActivos);
    onFiltrosCambiados?.(filtrosActivos);
  }, [filtros, mediator, onFiltrosCambiados]);

  const handleLimpiar = useCallback(() => {
    const limpio = { fechaDesde: '', fechaHasta: '', tipo: '', pais: '', usuarioId: '' };
    setFiltros(limpio);
    mediator?.notify(null, 'FILTROS_CAMBIADOS', {});
    onFiltrosCambiados?.({});
  }, [mediator, onFiltrosCambiados]);

  return (
    <div className="filtros-panel">
      <div className="filtros-row">
        <div className="filtro-group">
          <label>Fecha desde</label>
          <input
            type="date"
            value={filtros.fechaDesde}
            onChange={(e) => handleChange('fechaDesde', e.target.value)}
          />
        </div>
        <div className="filtro-group">
          <label>Fecha hasta</label>
          <input
            type="date"
            value={filtros.fechaHasta}
            onChange={(e) => handleChange('fechaHasta', e.target.value)}
          />
        </div>
        <div className="filtro-group">
          <label>Tipo</label>
          <select value={filtros.tipo} onChange={(e) => handleChange('tipo', e.target.value)}>
            <option value="">Todos</option>
            {TIPOS_AUDITORIA.filter(t => t).map(t => (
              <option key={t} value={t}>{t}</option>
            ))}
          </select>
        </div>
        <div className="filtro-group">
          <label>País</label>
          <input
            type="text"
            placeholder="Ej: Colombia"
            value={filtros.pais}
            onChange={(e) => handleChange('pais', e.target.value)}
          />
        </div>
        <div className="filtro-group">
          <label>Usuario ID</label>
          <input
            type="number"
            placeholder="ID"
            value={filtros.usuarioId}
            onChange={(e) => handleChange('usuarioId', e.target.value)}
          />
        </div>
        <div className="filtro-actions">
          <button className="btn-aplicar" onClick={handleAplicar}>Aplicar</button>
          <button className="btn-limpiar" onClick={handleLimpiar}>Limpiar</button>
        </div>
      </div>
    </div>
  );
};

export default FiltrosPanel;
