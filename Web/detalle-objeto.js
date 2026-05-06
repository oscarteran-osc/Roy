const params     = new URLSearchParams(window.location.search);
const idObjeto   = params.get('id');
const placeholder = 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600&q=80';

const catNombres = {
  eventos: 'Eventos', tecnologia: 'Tecnología', transporte: 'Transporte',
  herramientas: 'Herramientas', deporte: 'Deporte', instrumentos: 'Instrumentos musicales'
};

let objetoData = null;

async function cargarDetalle() {
  if (idObjeto) {
    try {
      const res = await fetch(`${API}/api/objeto/objeto/${idObjeto}`);
      if (res.ok) {
        objetoData = await res.json();
        pintarDetalle(objetoData);
        return;
      }
    } catch (e) {
      console.warn('Error cargando objeto por ID, usando parámetros de URL.');
    }
  }

  // Fallback: parámetros en la URL (compatibilidad con tarjetas estáticas)
  objetoData = {
    nombreObjeto: params.get('nombre') || 'Objeto',
    precio:       params.get('precio') || '0',
    categoria:    params.get('cat')    || 'General',
    zona:         params.get('zona')   || 'Sin zona',
    imagenUrl:    params.get('img')    || placeholder,
    descripcion:  params.get('desc')   || 'Sin descripción disponible.',
    nomArrendador: 'Arrendador'
  };
  pintarDetalle(objetoData);
}

function pintarDetalle(obj) {
  const catNombre = catNombres[obj.categoria] || obj.categoria || 'General';

  document.getElementById('detalle-nombre').textContent         = obj.nombreObjeto;
  document.getElementById('detalle-precio').textContent         = '$' + obj.precio;
  document.getElementById('detalle-zona').textContent           = '📍 ' + (obj.zona || 'Sin zona');
  document.getElementById('detalle-cat').textContent            = catNombre;
  document.getElementById('detalle-vendedor-link').textContent  = obj.nomArrendador || 'Arrendador';

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

function cambiarImg(thumb) {
  document.querySelectorAll('.thumb').forEach(t => t.classList.remove('active'));
  thumb.classList.add('active');
  const imgGrande         = document.getElementById('img-principal');
  imgGrande.style.opacity = '0';
  setTimeout(() => {
    imgGrande.src           = thumb.src;
    imgGrande.style.opacity = '1';
  }, 150);
}

document.getElementById('searchInput').addEventListener('keydown', function(e) {
  if (e.key === 'Enter' && this.value.trim() !== '') {
    window.location.href = 'resultados.html?q=' + encodeURIComponent(this.value.trim());
  }
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
  if (!objetoData || !objetoData.idObjeto) {
    alert('No se pudo identificar el objeto. Vuelve al inicio e inténtalo de nuevo.');
    return;
  }

  const fechaInicio = document.getElementById('fecha-inicio').value;
  const fechaFin    = document.getElementById('fecha-fin').value;

  if (!fechaInicio || !fechaFin) {
    alert('Por favor selecciona las fechas de inicio y fin.');
    return;
  }
  if (fechaInicio >= fechaFin) {
    alert('La fecha de fin debe ser posterior a la de inicio.');
    return;
  }

  const dias   = Math.ceil((new Date(fechaFin) - new Date(fechaInicio)) / (1000 * 60 * 60 * 24)) + 1;
  const monto  = parseFloat(objetoData.precio) * dias;

  const body = {
    idObjeto:          objetoData.idObjeto,
    idUsArrendador:    objetoData.idUsArrendador,
    idUsArrendatario:  session.idUsuario,
    fechaInicio,
    fechaFin,
    estado:            'pendiente',
    monto
  };

  this.disabled    = true;
  this.textContent = 'Enviando...';

  try {
    const res = await fetch(`${API}/api/solicitudes`, {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify(body)
    });

    if (res.ok) {
      alert(`¡Solicitud enviada! El arrendador te contactará pronto.\nMonto estimado: $${monto.toFixed(2)} por ${dias} día(s).`);
      window.location.href = 'solicitudes.html';
    } else {
      const err = await res.json().catch(() => ({}));
      alert(err.error || 'No se pudo enviar la solicitud. Intenta de nuevo.');
    }
  } catch (e) {
    alert('No se pudo conectar con el servidor.');
  } finally {
    this.disabled    = false;
    this.textContent = 'Enviar Solicitud';
  }
});

cargarDetalle();
