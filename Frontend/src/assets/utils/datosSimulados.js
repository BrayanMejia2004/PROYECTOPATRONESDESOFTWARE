const aleatorio = (min, max) => Math.floor(Math.random() * (max - min + 1)) + min;

export const generarFecha = (diasAtras = 0) => {
  const d = new Date();
  d.setDate(d.getDate() - diasAtras);
  return d;
};

export const generarTendenciaDiaria = (dias = 30, base = 10, variacion = 5) => {
  const data = [];
  for (let i = dias - 1; i >= 0; i--) {
    const d = generarFecha(i);
    const diaSem = d.getDay();
    const factorFinSemana = diaSem === 0 || diaSem === 6 ? 0.3 : 1;
    const valor = Math.round((base + aleatorio(-variacion, variacion)) * factorFinSemana);
    data.push({
      fecha: d.toISOString().split('T')[0],
      valor: Math.max(0, valor),
    });
  }
  return data;
};

export const generarDistribucionDuracion = () => [
  { rango: '< 5 min', cantidad: aleatorio(5, 15) },
  { rango: '5-30 min', cantidad: aleatorio(10, 25) },
  { rango: '30 min-2h', cantidad: aleatorio(15, 30) },
  { rango: '2-8h', cantidad: aleatorio(8, 20) },
  { rango: '> 8h', cantidad: aleatorio(2, 8) },
];

export const generarSesionesPorHora = () => {
  const data = [];
  for (let h = 0; h < 24; h++) {
    let factor;
    if (h >= 0 && h < 6) factor = aleatorio(1, 5);
    else if (h >= 6 && h < 9) factor = aleatorio(5, 15);
    else if (h >= 9 && h < 12) factor = aleatorio(20, 35);
    else if (h >= 12 && h < 14) factor = aleatorio(15, 25);
    else if (h >= 14 && h < 18) factor = aleatorio(25, 40);
    else if (h >= 18 && h < 22) factor = aleatorio(10, 20);
    else factor = aleatorio(3, 10);
    data.push({ hora: `${h.toString().padStart(2, '0')}:00`, sesiones: factor });
  }
  return data;
};

export const generarTendenciaSemanal = (semanas = 8, base = 50) => {
  const data = [];
  for (let i = semanas - 1; i >= 0; i--) {
    const d = new Date();
    d.setDate(d.getDate() - i * 7);
    data.push({
      semana: `Sem ${semanas - i}`,
      fecha: d.toISOString().split('T')[0],
      valor: Math.max(0, base + aleatorio(-15, 20)),
    });
  }
  return data;
};

export const generarActividadMensualPorAnio = (anio, base = 200) => {
  const nombres = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
  const meses = [];
  for (let i = 0; i < 12; i++) {
    const esFinde = i === 0 || i === 11 ? 0.7 : 1;
    meses.push({
      mes: nombres[i],
      actual: Math.max(0, Math.round((base + aleatorio(-50, 70)) * esFinde)),
      anterior: Math.max(0, Math.round((base + aleatorio(-60, 40)) * esFinde)),
    });
  }
  if (anio % 4 === 0 && anio % 100 !== 0) meses[1].actual += aleatorio(10, 25);
  return { meses, anio, totalActual: meses.reduce((s, m) => s + m.actual, 0), totalAnterior: meses.reduce((s, m) => s + m.anterior, 0) };
};

export const generarActividadPorIP = (ip, dias = 30) => {
  const ultDigito = parseInt(ip.split('.').pop(), 10) || 1;
  const base = 3 + (ultDigito % 8);
  const data = [];
  for (let i = dias - 1; i >= 0; i--) {
    const d = generarFecha(i);
    const diaSem = d.getDay();
    const factorFinSemana = diaSem === 0 || diaSem === 6 ? 0.2 : 1;
    const valor = Math.max(0, Math.round((base + aleatorio(-2, 4)) * factorFinSemana));
    data.push({ fecha: d.toISOString().split('T')[0], valor });
  }
  return { ip, data, total: data.reduce((s, d) => s + d.valor, 0) };
};

const DIAS_SEMANA = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
const padNum = (n) => String(n).padStart(2, '0');

export const generarCalendarioActividad = (dias = 28, base = 8) => {
  const data = [];
  for (let i = dias - 1; i >= 0; i--) {
    const d = generarFecha(i);
    const diaSem = d.getDay();
    const esFinde = diaSem === 0 || diaSem === 6 ? 0.3 : 1;
    const valor = Math.max(0, Math.round((base + aleatorio(-4, 6)) * esFinde));
    const fechaLocal = `${d.getFullYear()}-${padNum(d.getMonth() + 1)}-${padNum(d.getDate())}`;
    data.push({
      fecha: fechaLocal,
      diaSem: DIAS_SEMANA[diaSem],
      diaNum: d.getDate(),
      valor,
      nivel: valor === 0 ? 0 : valor <= 3 ? 1 : valor <= 6 ? 2 : valor <= 10 ? 3 : 4,
    });
  }
  return data;
};

const RANGOS_HORA = [
  { label: '0-4', hInicio: 0, hFin: 4 },
  { label: '5-8', hInicio: 5, hFin: 8 },
  { label: '9-12', hInicio: 9, hFin: 12 },
  { label: '13-16', hInicio: 13, hFin: 16 },
  { label: '17-20', hInicio: 17, hFin: 20 },
  { label: '21-23', hInicio: 21, hFin: 23 },
];

