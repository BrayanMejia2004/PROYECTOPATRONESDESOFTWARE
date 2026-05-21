import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../Context/AuthContext';
import { validateForm, validators } from '../../Utils/validators';
import './CompletarPerfil.css';

const CompletarPerfil = () => {
  const navigate = useNavigate();
  const { user, crearPerfil, isLoading } = useAuth();
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    telefono: '',
  });
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: null }));
    }
    setErrorMessage('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');

    const { isValid, errors: validationErrors } = validateForm(formData, {
      nombre: validators.nombre,
      apellido: validators.apellido,
    });

    if (!isValid) {
      setErrors(validationErrors);
      return;
    }

    setIsSubmitting(true);

    try {
      const perfilData = {
        nombre: formData.nombre.trim(),
        apellido: formData.apellido.trim(),
        telefono: formData.telefono?.trim() || null,
      };

      const result = await crearPerfil(perfilData);
      
      if (result.success) {
        navigate('/dashboard');
      } else {
        setErrorMessage(result.message);
      }
    } catch (error) {
      setErrorMessage('Error de conexión. Intenta más tarde.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="perfil-page">
      <div className="perfil-container">
        <div className="perfil-header">
          <div className="perfil-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
          </div>
          <h1>Completa tu Perfil</h1>
          <p>
            Hola <strong>{user?.username}</strong>, para continuar ingresa tus datos personales.
          </p>
        </div>

        {errorMessage && (
          <div className="perfil-error">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10" />
              <line x1="12" y1="8" x2="12" y2="12" />
              <line x1="12" y1="16" x2="12.01" y2="16" />
            </svg>
            {errorMessage}
          </div>
        )}

        <form onSubmit={handleSubmit} className="perfil-form">
          <div className={`perfil-field ${errors.nombre ? 'has-error' : ''}`}>
            <label htmlFor="nombre">Nombre</label>
            <div className="perfil-input-wrapper">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
              <input
                type="text"
                id="nombre"
                name="nombre"
                value={formData.nombre}
                onChange={handleChange}
                placeholder="Ingresa tu nombre"
                autoComplete="given-name"
              />
            </div>
            {errors.nombre && <span className="perfil-error-text">{errors.nombre}</span>}
          </div>

          <div className={`perfil-field ${errors.apellido ? 'has-error' : ''}`}>
            <label htmlFor="apellido">Apellido</label>
            <div className="perfil-input-wrapper">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
              <input
                type="text"
                id="apellido"
                name="apellido"
                value={formData.apellido}
                onChange={handleChange}
                placeholder="Ingresa tu apellido"
                autoComplete="family-name"
              />
            </div>
            {errors.apellido && <span className="perfil-error-text">{errors.apellido}</span>}
          </div>

          <div className="perfil-field">
            <label htmlFor="telefono">
              Teléfono <span className="perfil-optional">(opcional)</span>
            </label>
            <div className="perfil-input-wrapper">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M22 16.92v3a2 2 0 01-2.18 2 19.79 19.79 0 01-8.63-3.07 19.5 19.5 0 01-6-6 19.79 19.79 0 01-3.07-8.67A2 2 0 014.11 2h3a2 2 0 012 1.72 12.84 12.84 0 00.7 2.81 2 2 0 01-.45 2.11L8.09 9.91a16 16 0 006 6l1.27-1.27a2 2 0 012.11-.45 12.84 12.84 0 002.81.7A2 2 0 0122 16.92z" />
              </svg>
              <input
                type="tel"
                id="telefono"
                name="telefono"
                value={formData.telefono}
                onChange={handleChange}
                placeholder="Número de teléfono"
                autoComplete="tel"
              />
            </div>
          </div>

          <button type="submit" className="perfil-submit" disabled={isSubmitting || isLoading}>
            {isSubmitting ? (
              <>
                <span className="perfil-spinner"></span>
                Guardando...
              </>
            ) : (
              <>
                Completar Perfil
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M22 11.08V12a10 10 0 11-5.93-9.14" />
                  <path d="M22 4L12 14.01l-3-3" />
                </svg>
              </>
            )}
          </button>
        </form>

        <div className="perfil-info">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="12" cy="12" r="10" />
            <path d="M12 16v-4" />
            <path d="M12 8h.01" />
          </svg>
          <span>Completa tu perfil para acceder a todas las funcionalidades del sistema.</span>
        </div>
      </div>
    </div>
  );
};

export default CompletarPerfil;
