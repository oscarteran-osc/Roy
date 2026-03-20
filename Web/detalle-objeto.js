// ═══════════════════════════════════════
//  DETALLE-OBJETO.JS
// ═══════════════════════════════════════

const params  = new URLSearchParams(window.location.search);
const nombre  = params.get('nombre') || 'Objeto';
const precio  = params.get('precio') || '0';
const cat     = params.get('cat')    || 'General';
const zona    = params.get('zona')   || 'Sin zona';
const img     = params.get('img')    || 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600&q=80';
const desc    = params.get('desc')   || 'Sin descripción disponible.';

// Nombre bonito para la categoría
const catNombres = {
  eventos: 'Eventos', tecnologia: 'Tecnología', transporte: 'Transporte',
  herramientas: 'Herramientas', deporte: 'Deporte', instrumentos: 'Instrumentos musicales'
};
const catNombre = catNombres[cat] || cat;

// ── Pintar datos en la página ──
document.getElementById('detalle-nombre').textContent = nombre;
document.getElementById('detalle-precio').textContent = '$' + precio;
document.getElementById('detalle-zona').textContent   = '📍 ' + zona;
document.getElementById('detalle-cat').textContent    = catNombre;
document.getElementById('detalle-vendedor-link').textContent = 'José Alberto';

// Descripción — dividir por puntos para crear la lista
const descItems = desc.split('. ').filter(d => d.trim());
const lista = document.getElementById('detalle-desc-list');
lista.innerHTML = descItems.map(d => `<li>${d.trim().replace(/\.$/, '')}.</li>`).join('');

// Imagen principal y thumbnails
const imgPrincipal = document.getElementById('img-principal');
imgPrincipal.src = img;

// Generar thumbnails con la misma imagen en diferentes tamaños
const thumbs = document.querySelectorAll('.thumb');
thumbs.forEach(t => {
  t.src = img.replace('w=600', 'w=120');
});

// Breadcrumb
document.getElementById('breadcrumb-cat').textContent = catNombre;
document.getElementById('breadcrumb-cat').href        = 'resultados.html?cat=' + cat;
document.getElementById('breadcrumb-nombre').textContent = nombre;

// Título de la página
document.title = `ROY – ${nombre}`;

// ── Cambiar imagen principal al picar thumbnail ──
function cambiarImg(thumb) {
  document.querySelectorAll('.thumb').forEach(t => t.classList.remove('active'));
  thumb.classList.add('active');
  const imgGrande = document.getElementById('img-principal');
  imgGrande.style.opacity = '0';
  setTimeout(() => {
    imgGrande.src = thumb.src.replace('w=120', 'w=600');
    imgGrande.style.opacity = '1';
  }, 150);
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
    const c = this.dataset.cat;
    window.location.href = c === 'todas' ? 'dashboard.html' : 'resultados.html?cat=' + encodeURIComponent(c);
  });
});

// ── Botón solicitud ──
document.querySelector('.btn-solicitud').addEventListener('click', function() {
  // TODO: conectar a POST /api/solicitudes
  alert(`¡Solicitud enviada para "${nombre}"!\nEl arrendador te contactará pronto.`);
});