export const generarDistribucionHoras = () => RANGOS_HORA.map((r) => {
  let baseMin, baseMax;
  switch (r.label) {
    case '0-4': baseMin = 1; baseMax = 4; break;
    case '5-8': baseMin = 5; baseMax = 12; break;
    case '9-12': baseMin = 20; baseMax = 35; break;
    case '13-16': baseMin = 15; baseMax = 25; break;
    case '17-20': baseMin = 25; baseMax = 40; break;
    case '21-23': baseMin = 3; baseMax = 10; break;
    default: baseMin = 5; baseMax = 15;
  }
  return { ...r, eventos: aleatorio(baseMin, baseMax) };
});

export const generarMatrizSemanal = (dias = 7, base = 10) => {
  const data = [];
  const NOMBRES_DIAS = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];
  const scale = base / 10;
  for (let d = dias - 1; d >= 0; d--) {
    const dt = new Date();
    dt.setDate(dt.getDate() - d);
    const diaSem = dt.getDay();
    const idx = diaSem === 0 ? 6 : diaSem - 1;
    const fila = { dia: NOMBRES_DIAS[idx], fecha: `${dt.getFullYear()}-${padNum(dt.getMonth() + 1)}-${padNum(dt.getDate())}`, horas: [] };
    for (let h = 0; h < 24; h++) {
      let f;
      if (h >= 0 && h < 6) f = aleatorio(0, Math.round(4 * scale));
      else if (h >= 6 && h < 9) f = aleatorio(Math.round(2 * scale), Math.round(12 * scale));
      else if (h >= 9 && h < 12) f = aleatorio(Math.round(10 * scale), Math.round(35 * scale));
      else if (h >= 12 && h < 14) f = aleatorio(Math.round(8 * scale), Math.round(22 * scale));
      else if (h >= 14 && h < 18) f = aleatorio(Math.round(15 * scale), Math.round(42 * scale));
      else if (h >= 18 && h < 22) f = aleatorio(Math.round(6 * scale), Math.round(18 * scale));
      else f = aleatorio(Math.round(1 * scale), Math.round(8 * scale));
      const esFinde = idx >= 5 ? 0.25 : 1;
      fila.horas.push(Math.max(0, Math.round(f * esFinde)));
    }
    data.push(fila);
  }
  return data;
};

export const generarAnomalias = (dias = 30) => {
  const data = [];
  const base = 25;
  const anomaliaProb = 0.07;
  for (let i = dias - 1; i >= 0; i--) {
    const dt = new Date();
    dt.setDate(dt.getDate() - i);
    const diaSem = dt.getDay();
    const esFinde = diaSem === 0 || diaSem === 6 ? 0.3 : 1;
    const valorNormal = Math.round((base + aleatorio(-10, 12)) * esFinde);
    const esAnomalia = Math.random() < anomaliaProb;
    const valor = esAnomalia ? Math.round(base * (1.5 + Math.random())) : Math.max(0, valorNormal);
    data.push({
      fecha: `${dt.getFullYear()}-${padNum(dt.getMonth() + 1)}-${padNum(dt.getDate())}`,
      valor: Math.max(0, valor),
      anomalia: esAnomalia,
    });
  }
  const media = data.reduce((s, d) => s + d.valor, 0) / data.length;
  const desv = Math.sqrt(data.reduce((s, d) => s + (d.valor - media) ** 2, 0) / data.length);
  const totalAnomalias = data.filter(d => d.anomalia).length;
  const prediccion = [];
  for (let i = 1; i <= 7; i++) {
    const dt = new Date();
    dt.setDate(dt.getDate() + i);
    prediccion.push({
      fecha: `${dt.getFullYear()}-${padNum(dt.getMonth() + 1)}-${padNum(dt.getDate())}`,
      valor: Math.max(0, Math.round(media + aleatorio(-5, 5))),
    });
  }
  return { serie: data, media, desv, totalAnomalias, prediccion };
};

export const generarDatosBurbuja = (dias = 30) => {
  const roles = [
    { key: 'BASICA', label: 'Básica', color: '#d4a853' },
    { key: 'COMPLETA', label: 'Completa', color: '#00ba7c' },
    { key: 'SEGURIDAD', label: 'Seguridad', color: '#f4212e' },
  ];
  const usuarios = [
    { id: 1, nombre: 'admin' }, { id: 2, nombre: 'jperez' }, { id: 3, nombre: 'mgarcia' },
    { id: 4, nombre: 'lrodriguez' }, { id: 5, nombre: 'esanchez' }, { id: 6, nombre: 'cgonzalez' },
    { id: 7, nombre: 'atorres' }, { id: 8, nombre: 'mramirez' }, { id: 9, nombre: 'pdiaz' },
  ];
  const factor = dias / 30;
  return roles.map(r => {
    const total = Math.round(aleatorio(30, 150) * factor);
    const usuariosRol = usuarios.slice(0, aleatorio(2, 5)).map(u => ({
      ...u,
      acciones: aleatorio(5, Math.round(total * 0.3)),
    }));
    return { ...r, total, usuarios: usuariosRol };
  });
};

export const generarActividadMensual = (meses = 12, base = 200) => {
  const nombres = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
  const data = [];
  for (let i = meses - 1; i >= 0; i--) {
    const m = (new Date().getMonth() - i + 12) % 12;
    data.push({
      mes: nombres[m],
      actual: Math.max(0, base + aleatorio(-40, 60)),
      anterior: Math.max(0, base + aleatorio(-50, 40)),
    });
  }
  return data;
};
