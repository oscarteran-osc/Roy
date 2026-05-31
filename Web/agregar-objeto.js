let slotActual = 0;
const urls = [null, null, null];

function abrirPanelUrl(slot) {
  slotActual = slot;
  const panel = document.getElementById('panel-url');
  document.getElementById('url-input').value = urls[slot] || '';
  panel.style.display = 'flex';
  setTimeout(() => document.getElementById('url-input').focus(), 50);
}

function cerrarPanelUrl() {
  document.getElementById('panel-url').style.display = 'none';
}

function confirmarUrl() {
  const url = document.getElementById('url-input').value.trim();
  urls[slotActual] = url || null;

  const preview     = document.getElementById(`preview-img-${slotActual}`);
  const placeholder = document.getElementById(`placeholder-${slotActual}`);

  if (url) {
    preview.src           = url;
    preview.style.display = 'block';
    placeholder.style.display = 'none';
    preview.onerror = () => {
      preview.style.display     = 'none';
      placeholder.style.display = slotActual === 0 ? 'flex' : 'inline';
      urls[slotActual] = null;
    };
  } else {
    preview.style.display     = 'none';
    placeholder.style.display = slotActual === 0 ? 'flex' : 'inline';
  }

  cerrarPanelUrl();
}

document.getElementById('panel-url').addEventListener('click', function(e) {
  if (e.target === this) cerrarPanelUrl();
});

document.getElementById('url-input').addEventListener('keydown', function(e) {
  if (e.key === 'Enter') confirmarUrl();
  if (e.key === 'Escape') cerrarPanelUrl();
});

async function agregarObjeto() {
  const nombre = document.getElementById('ag-nombre').value.trim();
  const precio = document.getElementById('ag-precio').value.trim();
  const cat    = document.getElementById('ag-cat').value;
  const desc   = document.getElementById('ag-desc').value.trim();

  if (!nombre || !precio || !cat) {
    alert('Por favor llena los campos obligatorios.');
    return;
  }

  const session = getSession();
  if (!session) { window.location.href = 'login.html'; return; }

  const body = {
    idUsArrendador: session.idUsuario,
    nombreObjeto:   nombre,
    precio:         parseFloat(precio),
    categoria:      cat,
    descripcion:    desc,
    estado:         'disponible',
    imagenUrl:      urls[0] || null,
    imagenUrl2:     urls[1] || null,
    imagenUrl3:     urls[2] || null
  };

  try {
    const res = await fetch(`${API}/api/objeto/objeto`, {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify(body)
    });

    if (res.ok) {
      window.location.href = 'mis-objetos.html';
    } else {
      alert('No se pudo agregar el objeto. Inténtalo de nuevo.');
    }
  } catch (e) {
    alert('No se pudo conectar con el servidor.');
  }
}

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