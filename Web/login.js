document.getElementById('correo').addEventListener('keydown', e => { if (e.key === 'Enter') iniciarSesion(); });
document.getElementById('contra').addEventListener('keydown', e => { if (e.key === 'Enter') iniciarSesion(); });

async function iniciarSesion() {
  const correo = document.getElementById('correo').value.trim();
  const contra = document.getElementById('contra').value;
  const btn    = document.getElementById('btn-login');

  ocultarError();

  if (!correo || !contra) { mostrarError('Por favor ingresa tu correo y contraseña.'); return; }
  if (!correo.includes('@')) { mostrarError('Ingresa un correo válido.'); return; }

  btn.disabled    = true;
  btn.textContent = 'Iniciando...';

  try {
    const res  = await fetch(`${API}/auth/login`, {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify({ correo, password: contra })
    });
    const data = await res.json();

    if (!res.ok || data.errorMessage) {
      mostrarError(data.errorMessage || 'Correo o contraseña incorrectos.');
      return;
    }

    setSession({ idUsuario: data.idUsuario, nombre: data.nombre, correo: data.correo, token: data.token });
    window.location.href = 'dashboard.html';
  } catch (e) {
    mostrarError('No se pudo conectar con el servidor. Verifica tu conexión.');
  } finally {
    btn.disabled    = false;
    btn.textContent = 'Iniciar Sesión';
  }
}

function mostrarError(msg) {
  const el = document.getElementById('error-msg');
  el.textContent  = msg;
  el.style.display = 'block';
}
function ocultarError() {
  document.getElementById('error-msg').style.display = 'none';
}
