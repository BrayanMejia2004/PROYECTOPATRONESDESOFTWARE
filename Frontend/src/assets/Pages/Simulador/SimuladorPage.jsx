import { useState, useEffect, useRef, useCallback } from 'react';
import { CommandHistory } from '../../Components/Command/CommandHistory';
import { SimularEventoCommand, SimularEscenarioCommand } from '../../Components/Command/Command';
import EscenarioCard from '../../Components/Command/EscenarioCard';
import { obtenerEscenarios } from '../../Services/simulador/simuladorService';

const escenariosPredefinidos = [
  {
    nombre: 'Dia Normal',
    descripcion: '10 eventos variados (LOGIN, REGISTRO, CONSULTA) para demostración básica',
    totalEventos: 10,
    tiposEvento: ['LOGIN', 'REGISTRO', 'CONSULTA'],
  },
  {
    nombre: 'Ataque Fuerza Bruta',
    descripcion: '30 LOGIN fallidos desde misma IP en 1 minuto',
    totalEventos: 30,
    tiposEvento: ['LOGIN'],
  },
  {
    nombre: 'Pico de Accesos',
    descripcion: '50 LOGIN exitosos desde IPs distintas',
    totalEventos: 50,
    tiposEvento: ['LOGIN'],
  },
  {
    nombre: 'Actividad Sospechosa',
    descripcion: '15 eventos desde IP desconocida en horario nocturno',
    totalEventos: 15,
    tiposEvento: ['LOGIN', 'CONSULTA'],
  },
  {
    nombre: 'Multiusuario',
    descripcion: '20 eventos de 5 usuarios diferentes desde 3 IPs',
    totalEventos: 20,
    tiposEvento: ['LOGIN', 'REGISTRO', 'CONSULTA', 'LOGOUT'],
  },
];

const ACCIONES_PREDEFINIDAS = [
  { accion: 'LOGIN', tipo: 'BASICA' },
  { accion: 'REGISTRO_USUARIO', tipo: 'BASICA' },
  { accion: 'CONSULTA', tipo: 'BASICA' },
  { accion: 'ACTUALIZAR_PERFIL', tipo: 'COMPLETA' },
  { accion: 'ELIMINAR_USUARIO', tipo: 'SEGURIDAD' },
  { accion: 'CREAR_ROL', tipo: 'COMPLETA' },
  { accion: 'ASIGNAR_ROL', tipo: 'COMPLETA' },
];

