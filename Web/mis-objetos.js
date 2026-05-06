let misObjetos  = [];
let objetoActual = null;

document.getElementById('searchInput').addEventListener('keydown', function(e) {
  if (e.key === 'Enter' && this.value.trim() !== '') {
    window.location.href = 'resultados.html?q=' + encodeURIComponent(this.value.trim());
  }
});

document.querySelectorAll('.cat-nav-btn').forEach(btn => {
  btn.addEventListener('click', function() {
    const cat = this.dataset.cat;
    window.location.href = cat === 'todas' ? 'dashboard.html' : 'resultados.html?cat=' + encodeURIComponent(cat);
  });
});

document.querySelectorAll('.modal-overlay').forEach(overlay => {
  overlay.addEventListener('click', function(e) {
    if (e.target === this) cerrarModal(this.id);
  });
});

async function cargarMisObjetos() {
  const session = getSession();
  if (!session) return;
  try {
    const res = await fetch(`${API}/api/objeto/arrendador/${session.idUsuario}`);
    if (!res.ok) return;
    const objetos = await res.json();
    misObjetos = objetos;
    renderMisObjetos(objetos);
  } catch (e) {
    console.warn('No se pudieron cargar los objetos.');
  }
}

function renderMisObjetos(objetos) {
  const lista       = document.querySelector('.misobjetos-list');
  const placeholder = 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=120&q=80';

  if (!objetos || objetos.length === 0) {
    lista.innerHTML = '<p style="padding:20px;color:#888;">No tienes objetos registrados aún.</p>';
    return;
  }

  lista.innerHTML = objetos.map((obj, i) => `
    <div class="miobjeto-row">
      <img class="miobjeto-img"
           src="${obj.imagenUrl || placeholder}"
           alt="${obj.nombreObjeto}"
           onerror="this.src='${placeholder}'"/>
      <div class="miobjeto-info">
        <p class="miobjeto-nombre">${obj.nombreObjeto}</p>
        <p class="miobjeto-cat">${obj.categoria || ''}</p>
        <div class="miobjeto-btns">
          <button class="btn-ver-detalles" onclick="abrirDetalles(${i})">Ver detalles</button>
          <button class="btn-ver-solicitudes" onclick="abrirSolicitudes(${obj.idObjeto}, '${obj.nombreObjeto.replace(/'/g, "\\'")}')">Solicitudes</button>
          <button class="btn-eliminar" onclick="eliminarObjeto(this, ${obj.idObjeto})">🗑</button>
        </div>
      </div>
      <div class="miobjeto-precio">$${obj.precio}</div>
    </div>
  `).join('');
}

function abrirDetalles(index) {
  const obj    = misObjetos[index];
  objetoActual = obj;

  document.getElementById('modal-nombre').value = obj.nombreObjeto;
  document.getElementById('modal-precio').value = obj.precio;
  document.getElementById('modal-desc').value   = obj.descripcion || '';

  const select = document.getElementById('modal-cat');
  for (let opt of select.options) {
    if (opt.text === (obj.categoria || '')) { opt.selected = true; break; }
  }

  const img = obj.imagenUrl || 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600&q=80';
  document.getElementById('modal-img-principal').src = img;
  document.getElementById('modal-thumb-1').src       = img;
  document.getElementById('modal-thumb-2').src       = img;

  document.getElementById('modal-detalles').style.display = 'flex';
}

async function guardarDetalles() {
  if (!objetoActual) return;
  const session = getSession();
  const body = {
    idUsArrendador: session ? session.idUsuario : objetoActual.idUsArrendador,
    nombreObjeto:   document.getElementById('modal-nombre').value.trim(),
    precio:         parseFloat(document.getElementById('modal-precio').value) || objetoActual.precio,
    categoria:      document.getElementById('modal-cat').value,
    descripcion:    document.getElementById('modal-desc').value.trim(),
    estado:         objetoActual.estado,
    imagenUrl:      objetoActual.imagenUrl
  };

  try {
    const res = await fetch(`${API}/api/objeto/objeto/${objetoActual.idObjeto}`, {
      method:  'PUT',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify(body)
    });
    if (res.ok) {
      cerrarModal('modal-detalles');
      cargarMisObjetos();
    } else {
      alert('Error al guardar los cambios.');
    }
  } catch (e) {
    alert('No se pudo conectar con el servidor.');
  }
}

async function abrirSolicitudes(idObjeto, nombre) {
  document.getElementById('modal-sol-titulo').textContent = `Solicitudes – ${nombre}`;
  const grid = document.querySelector('.modal-sol-grid');
  grid.innerHTML = '<p style="padding:16px;color:#888;">Cargando...</p>';
  document.getElementById('modal-solicitudes').style.display = 'flex';

  try {
    const res        = await fetch(`${API}/api/solicitudes/objeto/${idObjeto}`);
    const solicitudes = res.ok ? await res.json() : [];

    if (!solicitudes.length) {
      grid.innerHTML = '<p style="padding:16px;color:#888;">Sin solicitudes.</p>';
      return;
    }

    grid.innerHTML = solicitudes.map(s => `
      <div class="sol-persona-card">
        <div class="sol-avatar">👤</div>
        <p class="sol-nombre">${s.nombreArrendatario || 'Usuario'}</p>
        <p class="sol-fecha">${s.fechaInicio} → ${s.fechaFin}</p>
        <p style="font-size:.8rem;color:#888;">${s.estado}</p>
      </div>
    `).join('');
  } catch (e) {
    grid.innerHTML = '<p style="padding:16px;color:#888;">Error al cargar.</p>';
  }
}

function cerrarModal(id) {
  document.getElementById(id).style.display = 'none';
}

function cambiarModalImg(thumb) {
  document.querySelectorAll('.modal-thumb').forEach(t => t.classList.remove('active'));
  thumb.classList.add('active');
  const principal = document.getElementById('modal-img-principal');
  principal.style.opacity = '0';
  setTimeout(() => {
    principal.src           = thumb.src;
    principal.style.opacity = '1';
  }, 150);
}

async function eliminarObjeto(btn, idObjeto) {
  if (!confirm('¿Estás seguro de que quieres eliminar este objeto?')) return;
  try {
    const res = await fetch(`${API}/api/objeto/objeto/${idObjeto}`, { method: 'DELETE' });
    if (res.ok) {
      const row = btn.closest('.miobjeto-row');
      row.style.opacity = '0';
      setTimeout(() => { row.remove(); }, 300);
    } else {
      alert('No se pudo eliminar el objeto.');
    }
  } catch (e) {
    alert('No se pudo conectar con el servidor.');
  }
}

cargarMisObjetos();
