import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../Context/AuthContext';
import { validateForm, validators } from '../../Utils/validators';
import './MiPerfil.css';

const MiPerfil = () => {
  const navigate = useNavigate();
  const { user, perfil, actualizarPerfil, loading } = useAuth();

  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    telefono: '',
  });
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    if (perfil) {
      setFormData({
        nombre: perfil.nombre || '',
        apellido: perfil.apellido || '',
        telefono: perfil.telefono || '',
      });
    }
  }, [perfil]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: null }));
    }
    setSuccessMessage('');
    setErrorMessage('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    setSuccessMessage('');

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

      const result = await actualizarPerfil(perfilData);

      if (result.success) {
        setSuccessMessage('Perfil actualizado exitosamente');
        setTimeout(() => setSuccessMessage(''), 3000);
      } else {
        setErrorMessage(result.message);
      }
    } catch (error) {
      setErrorMessage('Error de conexión. Intenta más tarde.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const getDisplayName = () => {
    if (formData.nombre && formData.apellido) {
      return `${formData.nombre} ${formData.apellido}`;
    }
    return user?.username || 'Usuario';
  };

  if (loading) {
    return (
      <div className="miperfil-loading">
        <div className="miperfil-spinner"></div>
        <span>Cargando...</span>
      </div>
    );
  }

  return (
    <div className="miperfil-page">
      <div className="miperfil-container">
        <div className="miperfil-header">
          <div className="miperfil-avatar">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
          </div>
          <div className="miperfil-user-info">
            <h1>{getDisplayName()}</h1>
            <span className="miperfil-username">@{user?.username}</span>
            <span className="miperfil-email">{perfil?.email || 'Sin email'}</span>
          </div>
        </div>

        <div className="miperfil-form-section">
          <h2>Información Personal</h2>

          {successMessage && (
            <div className="miperfil-success">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M22 11.08V12a10 10 0 11-5.93-9.14" />
                <path d="M22 4L12 14.01l-3-3" />
              </svg>
              {successMessage}
            </div>
          )}

          {errorMessage && (
            <div className="miperfil-error">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="12" cy="12" r="10" />
                <line x1="12" y1="8" x2="12" y2="12" />
                <line x1="12" y1="16" x2="12.01" y2="16" />
              </svg>
              {errorMessage}
            </div>
          )}

          <form onSubmit={handleSubmit} className="miperfil-form">
            <div className={`miperfil-field ${errors.nombre ? 'has-error' : ''}`}>
              <label htmlFor="nombre">Nombre</label>
              <div className="miperfil-input-wrapper">
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
                  placeholder="Tu nombre"
                />
              </div>
              {errors.nombre && <span className="miperfil-error-text">{errors.nombre}</span>}
            </div>

            <div className={`miperfil-field ${errors.apellido ? 'has-error' : ''}`}>
              <label htmlFor="apellido">Apellido</label>
              <div className="miperfil-input-wrapper">
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
                  placeholder="Tu apellido"
                />
              </div>
              {errors.apellido && <span className="miperfil-error-text">{errors.apellido}</span>}
            </div>

            <div className="miperfil-field">
              <label htmlFor="telefono">Teléfono</label>
              <div className="miperfil-input-wrapper">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M22 16.92v3a2 2 0 01-2.18 2 19.79 19.79 0 01-8.63-3.07 19.5 19.5 0 01-6-6 19.79 19.79 0 01-3.07-8.67A2 2 0 014.11 2h3a2 2 0 012 1.72 12.84 12.84 0 00.7 2.81 2 2 0 01-.45 2.11L8.09 9.91a16 16 0 006 6l1.27-1.27a2 2 0 012.11-.45 12.84 12.84 0 002.81.7A2 2 0 0122 16.92z" />
                </svg>
                <input
                  type="tel"
                  id="telefono"
                  name="telefono"
                  value={formData.telefono}
                  onChange={handleChange}
                  placeholder="Número de teléfono (opcional)"
                />
              </div>
            </div>

            <button type="submit" className="miperfil-submit" disabled={isSubmitting}>
              {isSubmitting ? (
                <>
                  <span className="miperfil-spinner-btn"></span>
                  Guardando...
                </>
              ) : (
                <>
                  Guardar Cambios
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M19 21H5a2 2 0 01-2-2V5a2 2 0 012-2h11l5 5v11a2 2 0 01-2 2z" />
                    <path d="M17 21v-8H7v8" />
                    <path d="M7 3v5h8" />
                  </svg>
                </>
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default MiPerfil;
