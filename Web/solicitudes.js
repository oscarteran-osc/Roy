function cambiarTab(tab) {
  document.getElementById('panel-rentador').style.display   = tab === 'rentador'  ? 'block' : 'none';
  document.getElementById('panel-ofertante').style.display  = tab === 'ofertante' ? 'block' : 'none';
  document.getElementById('btn-rentador').classList.toggle('active',  tab === 'rentador');
  document.getElementById('btn-ofertante').classList.toggle('active', tab === 'ofertante');
}

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

const placeholder = 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=100&q=80';

const estadoClass = {
  aceptada: 'aceptada', pendiente: 'pendiente', rechazada: 'rechazada',
  completada: 'aceptada', pagada: 'aceptada'
};
function getEstadoClass(estado) {
  return estadoClass[(estado || '').toLowerCase()] || '';
}

function renderSolicitudRentador(s) {
  const estado = (s.estado || '').toLowerCase();
  const btnAccion = estado === 'aceptada'
    ? `<a href="pagar.html?id=${s.idSolicitud}" class="btn-pagar">Pagar</a>`
    : estado === 'pendiente'
    ? `<button class="btn-cancelar-sol" onclick="cancelarSolicitud(${s.idSolicitud}, this)">Cancelar</button>`
    : `<span>—</span>`;

  return `
    <div class="sol-fila" data-id="${s.idSolicitud}">
      <div class="sol-articulo">
        <img src="${s.imagenObjeto || placeholder}" alt="${s.nombreObjeto || 'Objeto'}"
             onerror="this.src='${placeholder}'"/>
        <div>
          <p class="sol-art-nombre">${s.nombreObjeto || 'Objeto'}</p>
          <p class="sol-art-de">De: <span>${s.nombreArrendador || '—'}</span></p>
          <a href="detalle-objeto.html?id=${s.idObjeto}" class="sol-ver-detalles">Ver detalles</a>
        </div>
      </div>
      <div class="sol-detalles">
        <p><strong>Fecha inicio:</strong> ${s.fechaInicio}</p>
        <p><strong>Fecha fin:</strong> ${s.fechaFin}</p>
        <p><strong>Estado:</strong> <span class="sol-estado ${estadoClass[s.estado] || ''}">${s.estado}</span></p>
        ${s.monto ? `<p><strong>Monto:</strong> $${s.monto}</p>` : ''}
      </div>
      <div class="sol-acciones">
        ${btnAccion}
        <a href="chat.html?idSolicitud=${s.idSolicitud}&otroUsuario=${s.idUsArrendador}&nombre=${encodeURIComponent(s.nombreArrendador || 'Arrendador')}" class="btn-chat-sol">💬 Chat</a>
      </div>
    </div>`;
}

function renderSolicitudOfertante(s) {
  const estado = (s.estado || '').toLowerCase();
  const btnAccion = estado === 'pendiente'
    ? `<button class="btn-pagar" onclick="accionSolicitud(${s.idSolicitud}, 'aprobar', this)">Aceptar</button>
       <button class="btn-cancelar-sol" style="margin-top:8px;" onclick="accionSolicitud(${s.idSolicitud}, 'rechazar', this)">Rechazar</button>`
    : `<span>—</span>`;

  return `
    <div class="sol-fila" data-id="${s.idSolicitud}">
      <div class="sol-articulo">
        <img src="${s.imagenObjeto || placeholder}" alt="${s.nombreObjeto || 'Objeto'}"
             onerror="this.src='${placeholder}'"/>
        <div>
          <p class="sol-art-nombre">${s.nombreObjeto || 'Objeto'}</p>
          <p class="sol-art-de">Para: <span>${s.nombreArrendatario || '—'}</span></p>
          <a href="detalle-objeto.html?id=${s.idObjeto}" class="sol-ver-detalles">Ver detalles</a>
        </div>
      </div>
      <div class="sol-detalles">
        <p><strong>Fecha inicio:</strong> ${s.fechaInicio}</p>
        <p><strong>Fecha fin:</strong> ${s.fechaFin}</p>
        <p><strong>Estado:</strong> <span class="sol-estado ${estadoClass[s.estado] || ''}">${s.estado}</span></p>
        ${s.monto ? `<p><strong>Monto:</strong> $${s.monto}</p>` : ''}
      </div>
      <div class="sol-acciones">
        ${btnAccion}
        <a href="chat.html?idSolicitud=${s.idSolicitud}&otroUsuario=${s.idUsArrendatario}&nombre=${encodeURIComponent(s.nombreArrendatario || 'Arrendatario')}" class="btn-chat-sol">💬 Chat</a>
      </div>
    </div>`;
}

async function cargarSolicitudes() {
  const session = getSession();
  if (!session) return;

  try {
    const [resRentador, resOfertante] = await Promise.all([
      fetch(`${API}/api/solicitudes/arrendatario/${session.idUsuario}`),
      fetch(`${API}/api/solicitudes/arrendador/${session.idUsuario}`)
    ]);

    const rentador  = resRentador.ok  ? await resRentador.json()  : [];
    const ofertante = resOfertante.ok ? await resOfertante.json() : [];

    const panelR = document.getElementById('panel-rentador');
    const panelO = document.getElementById('panel-ofertante');

    panelR.innerHTML = rentador.length
      ? rentador.map(renderSolicitudRentador).join('')
      : '<p style="padding:20px;color:#888;">No tienes solicitudes como rentador.</p>';

    panelO.innerHTML = ofertante.length
      ? ofertante.map(renderSolicitudOfertante).join('')
      : '<p style="padding:20px;color:#888;">No tienes solicitudes como ofetante.</p>';

  } catch (e) {
    console.warn('No se pudieron cargar las solicitudes.');
  }
}

async function accionSolicitud(idSolicitud, accion, btn) {
  btn.disabled = true;
  try {
    const res = await fetch(`${API}/api/solicitudes/${idSolicitud}/${accion}`, { method: 'PUT' });
    if (res.ok) {
      cargarSolicitudes();
    } else {
      alert('No se pudo realizar la acción.');
      btn.disabled = false;
    }
  } catch (e) {
    alert('No se pudo conectar con el servidor.');
    btn.disabled = false;
  }
}

async function cancelarSolicitud(idSolicitud, btn) {
  if (!confirm('¿Deseas cancelar esta solicitud?')) return;
  btn.disabled = true;
  try {
    const res = await fetch(`${API}/api/solicitudes/${idSolicitud}`, { method: 'DELETE' });
    if (res.ok) {
      cargarSolicitudes();
    } else {
      alert('No se pudo cancelar la solicitud.');
      btn.disabled = false;
    }
  } catch (e) {
    alert('No se pudo conectar con el servidor.');
    btn.disabled = false;
  }
}

cargarSolicitudes();

