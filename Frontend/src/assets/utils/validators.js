export const validators = {
  username: (value) => {
    if (!value || value.trim() === '') {
      return 'El nombre de usuario es requerido';
    }
    if (value.length < 3) {
      return 'El usuario debe tener al menos 3 caracteres';
    }
    if (value.length > 50) {
      return 'El usuario no puede exceder 50 caracteres';
    }
    if (!/^[a-zA-Z0-9_]+$/.test(value)) {
      return 'Solo letras, números y guiones bajos';
    }
    return null;
  },

  email: (value) => {
    if (!value || value.trim() === '') {
      return 'El correo electrónico es requerido';
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(value)) {
      return 'Ingresa un correo electrónico válido';
    }
    return null;
  },

  password: (value) => {
    if (!value || value === '') {
      return 'La contraseña es requerida';
    }
    if (value.length < 6) {
      return 'La contraseña debe tener al menos 6 caracteres';
    }
    if (value.length > 100) {
      return 'La contraseña no puede exceder 100 caracteres';
    }
    return null;
  },

  confirmPassword: (password, confirmPassword) => {
    if (!confirmPassword) {
      return 'Confirma tu contraseña';
    }
    if (password !== confirmPassword) {
      return 'Las contraseñas no coinciden';
    }
    return null;
  },

  nombre: (value) => {
    if (!value || value.trim() === '') {
      return 'El nombre es requerido';
    }
    if (value.trim().length < 2) {
      return 'El nombre debe tener al menos 2 caracteres';
    }
    if (value.length > 50) {
      return 'El nombre no puede exceder 50 caracteres';
    }
    return null;
  },

  apellido: (value) => {
    if (!value || value.trim() === '') {
      return 'El apellido es requerido';
    }
    if (value.trim().length < 2) {
      return 'El apellido debe tener al menos 2 caracteres';
    }
    if (value.length > 50) {
      return 'El apellido no puede exceder 50 caracteres';
    }
    return null;
  },
};

export const validateForm = (fields, rules) => {
  const errors = {};
  let isValid = true;

  for (const [field, value] of Object.entries(fields)) {
    if (rules[field]) {
      const error = rules[field](value);
      if (error) {
        errors[field] = error;
        isValid = false;
      }
    }
  }

  return { isValid, errors };
};
