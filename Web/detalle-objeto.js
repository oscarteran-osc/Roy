const params      = new URLSearchParams(window.location.search);
const idObjeto    = params.get('id');
const placeholder = 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600&q=80';
const catNombres  = {
  eventos:'Eventos', tecnologia:'Tecnología', transporte:'Transporte',
  herramientas:'Herramientas', deporte:'Deporte', instrumentos:'Instrumentos musicales'
};

let objetoData  = null;
let estrellasSel = 0;

async function cargarDetalle() {
  if (idObjeto) {
    try {
      const res = await fetch(`${API}/api/objeto/objeto/${idObjeto}`);
      if (res.ok) {
        objetoData = await res.json();
        pintarDetalle(objetoData);
        cargarResenas();
        return;
      }
    } catch (e) {}
  }
  objetoData = {
    nombreObjeto: params.get('nombre') || 'Objeto',
    precio:       params.get('precio') || '0',
    categoria:    params.get('cat')    || 'General',
    zona:         params.get('zona')   || 'Sin zona',
    imagenUrl:    params.get('img')    || placeholder,
    descripcion:  params.get('desc')   || 'Sin descripción disponible.',
    nomArrendador:'Arrendador'
  };
  pintarDetalle(objetoData);
}

function pintarDetalle(obj) {
  const catNombre = catNombres[obj.categoria] || obj.categoria || 'General';
  document.getElementById('detalle-nombre').textContent        = obj.nombreObjeto;
  document.getElementById('detalle-precio').textContent        = '$' + obj.precio;
  document.getElementById('detalle-zona').textContent          = '📍 ' + (obj.zona || 'Sin zona');
  document.getElementById('detalle-cat').textContent           = catNombre;
  document.getElementById('detalle-vendedor-link').textContent = obj.nomArrendador || 'Arrendador';
  const descItems = (obj.descripcion || 'Sin descripción.').split('. ').filter(d => d.trim());
  document.getElementById('detalle-desc-list').innerHTML =
    descItems.map(d => `<li>${d.trim().replace(/\.$/, '')}.</li>`).join('');
  const img = obj.imagenUrl || placeholder;
  document.getElementById('img-principal').src = img;
  document.querySelectorAll('.thumb').forEach(t => { t.src = img; });
  document.getElementById('breadcrumb-cat').textContent = catNombre;
  document.getElementById('breadcrumb-cat').href        = 'resultados.html?cat=' + (obj.categoria || '');
  document.getElementById('breadcrumb-nombre').textContent = obj.nombreObjeto;
  document.title = `ROY – ${obj.nombreObjeto}`;
}

async function cargarResenas() {
  if (!idObjeto) return;
  try {
    const res     = await fetch(`${API}/api/resenas/objeto/${idObjeto}`);
    const resenas = res.ok ? await res.json() : [];
    pintarResenas(resenas);
  } catch (e) {
    pintarResenas([]);
  }
}

function pintarResenas(resenas) {
  const total = resenas.length;
  const conteo = [0, 0, 0, 0, 0];
  resenas.forEach(r => { if (r.calificacion >= 1 && r.calificacion <= 5) conteo[r.calificacion - 1]++; });
  const promedio = total ? (resenas.reduce((s, r) => s + r.calificacion, 0) / total) : 0;

  document.getElementById('resenas-promedio').textContent = promedio.toFixed(1);
  document.getElementById('resenas-total').textContent    = `${total} opinión${total !== 1 ? 'es' : ''}`;
  document.getElementById('rating-num-header').textContent = promedio.toFixed(1);
  pintarEstrellas('stars-header', promedio);
  pintarEstrellas('stars-resumen', promedio);

  for (let i = 5; i >= 1; i--) {
    const pct = total ? Math.round((conteo[i - 1] / total) * 100) : 0;
    document.getElementById(`barra-fill-${i}`).style.width = pct + '%';
    document.getElementById(`barra-pct-${i}`).textContent  = pct + '%';
  }

  const lista = document.getElementById('resenas-lista');
  if (!total) {
    lista.innerHTML = '<p style="color:#888;padding:16px 0;">Aún no hay reseñas para este objeto.</p>';
    return;
  }
  lista.innerHTML = resenas.map(r => {
    const iniciales = (r.nombreAutor || 'U').split(' ').map(p => p[0]).join('').substring(0, 2).toUpperCase();
    const fecha     = r.fechaResena ? new Date(r.fechaResena).toLocaleDateString('es-MX', { year:'numeric', month:'long', day:'numeric' }) : '';
    return `
      <div class="resena-card">
        <div class="resena-header">
          <div class="resena-avatar">${iniciales}</div>
          <div>
            <p class="resena-nombre">${r.nombreAutor || 'Usuario'}</p>
            <div class="stars small">${estrellasSVG(r.calificacion)}</div>
            <p class="resena-fecha">Publicado el ${fecha}</p>
          </div>
        </div>
        <p class="resena-texto">${r.comentario || ''}</p>
      </div>`;
  }).join('');
}

