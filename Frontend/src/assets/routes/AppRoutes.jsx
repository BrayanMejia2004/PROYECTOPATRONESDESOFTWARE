import { Routes, Route, Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../Context/AuthContext';
import Login from '../Pages/Login';
import Registro from '../Pages/Registro';
import CompletarPerfil from '../Pages/CompletarPerfil';
import MiPerfil from '../Pages/MiPerfil';
import MapaCalor from '../Pages/Dashboard';
import DashboardRouter from '../Components/DashboardRouter';
import GestionUsuarios from '../Pages/GestionUsuarios';
import GestionRoles from '../Pages/GestionRoles';
import SesionesActivas from '../Pages/SesionesActivas';
import Auditoria from '../Pages/Auditoria';
import Layout from '../Components/Layout';

const LoadingScreen = () => (
  <div style={{
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: '100vh',
    background: '#0f1419',
    color: '#fff'
  }}>
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      gap: '1rem'
    }}>
      <div style={{
        width: '40px',
        height: '40px',
        border: '3px solid rgba(212, 168, 83, 0.2)',
        borderTopColor: '#d4a853',
        borderRadius: '50%',
        animation: 'spin 0.8s linear infinite'
      }} />
      <span>Cargando...</span>
    </div>
    <style>{`
      @keyframes spin {
        to { transform: rotate(360deg); }
      }
    `}</style>
  </div>
);

const AdminRoute = ({ children }) => {
  const { isAuthenticated, isAdmin, loading } = useAuth();

  if (loading) {
    return <LoadingScreen />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (!isAdmin()) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

const AuditorRoute = ({ children }) => {
  const { isAuthenticated, isAuditor, loading } = useAuth();

  if (loading) {
    return <LoadingScreen />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (!isAuditor()) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

const PrivateRoute = ({ children, requireProfile = false }) => {
  const { isAuthenticated, hasPerfilCompleto, loading, perfilLoading } = useAuth();

  if (loading || perfilLoading) {
    return <LoadingScreen />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (requireProfile && !hasPerfilCompleto) {
    return <Navigate to="/completar-perfil" replace />;
  }

  if (!requireProfile && hasPerfilCompleto) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

const AppRoutes = () => {
  const { isAuthenticated, hasPerfilCompleto, loading } = useAuth();

  if (loading) {
    return <LoadingScreen />;
  }

  return (
    <Routes>
      <Route
        path="/login"
        element={isAuthenticated ? (
          hasPerfilCompleto ? <Navigate to="/dashboard" replace /> : <Navigate to="/completar-perfil" replace />
        ) : <Login />}
      />
      <Route
        path="/registro"
        element={isAuthenticated ? (
          hasPerfilCompleto ? <Navigate to="/dashboard" replace /> : <Navigate to="/completar-perfil" replace />
        ) : <Registro />}
      />
      <Route
        path="/completar-perfil"
        element={
          <PrivateRoute requireProfile={false}>
            <CompletarPerfil />
          </PrivateRoute>
        }
      />
       <Route element={<Layout />}>
         <Route
           path="/mi-perfil"
           element={
             <PrivateRoute requireProfile={true}>
               <MiPerfil />
             </PrivateRoute>
           }
         />
          <Route
            path="/mapadecalor"
            element={
              <PrivateRoute requireProfile={true}>
                <MapaCalor />
              </PrivateRoute>
            }
          />
          <Route
            path="/dashboard"
            element={
              <PrivateRoute requireProfile={true}>
                <DashboardRouter />
              </PrivateRoute>
            }
          />
         <Route
           path="/admin/usuarios"
           element={
             <AdminRoute>
               <GestionUsuarios />
             </AdminRoute>
           }
         />
          <Route
            path="/admin/roles"
            element={
              <AdminRoute>
                <GestionRoles />
              </AdminRoute>
            }
          />
          <Route
            path="/admin/sesiones"
            element={
              <AdminRoute>
                <SesionesActivas />
              </AdminRoute>
            }
          />
          <Route
            path="/auditoria"
            element={
              <AuditorRoute>
                <Auditoria />
              </AuditorRoute>
            }
          />
        </Route>
      <Route path="/" element={
        isAuthenticated ? (
          hasPerfilCompleto ? <Navigate to="/dashboard" replace /> : <Navigate to="/completar-perfil" replace />
        ) : <Navigate to="/login" replace />
      } />
      <Route path="*" element={
        isAuthenticated ? (
          hasPerfilCompleto ? <Navigate to="/dashboard" replace /> : <Navigate to="/completar-perfil" replace />
        ) : <Navigate to="/login" replace />
      } />
    </Routes>
  );
};

export default AppRoutes;
