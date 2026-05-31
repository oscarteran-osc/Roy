document.getElementById('correo').addEventListener('keydown', e => { if (e.key === 'Enter') iniciarSesion(); });
document.getElementById('contra').addEventListener('keydown', e => { if (e.key === 'Enter') iniciarSesion(); });

function togglePassword(inputId, icon) {
  const input = document.getElementById(inputId);
  if (input.type === 'password') { input.type = 'text'; icon.textContent = '🙈'; }
  else { input.type = 'password'; icon.textContent = '👁️'; }
}

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

// ===== RECUPERAR CONTRASEÑA =====

function abrirModalRecuperar() {
  const modal = document.getElementById('modal-recuperar');
  modal.style.display = 'flex';
  document.getElementById('correo-recuperar').value = '';
  document.getElementById('recuperar-error').style.display = 'none';
  document.getElementById('recuperar-exito').style.display = 'none';
  document.getElementById('btn-recuperar').disabled = false;
  document.getElementById('btn-recuperar').textContent = 'Enviar enlace';
  setTimeout(() => document.getElementById('correo-recuperar').focus(), 100);
}

function cerrarModalRecuperar() {
  document.getElementById('modal-recuperar').style.display = 'none';
}

document.getElementById('modal-recuperar').addEventListener('click', function(e) {
  if (e.target === this) cerrarModalRecuperar();
});

document.addEventListener('keydown', e => {
  if (e.key === 'Escape') cerrarModalRecuperar();
});

async function enviarRecuperacion() {
  const correo = document.getElementById('correo-recuperar').value.trim();
  const btn    = document.getElementById('btn-recuperar');
  const errEl  = document.getElementById('recuperar-error');
  const okEl   = document.getElementById('recuperar-exito');

  errEl.style.display = 'none';
  okEl.style.display  = 'none';

  if (!correo || !correo.includes('@')) {
    errEl.textContent   = 'Ingresa un correo válido.';
    errEl.style.display = 'block';
    return;
  }

  btn.disabled    = true;
  btn.textContent = 'Enviando...';

  try {
    const res  = await fetch(`${API}/auth/recuperar-password`, {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify({ correo })
    });
    const data = await res.json();

    okEl.textContent   = data.mensaje || 'Si el correo está registrado, recibirás un enlace en breve.';
    okEl.style.display = 'block';
    btn.textContent    = '✓ Enviado';
  } catch (e) {
    errEl.textContent   = 'No se pudo conectar con el servidor.';
    errEl.style.display = 'block';
    btn.disabled        = false;
    btn.textContent     = 'Enviar enlace';
  }
}
