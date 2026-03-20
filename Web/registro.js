// ── Enter en el último campo dispara el registro ──
document.getElementById('checkcontra').addEventListener('keydown', e => { if (e.key === 'Enter') crearCuenta(); });

function crearCuenta() {
  const nombre   = document.getElementById('nombre').value.trim();
  const apellido = document.getElementById('apellido').value.trim();
  const telefono = document.getElementById('telefono').value.trim();
  const zona     = document.getElementById('zona').value.trim();
  const correo   = document.getElementById('correo').value.trim();
  const contra   = document.getElementById('contra').value;
  const check    = document.getElementById('checkcontra').value;

  ocultarError();

  if (!nombre || !apellido || !telefono || !zona || !correo || !contra) { mostrarError('Por favor llena todos los campos obligatorios.'); return; }
  if (!correo.includes('@')) { mostrarError('Correo inválido.'); return; }
  if (contra !== check) { mostrarError('Las contraseñas no coinciden.'); return; }
  if (contra.length < 8) { mostrarError('La contraseña debe tener al menos 8 caracteres.'); return; }
  if (!/[A-Z]/.test(contra) || !/\d/.test(contra) || !/[^A-Za-z0-9]/.test(contra)) {
    mostrarError('La contraseña debe incluir al menos 1 mayúscula, 1 número y 1 carácter especial.'); return;
  }

  // TODO: conectar a POST /auth/register cuando esté el backend
  window.location.href = 'dashboard.html';
}

function mostrarError(msg) {
  const el = document.getElementById('error-msg');
  el.textContent = msg;
  el.style.display = 'block';
}
function ocultarError() {
  document.getElementById('error-msg').style.display = 'none';
}