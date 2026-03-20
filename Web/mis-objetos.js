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

// ── Abrir modal detalles ──
function abrirDetalles(nombre, precio, cat, zona, img, desc) {
  document.getElementById('modal-nombre').value  = nombre;
  document.getElementById('modal-precio').value  = precio;
  document.getElementById('modal-desc').value    = desc;

  // Seleccionar la categoría correcta
  const catNombres = { herramientas: 'Herramientas', tecnologia: 'Tecnología', transporte: 'Transporte', eventos: 'Eventos', deporte: 'Deporte' };
  const select = document.getElementById('modal-cat');
  for (let opt of select.options) {
    if (opt.text === catNombres[cat]) { opt.selected = true; break; }
  }

  // Imágenes
  document.getElementById('modal-img-principal').src = img;
  document.getElementById('modal-thumb-1').src = img.replace('w=600', 'w=80');
  document.getElementById('modal-thumb-2').src = img.replace('w=600', 'w=80');

  document.getElementById('modal-detalles').style.display = 'flex';
}

// ── Abrir modal solicitudes ──
function abrirSolicitudes(nombreObjeto) {
  document.getElementById('modal-solicitudes').style.display = 'flex';
}

// ── Cerrar modal ──
function cerrarModal(id) {
  document.getElementById(id).style.display = 'none';
}

// ── Cerrar al picar fuera ──
document.querySelectorAll('.modal-overlay').forEach(overlay => {
  overlay.addEventListener('click', function(e) {
    if (e.target === this) cerrarModal(this.id);
  });
});

// ── Cambiar imagen en modal ──
function cambiarModalImg(thumb) {
  document.querySelectorAll('.modal-thumb').forEach(t => t.classList.remove('active'));
  thumb.classList.add('active');
  const principal = document.getElementById('modal-img-principal');
  principal.style.opacity = '0';
  setTimeout(() => {
    principal.src = thumb.src.replace('w=80', 'w=600');
    principal.style.opacity = '1';
  }, 150);
}

// ── Eliminar objeto (visual) ──
function eliminarObjeto(btn) {
  if (confirm('¿Estás seguro de que quieres eliminar este objeto?')) {
    btn.closest('.miobjeto-row').style.opacity = '0';
    setTimeout(() => btn.closest('.miobjeto-row').remove(), 300);
  }
}