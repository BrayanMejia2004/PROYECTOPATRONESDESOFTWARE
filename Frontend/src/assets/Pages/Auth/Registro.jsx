import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../Context/AuthContext';
import { validateForm, validators } from '../../Utils/validators';
import './AuthPages.css';

const Registro = () => {
  const navigate = useNavigate();
  const { registro } = useAuth();
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: null }));
    }
    setErrorMessage('');
    setSuccessMessage('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    setSuccessMessage('');

    const { isValid, errors: validationErrors } = validateForm(formData, {
      username: validators.username,
      email: validators.email,
      password: validators.password,
      confirmPassword: () => validators.confirmPassword(formData.password, formData.confirmPassword),
    });

    if (!isValid) {
      setErrors(validationErrors);
      return;
    }

    setIsLoading(true);

    try {
      const result = await registro(formData.username, formData.email, formData.password);
      
      if (result.success) {
        setSuccessMessage('¡Cuenta creada exitosamente! Redirigiendo al login...');
        setTimeout(() => {
          navigate('/login');
        }, 2000);
      } else {
        setErrorMessage(result.message);
      }
    } catch (error) {
      setErrorMessage('Error de conexión. Intenta más tarde.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-panel auth-panel-left">
        <div className="auth-panel-content">
          <div className="auth-logo">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <path d="M12 2L2 7l10 5 10-5-10-5z" />
              <path d="M2 17l10 5 10-5" />
              <path d="M2 12l10 5 10-5" />
            </svg>
          </div>
          <h1>Únete al Sistema</h1>
          <p>Crea tu cuenta para acceder a todas las funcionalidades</p>
          
          <div className="auth-features">
            <div className="auth-feature">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M16 21v-2a4 4 0 00-4-4H6a4 4 0 00-4 4v2" />
                <circle cx="9" cy="7" r="4" />
                <line x1="19" y1="8" x2="19" y2="14" />
                <line x1="22" y1="11" x2="16" y2="11" />
              </svg>
              <span>Gestión de Usuarios</span>
            </div>
            <div className="auth-feature">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z" />
                <path d="M14 2v6h6" />
                <line x1="16" y1="13" x2="8" y2="13" />
                <line x1="16" y1="17" x2="8" y2="17" />
              </svg>
              <span>Reportes Personalizados</span>
            </div>
            <div className="auth-feature">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
                <path d="M9 12l2 2 4-4" />
              </svg>
              <span>Acceso Seguro</span>
            </div>
          </div>
        </div>
        
        <div className="auth-panel-pattern"></div>
      </div>

      <div className="auth-panel auth-panel-right">
        <div className="auth-form-container">
          <div className="auth-form-header">
            <h2>Crear Cuenta</h2>
            <p>Completa el formulario para registrarte</p>
          </div>

          {errorMessage && (
            <div className="auth-error">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="12" cy="12" r="10" />
                <line x1="12" y1="8" x2="12" y2="12" />
                <line x1="12" y1="16" x2="12.01" y2="16" />
              </svg>
              {errorMessage}
            </div>
          )}

          {successMessage && (
            <div className="auth-success">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M22 11.08V12a10 10 0 11-5.93-9.14" />
                <path d="M22 4L12 14.01l-3-3" />
              </svg>
              {successMessage}
            </div>
          )}

          <form onSubmit={handleSubmit} className="auth-form">
            <div className={`form-group ${errors.username ? 'has-error' : ''}`}>
              <label htmlFor="username">Usuario</label>
              <div className="input-wrapper">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
                <input
                  type="text"
                  id="username"
                  name="username"
                  value={formData.username}
                  onChange={handleChange}
                  placeholder="Elige un nombre de usuario"
                  autoComplete="username"
                />
              </div>
              {errors.username && <span className="error-message">{errors.username}</span>}
            </div>

            <div className={`form-group ${errors.email ? 'has-error' : ''}`}>
              <label htmlFor="email">Correo Electrónico</label>
              <div className="input-wrapper">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
                  <path d="M22 6l-10 7L2 6" />
                </svg>
                <input
                  type="email"
                  id="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="tu@correo.gob.mx"
                  autoComplete="email"
                />
              </div>
              {errors.email && <span className="error-message">{errors.email}</span>}
            </div>

            <div className={`form-group ${errors.password ? 'has-error' : ''}`}>
              <label htmlFor="password">Contraseña</label>
              <div className="input-wrapper">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                  <path d="M7 11V7a5 5 0 0110 0v4" />
                </svg>
                <input
                  type="password"
                  id="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="Mínimo 6 caracteres"
                  autoComplete="new-password"
                />
              </div>
              {errors.password && <span className="error-message">{errors.password}</span>}
            </div>

            <div className={`form-group ${errors.confirmPassword ? 'has-error' : ''}`}>
              <label htmlFor="confirmPassword">Confirmar Contraseña</label>
              <div className="input-wrapper">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                  <path d="M7 11V7a5 5 0 0110 0v4" />
                </svg>
                <input
                  type="password"
                  id="confirmPassword"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  placeholder="Repite tu contraseña"
                  autoComplete="new-password"
                />
              </div>
              {errors.confirmPassword && <span className="error-message">{errors.confirmPassword}</span>}
            </div>

            <button type="submit" className="auth-submit" disabled={isLoading}>
              {isLoading ? (
                <>
                  <span className="spinner"></span>
                  Creando cuenta...
                </>
              ) : (
                <>
                  Crear Cuenta
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M16 21v-2a4 4 0 00-4-4H6a4 4 0 00-4 4v2" />
                    <circle cx="9" cy="7" r="4" />
                    <line x1="19" y1="8" x2="19" y2="14" />
                    <line x1="22" y1="11" x2="16" y2="11" />
                  </svg>
                </>
              )}
            </button>
          </form>

          <div className="auth-form-footer">
            <p>
              ¿Ya tienes cuenta?{' '}
              <Link to="/login" className="auth-link">
                Inicia sesión
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Registro;
