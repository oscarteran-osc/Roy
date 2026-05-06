async function cargarPerfil() {
  const session = getSession();
  if (!session) return;

  try {
    const res = await fetch(`${API}/Roy/api/usuario/${session.idUsuario}`);
    if (!res.ok) return;
    const u = await res.json();

    const sideNombre = document.getElementById('sidebar-nombre');
    const sideZona   = document.getElementById('sidebar-zona');
    if (sideNombre) sideNombre.textContent = `${u.nombre} ${u.apellido}`;
    if (sideZona)   sideZona.textContent   = u.zona || '—';

    const elTel    = document.getElementById('cuenta-telefono');
    const elCorreo = document.getElementById('cuenta-correo');
    if (elTel)    elTel.textContent    = u.telefono || '—';
    if (elCorreo) elCorreo.textContent = u.correo   || '—';

    const iNombre   = document.getElementById('edit-nombre');
    const iApellido = document.getElementById('edit-apellido');
    const iTelefono = document.getElementById('edit-telefono');
    const iCorreo   = document.getElementById('edit-correo');
    const iZona     = document.getElementById('edit-zona');
    if (iNombre)   iNombre.value   = u.nombre   || '';
    if (iApellido) iApellido.value = u.apellido || '';
    if (iTelefono) iTelefono.value = u.telefono || '';
    if (iCorreo)   iCorreo.value   = u.correo   || '';
    if (iZona)     iZona.value     = u.zona     || '';
  } catch (e) {
    console.warn('No se pudo cargar el perfil.');
  }

  // Cargar promedio de estrellas
  try {
    const rRes = await fetch(`${API}/api/resenas/promedio/${session.idUsuario}`);
    if (rRes.ok) {
      const r = await rRes.json();
      const el = document.getElementById('sidebar-rating');
      if (el) el.textContent = r.promedioCalificacion
        ? `⭐ ${parseFloat(r.promedioCalificacion).toFixed(1)}`
        : '⭐ Sin reseñas';
    }
  } catch (e) {}
}

async function cargarHistorial() {
  const session = getSession();
  if (!session) return;
  const lista = document.getElementById('historial-lista');
  if (!lista) return;

  try {
    const res = await fetch(`${API}/api/solicitudes/arrendatario/${session.idUsuario}`);
    const solicitudes = res.ok ? await res.json() : [];

    if (!solicitudes.length) {
      lista.innerHTML = '<p style="color:#888;">No tienes rentas en tu historial.</p>';
      return;
    }

    const placeholder = 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=80&q=80';
    lista.innerHTML = solicitudes.map(s => `
      <div class="historial-item">
        <img src="${s.imagenObjeto || placeholder}" alt="${s.nombreObjeto || 'Objeto'}"
             onerror="this.src='${placeholder}'"/>
        <div>
          <p class="historial-nombre">${s.nombreObjeto || 'Objeto'}</p>
          <p class="historial-fechas">${s.fechaInicio || ''} — ${s.fechaFin || ''}</p>
          <p style="font-size:.8rem;color:#888;">Estado: ${s.estado || ''}</p>
        </div>
      </div>`).join('');
  } catch (e) {
    lista.innerHTML = '<p style="color:#888;">No se pudo cargar el historial.</p>';
  }
}

function cambiarPanel(panel) {
  document.getElementById('panel-info').style.display      = panel === 'info'      ? 'block' : 'none';
  document.getElementById('panel-historial').style.display = panel === 'historial' ? 'block' : 'none';
  document.querySelectorAll('.cuenta-sidebar-link').forEach(btn => btn.classList.remove('active'));
  event.target.classList.add('active');
  if (panel === 'historial') cargarHistorial();
}

function abrirModalEditar() {
  document.getElementById('modal-editar').style.display = 'flex';
}
function cerrarModalEditar() {
  document.getElementById('modal-editar').style.display = 'none';
}

async function guardarPerfil() {
  const session = getSession();
  if (!session) return;

  const nombre   = document.getElementById('edit-nombre')?.value.trim()   || '';
  const apellido = document.getElementById('edit-apellido')?.value.trim() || '';
  const telefono = document.getElementById('edit-telefono')?.value.trim() || '';
  const correo   = document.getElementById('edit-correo')?.value.trim()   || '';
  const zona     = document.getElementById('edit-zona')?.value.trim()     || '';
  const password = document.getElementById('edit-password')?.value        || '';

  const body = { nombre, apellido, telefono, correo, zona };
  if (password) body.password = password;

  try {
    const res = await fetch(`${API}/Roy/api/usuario/${session.idUsuario}`, {
      method:  'PUT',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify(body)
    });

    if (res.ok) {
      const updated = await res.json();
      const s = getSession();
      setSession({ ...s, nombre: updated.nombre, correo: updated.correo });
      cerrarModalEditar();
      cargarPerfil();
    } else {
      alert('Error al guardar los cambios.');
    }
  } catch (e) {
    alert('No se pudo conectar con el servidor.');
  }
}

document.getElementById('modal-editar').addEventListener('click', function(e) {
  if (e.target === this) cerrarModalEditar();
});

document.getElementById('searchInput').addEventListener('keydown', function(e) {
  if (e.key === 'Enter' && this.value.trim() !== '')
    window.location.href = 'resultados.html?q=' + encodeURIComponent(this.value.trim());
});

document.querySelectorAll('.cat-nav-btn').forEach(btn => {
  btn.addEventListener('click', function() {
    const cat = this.dataset.cat;
    window.location.href = cat === 'todas' ? 'dashboard.html' : 'resultados.html?cat=' + encodeURIComponent(cat);
  });
});

const btnCerrar = document.querySelector('.cuenta-cerrar-sesion');
if (btnCerrar) {
  btnCerrar.addEventListener('click', function(e) {
    e.preventDefault();
    clearSession();
    window.location.href = 'index.html';
  });
}

cargarPerfil();