const SimuladorPage = () => {
  const [history] = useState(() => new CommandHistory());
  const [historial, setHistorial] = useState([]);
  const [mensaje, setMensaje] = useState(null);
  const [mensajeTipo, setMensajeTipo] = useState('exito');
  const [ejecutando, setEjecutando] = useState(null);
  const [escenariosApi, setEscenariosApi] = useState([]);
  const [customEvento, setCustomEvento] = useState({
    accion: 'LOGIN',
    tipo: 'BASICA',
    usuarioId: '',
    ipOrigen: '',
    descripcion: '',
  });
  const [pestañaActiva, setPestañaActiva] = useState('escenarios');
  const forceUpdate = useRef(0);

  useEffect(() => {
    obtenerEscenarios()
      .then((res) => setEscenariosApi(res.data))
      .catch(() => {});
  }, []);

  const mostrarMensaje = useCallback((texto, tipo = 'exito') => {
    setMensaje(texto);
    setMensajeTipo(tipo);
    setTimeout(() => setMensaje(null), 4000);
  }, []);

  const actualizarHistorial = useCallback(() => {
    forceUpdate.current += 1;
    setHistorial(history.getHistorial());
  }, [history]);

  const ejecutarEscenario = useCallback(async (escenario) => {
    setEjecutando(escenario.nombre);
    try {
      const eventos = [];
      for (let i = 0; i < escenario.totalEventos; i++) {
        const accionBase = escenario.tiposEvento[i % escenario.tiposEvento.length];
        const accionInfo = ACCIONES_PREDEFINIDAS.find((a) => a.accion.startsWith(accionBase))
          || { accion: accionBase, tipo: 'BASICA' };
        eventos.push({
          accion: accionBase === 'LOGIN' && escenario.nombre === 'Ataque Fuerza Bruta'
            ? 'LOGIN_FALLIDO' : accionInfo.accion,
          descripcion: `Evento simulado - ${escenario.nombre} #${i + 1}`,
          tipo: accionInfo.tipo,
        });
      }
      const command = new SimularEscenarioCommand({
        nombre: escenario.nombre,
        descripcion: escenario.descripcion,
        eventos,
        batchSize: 10,
      });
      await history.execute(command);
      actualizarHistorial();
      mostrarMensaje(`Escenario "${escenario.nombre}" ejecutado con éxito`);
    } catch (err) {
      mostrarMensaje(`Error al ejecutar escenario: ${err.response?.data?.message || err.message}`, 'error');
    } finally {
      setEjecutando(null);
    }
  }, [history, actualizarHistorial, mostrarMensaje]);

  const ejecutarEventoPersonalizado = useCallback(async () => {
    try {
      const evento = {
        accion: customEvento.accion,
        tipo: customEvento.tipo,
        descripcion: customEvento.descripcion || `Evento ${customEvento.accion} personalizado`,
      };
      if (customEvento.usuarioId) evento.usuarioId = parseInt(customEvento.usuarioId);
      if (customEvento.ipOrigen) evento.ipOrigen = customEvento.ipOrigen;

      const command = new SimularEventoCommand(evento);
      await history.execute(command);
      actualizarHistorial();
      mostrarMensaje(`Evento ${customEvento.accion} simulado con éxito`);
    } catch (err) {
      mostrarMensaje(`Error al simular evento: ${err.response?.data?.message || err.message}`, 'error');
    }
  }, [customEvento, history, actualizarHistorial, mostrarMensaje]);

  const deshacer = useCallback(async () => {
    if (!history.canUndo()) return;
    try {
      const desc = history.getUndoDescription();
      await history.undo();
      actualizarHistorial();
      mostrarMensaje(`Deshecho: ${desc}`);
    } catch (err) {
      mostrarMensaje(`Error al deshacer: ${err.message}`, 'error');
    }
  }, [history, actualizarHistorial, mostrarMensaje]);

  const rehacer = useCallback(async () => {
    if (!history.canRedo()) return;
    try {
      const desc = history.getRedoDescription();
      await history.redo();
      actualizarHistorial();
      mostrarMensaje(`Rehecho: ${desc}`);
    } catch (err) {
      mostrarMensaje(`Error al rehacer: ${err.message}`, 'error');
    }
  }, [history, actualizarHistorial, mostrarMensaje]);

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <div>
          <h1 style={styles.titulo}>Simulador de Eventos</h1>
          <p style={styles.subtitulo}>Genera eventos de auditoría simulados para pruebas y demostraciones</p>
        </div>
        <div style={styles.controls}>
          <button
            style={{ ...styles.controlBtn, opacity: history.canUndo() ? 1 : 0.4, cursor: history.canUndo() ? 'pointer' : 'not-allowed' }}
            onClick={deshacer}
            disabled={!history.canUndo()}
            title={history.getUndoDescription() || 'Deshacer'}
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <polyline points="1 4 1 10 7 10" />
              <path d="M3.51 15a9 9 0 102.13-9.36L1 10" />
            </svg>
            Deshacer
          </button>
          <button
            style={{ ...styles.controlBtn, opacity: history.canRedo() ? 1 : 0.4, cursor: history.canRedo() ? 'pointer' : 'not-allowed' }}
            onClick={rehacer}
            disabled={!history.canRedo()}
            title={history.getRedoDescription() || 'Rehacer'}
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <polyline points="23 4 23 10 17 10" />
              <path d="M20.49 15a9 9 0 11-2.12-9.36L23 10" />
            </svg>
            Rehacer
          </button>
        </div>
      </div>

      {mensaje && (
        <div style={{ ...styles.mensaje, backgroundColor: mensajeTipo === 'error' ? 'rgba(239, 68, 68, 0.15)' : 'rgba(34, 197, 94, 0.15)', borderColor: mensajeTipo === 'error' ? 'rgba(239, 68, 68, 0.3)' : 'rgba(34, 197, 94, 0.3)', color: mensajeTipo === 'error' ? '#ef4444' : '#22c55e' }}>
          {mensaje}
        </div>
      )}

      <div style={styles.tabs}>
        <button
          style={{ ...styles.tab, ...(pestañaActiva === 'escenarios' ? styles.tabActiva : {}) }}
          onClick={() => setPestañaActiva('escenarios')}
        >
          Escenarios Predefinidos
        </button>
        <button
          style={{ ...styles.tab, ...(pestañaActiva === 'personalizado' ? styles.tabActiva : {}) }}
          onClick={() => setPestañaActiva('personalizado')}
        >
          Evento Personalizado
        </button>
        <button
          style={{ ...styles.tab, ...(pestañaActiva === 'historial' ? styles.tabActiva : {}) }}
          onClick={() => setPestañaActiva('historial')}
        >
          Historial ({historial.length})
        </button>
      </div>

      <div style={styles.contenido}>
        {pestañaActiva === 'escenarios' && (
          <div style={styles.escenariosGrid}>
            {escenariosPredefinidos.map((escenario) => (
              <EscenarioCard
                key={escenario.nombre}
                escenario={escenario}
                onEjecutar={ejecutarEscenario}
                ejecutando={ejecutando === escenario.nombre}
              />
            ))}
          </div>
        )}

        {pestañaActiva === 'personalizado' && (
          <div style={styles.formContainer}>
            <div style={styles.formGrid}>
              <div style={styles.formGroup}>
                <label style={styles.label}>Acción</label>
                <select
                  style={styles.select}
                  value={customEvento.accion}
                  onChange={(e) => setCustomEvento({ ...customEvento, accion: e.target.value })}
                >
                  {ACCIONES_PREDEFINIDAS.map((a) => (
                    <option key={a.accion} value={a.accion}>{a.accion}</option>
                  ))}
                </select>
              </div>
              <div style={styles.formGroup}>
                <label style={styles.label}>Tipo</label>
                <select
                  style={styles.select}
                  value={customEvento.tipo}
                  onChange={(e) => setCustomEvento({ ...customEvento, tipo: e.target.value })}
                >
                  <option value="BASICA">BÁSICA</option>
                  <option value="COMPLETA">COMPLETA</option>
                  <option value="SEGURIDAD">SEGURIDAD</option>
                </select>
              </div>
              <div style={styles.formGroup}>
                <label style={styles.label}>Usuario ID (opcional)</label>
                <input
                  style={styles.input}
                  type="number"
                  placeholder="Aleatorio si se deja vacío"
                  value={customEvento.usuarioId}
                  onChange={(e) => setCustomEvento({ ...customEvento, usuarioId: e.target.value })}
                />
              </div>
              <div style={styles.formGroup}>
                <label style={styles.label}>IP Origen (opcional)</label>
                <input
                  style={styles.input}
                  type="text"
                  placeholder="Ej: 192.168.1.10"
                  value={customEvento.ipOrigen}
                  onChange={(e) => setCustomEvento({ ...customEvento, ipOrigen: e.target.value })}
                />
              </div>
            </div>
            <div style={styles.formGroup}>
              <label style={styles.label}>Descripción (opcional)</label>
              <input
                style={styles.input}
                type="text"
                placeholder="Descripción del evento"
                value={customEvento.descripcion}
                onChange={(e) => setCustomEvento({ ...customEvento, descripcion: e.target.value })}
              />
            </div>
            <button style={styles.botonSimular} onClick={ejecutarEventoPersonalizado}>
              Simular Evento
            </button>
          </div>
        )}

        {pestañaActiva === 'historial' && (
          <div style={styles.historialContainer}>
            {historial.length === 0 ? (
              <p style={styles.sinHistorial}>No hay simulaciones en esta sesión</p>
            ) : (
              historial.map((cmd, i) => (
                <div key={i} style={styles.historialItem}>
                  <div style={styles.historialIcon}>
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#d4a853" strokeWidth="2">
                      <polyline points="20 6 9 17 4 12" />
                    </svg>
                  </div>
                  <div style={styles.historialInfo}>
                    <span style={styles.historialAccion}>{cmd.getDescripcion()}</span>
                    <span style={styles.historialEstado}>Ejecutado</span>
                  </div>
                </div>
              ))
            )}
          </div>
        )}
      </div>
    </div>
  );
};

