import { useEffect, useRef } from 'react';
import DashboardMediator from './DashboardMediator';

const ConexionWebSocket = ({ mediator }) => {
  const wsRef = useRef(null);
  const heartbeatRef = useRef(null);
  const reconnectRef = useRef(null);

  useEffect(() => {
    let montado = true;

    const conectar = () => {
      if (!montado) return;
      const protocolo = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
      const host = window.location.host;
      const url = `${protocolo}//${host}/ws/eventos-globales`;

      try {
        const ws = new WebSocket(url);
        wsRef.current = ws;

        ws.onopen = () => {
          if (!montado) { ws.close(); return; }
          if (heartbeatRef.current) clearInterval(heartbeatRef.current);
          heartbeatRef.current = setInterval(() => {
            if (ws.readyState === WebSocket.OPEN) {
              ws.send(JSON.stringify({ type: 'HEARTBEAT' }));
            }
          }, 30000);
        };

        ws.onmessage = (event) => {
          if (!montado) return;
          try {
            const data = JSON.parse(event.data);
            if (data.type === 'EVENTO' && data.evento) {
              mediator?.notify(null, 'WEBSOCKET_EVENTO', data.evento);
            }
          } catch {
            // ignorar mensajes no JSON
          }
        };

        ws.onclose = () => {
          if (!montado) return;
          if (heartbeatRef.current) clearInterval(heartbeatRef.current);
          reconnectRef.current = setTimeout(conectar, 5000);
        };

        ws.onerror = () => {
          ws.close();
        };
      } catch {
        reconnectRef.current = setTimeout(conectar, 5000);
      }
    };

    conectar();

    return () => {
      montado = false;
      if (heartbeatRef.current) clearInterval(heartbeatRef.current);
      if (reconnectRef.current) clearTimeout(reconnectRef.current);
      if (wsRef.current) wsRef.current.close();
    };
  }, [mediator]);

  return null;
};

export default ConexionWebSocket;
