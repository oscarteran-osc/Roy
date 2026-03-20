// ── Cambiar panel info/historial ──
function cambiarPanel(panel) {
  document.getElementById('panel-info').style.display     = panel === 'info'     ? 'block' : 'none';
  document.getElementById('panel-historial').style.display = panel === 'historial' ? 'block' : 'none';

  document.querySelectorAll('.cuenta-sidebar-link').forEach(btn => btn.classList.remove('active'));
  event.target.classList.add('active');
}

// ── Modal editar perfil ──
function abrirModalEditar() {
  document.getElementById('modal-editar').style.display = 'flex';
}
function cerrarModalEditar() {
  document.getElementById('modal-editar').style.display = 'none';
}
function guardarPerfil() {
  // TODO: conectar a PUT /Roy/api/usuario/{id}
  cerrarModalEditar();
}

// ── Cerrar modal al picar fuera ──
document.getElementById('modal-editar').addEventListener('click', function(e) {
  if (e.target === this) cerrarModalEditar();
});

// ── Buscador ──
document.getElementById('searchInput').addEventListener('keydown', function(e) {
  if (e.key === 'Enter' && this.value.trim() !== '')
    window.location.href = 'resultados.html?q=' + encodeURIComponent(this.value.trim());
});

// ── Barra naranja ──
document.querySelectorAll('.cat-nav-btn').forEach(btn => {
  btn.addEventListener('click', function() {
    const cat = this.dataset.cat;
    window.location.href = cat === 'todas' ? 'dashboard.html' : 'resultados.html?cat=' + encodeURIComponent(cat);
  });
});