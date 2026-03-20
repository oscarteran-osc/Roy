// ── Leer parámetros de la URL ──
const params   = new URLSearchParams(window.location.search);
const termino  = params.get('q') || '';
const categoria = params.get('cat') || '';

// Nombres bonitos para las categorías
const catNombres = {
  todas:        'Todas',
  eventos:      'Eventos',
  tecnologia:   'Tecnología',
  transporte:   'Transporte',
  herramientas: 'Herramientas',
  deporte:      'Deporte',
  instrumentos: 'Instrumentos musicales'
};

// Mostrar en pantalla según qué parámetro llegó
const label   = document.getElementById('termino-display');
const msgText = document.getElementById('termino-msg');

if (termino) {
  label.textContent   = `"${termino}"`;
  msgText.textContent = termino;
} else if (categoria) {
  const nombreCat = catNombres[categoria] || categoria;
  label.textContent   = nombreCat;
  msgText.textContent = nombreCat;
}

// Marcar activo el botón de categoría en la barra naranja
if (categoria) {
  document.querySelectorAll('.cat-nav-btn').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.cat === categoria);
  });
}

// Poner el término en el buscador si vino por búsqueda
if (termino) document.getElementById('searchInput').value = termino;

// ── Buscar desde esta página también manda a resultados ──
document.getElementById('searchInput').addEventListener('keydown', function(e) {
  if (e.key === 'Enter' && this.value.trim() !== '') {
    window.location.href = 'resultados.html?q=' + encodeURIComponent(this.value.trim());
  }
});

// ── Barra naranja también funciona desde resultados ──
document.querySelectorAll('.cat-nav-btn').forEach(btn => {
  btn.addEventListener('click', function() {
    const cat = this.dataset.cat;
    if (cat === 'todas') {
      window.location.href = 'dashboard.html';
    } else {
      window.location.href = 'resultados.html?cat=' + encodeURIComponent(cat);
    }
  });
});