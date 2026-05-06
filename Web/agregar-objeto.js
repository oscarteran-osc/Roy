document.getElementById('fileInput').addEventListener('change', function() {
  const file = this.files[0];
  if (!file) return;
  const reader  = new FileReader();
  reader.onload = e => {
    const preview     = document.getElementById('preview-img');
    const placeholder = document.getElementById('upload-placeholder');
    preview.src            = e.target.result;
    preview.style.display  = 'block';
    placeholder.style.display = 'none';
  };
  reader.readAsDataURL(file);
});

const zone = document.getElementById('dropZone');
zone.addEventListener('dragover', e => { e.preventDefault(); zone.style.borderColor = 'var(--azul)'; });
zone.addEventListener('dragleave', () => zone.style.borderColor = '#C0D0E8');
zone.addEventListener('drop', e => {
  e.preventDefault();
  zone.style.borderColor = '#C0D0E8';
  const file = e.dataTransfer.files[0];
  if (file && file.type.startsWith('image/')) {
    document.getElementById('fileInput').files = e.dataTransfer.files;
    document.getElementById('fileInput').dispatchEvent(new Event('change'));
  }
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
    imagenUrl:      null
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
