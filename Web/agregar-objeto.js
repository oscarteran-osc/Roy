// ── Preview de imagen ──
document.getElementById('fileInput').addEventListener('change', function() {
  const file = this.files[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = e => {
    const preview = document.getElementById('preview-img');
    const placeholder = document.getElementById('upload-placeholder');
    preview.src = e.target.result;
    preview.style.display = 'block';
    placeholder.style.display = 'none';
  };
  reader.readAsDataURL(file);
});

// ── Drag & drop ──
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

// ── Agregar objeto (simula guardar) ──
function agregarObjeto() {
  const nombre = document.getElementById('ag-nombre').value.trim();
  const precio = document.getElementById('ag-precio').value.trim();
  const cat    = document.getElementById('ag-cat').value;

  if (!nombre || !precio || !cat) {
    alert('Por favor llena los campos obligatorios.');
    return;
  }
  // TODO: conectar a POST /api/objeto cuando esté el backend
  window.location.href = 'mis-objetos.html';
}

// ── Buscador ──
document.getElementById('searchInput').addEventListener('keydown', function(e) {
  if (e.key === 'Enter' && this.value.trim() !== '') {
    window.location.href = 'resultados.html?q=' + encodeURIComponent(this.value.trim());
  }
});

// ── Barra naranja ──
document.querySelectorAll('.cat-nav-btn').forEach(btn => {
  btn.addEventListener('click', function() {
    const cat = this.dataset.cat;
    window.location.href = cat === 'todas' ? 'dashboard.html' : 'resultados.html?cat=' + encodeURIComponent(cat);
  });
}); 