document.getElementById('checkcontra').addEventListener('keydown', e => { if (e.key === 'Enter') crearCuenta(); });

async function crearCuenta() {
  const nombre    = document.getElementById('nombre').value.trim();
  const apellido  = document.getElementById('apellido').value.trim();
  const telefono  = document.getElementById('telefono').value.trim();
  const zona      = document.getElementById('zona').value.trim();
  const domicilio = document.getElementById('domicilio').value.trim();
  const correo    = document.getElementById('correo').value.trim();
  const contra    = document.getElementById('contra').value;
  const check     = document.getElementById('checkcontra').value;
  const btn       = document.getElementById('btn-registro');

  ocultarError();

  if (!nombre || !apellido || !telefono || !zona || !correo || !contra) {
    mostrarError('Por favor llena todos los campos obligatorios.'); return;
  }
  if (!correo.includes('@')) { mostrarError('Correo inválido.'); return; }
  if (contra !== check) { mostrarError('Las contraseñas no coinciden.'); return; }
  if (contra.length < 8) { mostrarError('La contraseña debe tener al menos 8 caracteres.'); return; }
  if (!/[A-Z]/.test(contra) || !/\d/.test(contra) || !/[^A-Za-z0-9]/.test(contra)) {
    mostrarError('La contraseña debe incluir al menos 1 mayúscula, 1 número y 1 carácter especial.'); return;
  }

  btn.disabled    = true;
  btn.textContent = 'Creando cuenta...';

  try {
    const res  = await fetch(`${API}/auth/register`, {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify({ nombre, apellido, telefono, zona, domicilio, correo, password: contra })
    });
    const data = await res.json();

    if (!res.ok || data.errorMessage) {
      mostrarError(data.errorMessage || 'No se pudo crear la cuenta. El correo podría ya estar registrado.');
      return;
    }

    setSession({ idUsuario: data.idUsuario, nombre: data.nombre, correo: data.correo, token: data.token });
    window.location.href = 'dashboard.html';
  } catch (e) {
    mostrarError('No se pudo conectar con el servidor. Verifica tu conexión.');
  } finally {
    btn.disabled    = false;
    btn.textContent = 'Crear cuenta';
  }
}

function mostrarError(msg) {
  const el = document.getElementById('error-msg');
  el.textContent   = msg;
  el.style.display = 'block';
}
function ocultarError() {
  document.getElementById('error-msg').style.display = 'none';
}
