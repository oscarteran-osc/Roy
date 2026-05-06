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

async function cargarObjetos() {
  try {
    const res = await fetch(`${API}/api/objeto/objeto`);
    if (!res.ok) return;
    const data = await res.json();
    if (data && data.length > 0) renderObjetos(data);
  } catch (e) {
    console.warn('API no disponible, mostrando datos estáticos.');
  }
}

function renderObjetos(objetos) {
  const grid = document.getElementById('cerca-grid');
  const placeholder = 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=300&q=80';
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

cargarObjetos();
