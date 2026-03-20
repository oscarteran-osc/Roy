// ── Enter en cualquier campo dispara el login ──
document.getElementById('correo').addEventListener('keydown', e => { if (e.key === 'Enter') iniciarSesion(); });
document.getElementById('contra').addEventListener('keydown', e => { if (e.key === 'Enter') iniciarSesion(); });

function iniciarSesion() {
  const correo = document.getElementById('correo').value.trim();
  const contra = document.getElementById('contra').value;

  ocultarError();

  if (!correo || !contra) { mostrarError('Por favor ingresa tu correo y contraseña.'); return; }
  if (!correo.includes('@')) { mostrarError('Ingresa un correo válido.'); return; }

  // TODO: conectar a POST /auth/login cuando esté el backend
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