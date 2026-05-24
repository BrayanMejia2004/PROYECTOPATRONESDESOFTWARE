import { forwardRef, useImperativeHandle, useRef, useState, useEffect } from 'react';
import { obtenerResumenGlobal } from '../Services/eventoGlobalService';

const ContadoresGlobales = forwardRef(({ resumen: resumenProp }, ref) => {
  const [totalEventos, setTotalEventos] = useState(resumenProp?.totalEventos || 0);
  const [usuariosActivos, setUsuariosActivos] = useState(resumenProp?.usuariosActivos || 0);
  const [paisesDetectados, setPaisesDetectados] = useState(resumenProp?.paisesDetectados || 0);
  const [ipsUnicas, setIpsUnicas] = useState(resumenProp?.ipsUnicas || 0);
  const mediatorRef = useRef(null);

  useEffect(() => {
    if (!resumenProp) cargarResumen();
  }, []);

  useEffect(() => {
    if (resumenProp) {
      setTotalEventos(resumenProp.totalEventos || 0);
      setUsuariosActivos(resumenProp.usuariosActivos || 0);
      setPaisesDetectados(resumenProp.paisesDetectados || 0);
      setIpsUnicas(resumenProp.ipsUnicas || 0);
    }
  }, [resumenProp]);

  const cargarResumen = async () => {
    const result = await obtenerResumenGlobal();
    if (result.success && result.data) {
      setTotalEventos(result.data.totalEventos || 0);
      setUsuariosActivos(result.data.usuariosActivos || 0);
      setPaisesDetectados(result.data.paisesDetectados || 0);
      setIpsUnicas(result.data.ipsUnicas || 0);
    }
  };

  useImperativeHandle(ref, () => ({
    setMediator: (m) => { mediatorRef.current = m; },
    aplicarFiltros: () => { cargarResumen(); },
    filtrarPorPais: () => {},
    incrementar: (evento) => {
      setTotalEventos(prev => prev + 1);
    },
  }));

  return (
    <div className="contadores-globales">
      <div className="contador-card">
        <span className="contador-valor">{totalEventos.toLocaleString()}</span>
        <span className="contador-label">Eventos hoy</span>
      </div>
      <div className="contador-card">
        <span className="contador-valor">{paisesDetectados}</span>
        <span className="contador-label">Países activos</span>
      </div>
      <div className="contador-card">
        <span className="contador-valor">{usuariosActivos}</span>
        <span className="contador-label">Usuarios activos</span>
      </div>
      <div className="contador-card">
        <span className="contador-valor">{ipsUnicas}</span>
        <span className="contador-label">IPs únicas</span>
      </div>
    </div>
  );
});

ContadoresGlobales.displayName = 'ContadoresGlobales';
export default ContadoresGlobales;