function pintarEstrellas(containerId, valor) {
  const el = document.getElementById(containerId);
  if (!el) return;
  el.innerHTML = estrellasSVG(valor);
}

function estrellasSVG(valor) {
  let html = '';
  for (let i = 1; i <= 5; i++) {
    if (valor >= i)            html += '<span class="star filled">★</span>';
    else if (valor >= i - 0.5) html += '<span class="star half">★</span>';
    else                       html += '<span class="star">★</span>';
  }
  return html;
}

function seleccionarEstrella(n) {
  estrellasSel = n;
  document.querySelectorAll('.estrella-input').forEach((el, i) => {
    el.classList.toggle('filled', i < n);
  });
}

async function enviarResena() {
  const session = getSession();
  if (!session) { alert('Debes iniciar sesión para dejar una reseña.'); return; }
  if (!estrellasSel) { alert('Selecciona una calificación.'); return; }
  const comentario = document.getElementById('resena-comentario').value.trim();
  if (!comentario) { alert('Escribe un comentario.'); return; }

  const body = {
    idObjeto:     parseInt(idObjeto),
    idUsAutor:    session.idUsuario,
    idUsReceptor: objetoData?.idUsArrendador || 0,
    calificacion: estrellasSel,
    comentario
  };

  const btn = document.getElementById('btn-enviar-resena');
  btn.disabled = true; btn.textContent = 'Enviando...';

  try {
    const res = await fetch(`${API}/api/resenas`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    if (res.ok) {
      document.getElementById('resena-comentario').value = '';
      estrellasSel = 0;
      document.querySelectorAll('.estrella-input').forEach(el => el.classList.remove('filled'));
      cargarResenas();
    } else {
      const err = await res.json().catch(() => ({}));
      alert(err.error || 'No se pudo enviar la reseña.');
    }
  } catch (e) {
    alert('No se pudo conectar con el servidor.');
  } finally {
    btn.disabled = false; btn.textContent = 'Publicar reseña';
  }
}

function cambiarImg(thumb) {
  document.querySelectorAll('.thumb').forEach(t => t.classList.remove('active'));
  thumb.classList.add('active');
  const imgGrande = document.getElementById('img-principal');
  imgGrande.style.opacity = '0';
  setTimeout(() => { imgGrande.src = thumb.src; imgGrande.style.opacity = '1'; }, 150);
}

document.getElementById('searchInput').addEventListener('keydown', function(e) {
  if (e.key === 'Enter' && this.value.trim() !== '')
    window.location.href = 'resultados.html?q=' + encodeURIComponent(this.value.trim());
});

document.querySelectorAll('.cat-nav-btn').forEach(btn => {
  btn.addEventListener('click', function() {
    const c = this.dataset.cat;
    window.location.href = c === 'todas' ? 'dashboard.html' : 'resultados.html?cat=' + encodeURIComponent(c);
  });
});

document.querySelector('.btn-solicitud').addEventListener('click', async function() {
  const session = getSession();
  if (!session) { window.location.href = 'login.html'; return; }
  if (!objetoData?.idObjeto) { alert('No se pudo identificar el objeto.'); return; }
  const fechaInicio = document.getElementById('fecha-inicio').value;
  const fechaFin    = document.getElementById('fecha-fin').value;
  if (!fechaInicio || !fechaFin) { alert('Por favor selecciona las fechas.'); return; }
  if (fechaInicio >= fechaFin)   { alert('La fecha fin debe ser posterior al inicio.'); return; }
  const dias  = Math.ceil((new Date(fechaFin) - new Date(fechaInicio)) / 86400000) + 1;
  const monto = parseFloat(objetoData.precio) * dias;
  const body  = { idObjeto: objetoData.idObjeto, idUsArrendador: objetoData.idUsArrendador, idUsArrendatario: session.idUsuario, fechaInicio, fechaFin, estado: 'PENDIENTE', monto };
  this.disabled = true; this.textContent = 'Enviando...';
  try {
    const res = await fetch(`${API}/api/solicitudes`, { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body) });
    if (res.ok) { alert(`¡Solicitud enviada!\nMonto estimado: $${monto.toFixed(2)} por ${dias} día(s).`); window.location.href = 'solicitudes.html'; }
    else { const err = await res.json().catch(() => ({})); alert(err.error || 'No se pudo enviar la solicitud.'); }
  } catch (e) { alert('No se pudo conectar con el servidor.'); }
  finally { this.disabled = false; this.textContent = 'Enviar Solicitud'; }
});

cargarDetalle();