const styles = {
  container: {
    padding: '2rem',
    maxWidth: '1200px',
    margin: '0 auto',
    color: '#e2e8f0',
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: '1.5rem',
    flexWrap: 'wrap',
    gap: '1rem',
  },
  titulo: {
    margin: 0,
    fontSize: '1.8rem',
    fontWeight: 700,
    background: 'linear-gradient(135deg, #d4a853, #f5d782)',
    WebkitBackgroundClip: 'text',
    WebkitTextFillColor: 'transparent',
  },
  subtitulo: {
    margin: '0.5rem 0 0',
    color: '#94a3b8',
    fontSize: '0.95rem',
  },
  controls: {
    display: 'flex',
    gap: '0.75rem',
  },
  controlBtn: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    background: 'rgba(212, 168, 83, 0.1)',
    border: '1px solid rgba(212, 168, 83, 0.2)',
    borderRadius: '8px',
    padding: '0.6rem 1.2rem',
    color: '#d4a853',
    fontSize: '0.9rem',
    fontWeight: 500,
    cursor: 'pointer',
    transition: 'all 0.2s ease',
  },
  mensaje: {
    padding: '0.75rem 1rem',
    borderRadius: '8px',
    border: '1px solid',
    marginBottom: '1rem',
    fontSize: '0.9rem',
    fontWeight: 500,
  },
  tabs: {
    display: 'flex',
    gap: '0.5rem',
    marginBottom: '1.5rem',
    borderBottom: '1px solid rgba(255, 255, 255, 0.1)',
    paddingBottom: '0',
  },
  tab: {
    background: 'none',
    border: 'none',
    padding: '0.75rem 1.5rem',
    color: '#94a3b8',
    fontSize: '0.95rem',
    fontWeight: 500,
    cursor: 'pointer',
    borderBottom: '2px solid transparent',
    transition: 'all 0.2s ease',
  },
  tabActiva: {
    color: '#d4a853',
    borderBottomColor: '#d4a853',
  },
  contenido: {
    minHeight: '300px',
  },
  escenariosGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))',
    gap: '1rem',
  },
  formContainer: {
    background: 'linear-gradient(135deg, #1a2332 0%, #1e2d3d 100%)',
    border: '1px solid rgba(212, 168, 83, 0.2)',
    borderRadius: '12px',
    padding: '2rem',
    maxWidth: '600px',
  },
  formGrid: {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '1rem',
    marginBottom: '1rem',
  },
  formGroup: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.4rem',
    marginBottom: '1rem',
  },
  label: {
    color: '#94a3b8',
    fontSize: '0.85rem',
    fontWeight: 500,
  },
  input: {
    background: 'rgba(0, 0, 0, 0.3)',
    border: '1px solid rgba(255, 255, 255, 0.1)',
    borderRadius: '8px',
    padding: '0.7rem 1rem',
    color: '#e2e8f0',
    fontSize: '0.9rem',
    outline: 'none',
    transition: 'border-color 0.2s ease',
  },
  select: {
    background: 'rgba(0, 0, 0, 0.3)',
    border: '1px solid rgba(255, 255, 255, 0.1)',
    borderRadius: '8px',
    padding: '0.7rem 1rem',
    color: '#e2e8f0',
    fontSize: '0.9rem',
    outline: 'none',
    cursor: 'pointer',
  },
  botonSimular: {
    backgroundColor: 'rgba(212, 168, 83, 0.15)',
    color: '#d4a853',
    border: '1px solid rgba(212, 168, 83, 0.3)',
    borderRadius: '8px',
    padding: '0.8rem 2rem',
    fontSize: '0.95rem',
    fontWeight: 600,
    cursor: 'pointer',
    transition: 'all 0.2s ease',
    width: '100%',
    marginTop: '0.5rem',
  },
  historialContainer: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.5rem',
  },
  sinHistorial: {
    textAlign: 'center',
    color: '#64748b',
    padding: '3rem',
    fontSize: '1rem',
  },
  historialItem: {
    display: 'flex',
    alignItems: 'center',
    gap: '1rem',
    background: 'rgba(255, 255, 255, 0.03)',
    border: '1px solid rgba(255, 255, 255, 0.06)',
    borderRadius: '8px',
    padding: '0.8rem 1rem',
  },
  historialIcon: {
    width: '32px',
    height: '32px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    background: 'rgba(212, 168, 83, 0.1)',
    borderRadius: '50%',
  },
  historialInfo: {
    display: 'flex',
    justifyContent: 'space-between',
    flex: 1,
    alignItems: 'center',
  },
  historialAccion: {
    color: '#e2e8f0',
    fontSize: '0.9rem',
    fontWeight: 500,
  },
  historialEstado: {
    color: '#22c55e',
    fontSize: '0.8rem',
  },
};

export default SimuladorPage;
