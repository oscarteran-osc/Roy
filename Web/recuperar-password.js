function togglePassword(inputId, icon) {
  const input = document.getElementById(inputId);
  if (input.type === 'password') { input.type = 'text'; icon.textContent = '🙈'; }
  else { input.type = 'password'; icon.textContent = '👁️'; }
}

// Obtener el token de la URL
const params = new URLSearchParams(window.location.search);
const token  = params.get('token');

if (!token) {
  document.getElementById('error-msg').textContent = 'Enlace inválido. Solicita uno nuevo desde el login.';
  document.getElementById('error-msg').style.display = 'block';
  document.getElementById('form-reset').style.display = 'none';
}

async function resetearPassword() {
  const nueva     = document.getElementById('nueva-contra').value;
  const confirmar = document.getElementById('confirmar-contra').value;
  const btn       = document.getElementById('btn-reset');
  const errEl     = document.getElementById('error-msg');
  const okEl      = document.getElementById('exito-msg');

  errEl.style.display = 'none';
  okEl.style.display  = 'none';

  if (!nueva || nueva.length < 6) {
    errEl.textContent   = 'La contraseña debe tener al menos 6 caracteres.';
    errEl.style.display = 'block';
    return;
  }
  if (nueva !== confirmar) {
    errEl.textContent   = 'Las contraseñas no coinciden.';
    errEl.style.display = 'block';
    return;
  }

  btn.disabled    = true;
  btn.textContent = 'Restableciendo...';

  try {
    const res  = await fetch(`${API}/auth/resetear-password`, {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify({ token, nuevaPassword: nueva })
    });
    const data = await res.json();

    if (!res.ok) {
      errEl.textContent   = data.error || 'Ocurrió un error. Intenta de nuevo.';
      errEl.style.display = 'block';
      btn.disabled        = false;
      btn.textContent     = 'Restablecer contraseña';
      return;
    }

    document.getElementById('form-reset').style.display = 'none';
    okEl.textContent   = '✓ Contraseña actualizada. Ahora puedes iniciar sesión.';
    okEl.style.display = 'block';

    setTimeout(() => window.location.href = 'login.html', 2500);
  } catch (e) {
    errEl.textContent   = 'No se pudo conectar con el servidor.';
    errEl.style.display = 'block';
    btn.disabled        = false;
    btn.textContent     = 'Restablecer contraseña';
  }
}
