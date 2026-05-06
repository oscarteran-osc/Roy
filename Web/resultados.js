const params    = new URLSearchParams(window.location.search);
const termino   = params.get('q')   || '';
const categoria = params.get('cat') || '';

const catNombres = {
  todas: 'Todas', eventos: 'Eventos', tecnologia: 'Tecnología',
  transporte: 'Transporte', herramientas: 'Herramientas',
  deporte: 'Deporte', instrumentos: 'Instrumentos musicales'
};

const label   = document.getElementById('termino-display');
const msgText = document.getElementById('termino-msg');

if (termino) {
  label.textContent   = `"${termino}"`;
  msgText.textContent = termino;
} else if (categoria) {
  const nombreCat     = catNombres[categoria] || categoria;
  label.textContent   = nombreCat;
  msgText.textContent = nombreCat;
}

if (categoria) {
  document.querySelectorAll('.cat-nav-btn').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.cat === categoria);
  });
}
if (termino) document.getElementById('searchInput').value = termino;

document.getElementById('searchInput').addEventListener('keydown', function(e) {
  if (e.key === 'Enter' && this.value.trim() !== '') {
    window.location.href = 'resultados.html?q=' + encodeURIComponent(this.value.trim());
  }
});

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

const placeholder = 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=300&q=80';

function renderResultados(objetos) {
  const main = document.querySelector('.dash-main');

  const sinResultados = document.querySelector('.sin-resultados');
  if (!objetos || objetos.length === 0) {
    if (sinResultados) sinResultados.style.display = 'block';
    return;
  }
  if (sinResultados) sinResultados.style.display = 'none';

  let grid = document.getElementById('resultados-grid');
  if (!grid) {
    grid = document.createElement('div');
    grid.id        = 'resultados-grid';
    grid.className = 'cerca-scroll';
    main.appendChild(grid);
  }

  grid.innerHTML = objetos.map(obj => `
    <div class="cerca-card">
      <div class="cerca-img">
        <img src="${obj.imagenUrl || placeholder}"
             alt="${obj.nombreObjeto}"
             onerror="this.src='${placeholder}'"/>
      </div>
      <div class="cerca-info">
        <span class="cerca-cat">${obj.categoria || 'General'}</span>
        <h4>${obj.nombreObjeto}</h4>
        <p class="cerca-zona">📍 ${obj.zona || 'Sin zona'}</p>
        <div class="cerca-footer">
          <span class="cerca-precio">$${obj.precio}<small>/día</small></span>
          <a href="detalle-objeto.html?id=${obj.idObjeto}" class="obj-btn">Ver más</a>
        </div>
      </div>
    </div>
  `).join('');
}

async function buscar() {
  try {
    let url;
    if (termino) {
      url = `${API}/api/objeto/buscar?q=${encodeURIComponent(termino)}`;
    } else if (categoria) {
      url = `${API}/api/objeto/categoria?nombre=${encodeURIComponent(catNombres[categoria] || categoria)}`;
    } else {
      url = `${API}/api/objeto/objeto`;
    }

    const res = await fetch(url);
    if (!res.ok) return;
    const data = await res.json();
    renderResultados(data);
  } catch (e) {
    console.warn('No se pudo conectar con la API.');
  }
}

buscar();
