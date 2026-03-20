// ═══════════════════════════════════════
//  DASHBOARD.JS
// ═══════════════════════════════════════

// ── Búsqueda: Enter manda a resultados.html ──
document.getElementById('searchInput').addEventListener('keydown', function(e) {
  if (e.key === 'Enter' && this.value.trim() !== '') {
    window.location.href = 'resultados.html?q=' + encodeURIComponent(this.value.trim());
  }
});

// ── Filtro barra naranja → manda a resultados por categoría ──
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


// ═══════════════════════════════════════
//  TODO: CONECTAR A LA API
//  Cuando Spring Boot esté listo, descomenta
//  este bloque y borra las tarjetas estáticas
//  del HTML dentro de #cerca-grid
// ═══════════════════════════════════════

// const API = 'http://localhost:8080';

// async function cargarCerca() {
//   try {
//     const res  = await fetch(`${API}/api/objeto/objeto`);
//     const data = await res.json();
//     renderCerca(data);
//   } catch (e) {
//     console.warn('API no disponible, mostrando datos estáticos.');
//   }
// }

// function renderCerca(objetos) {
//   const grid = document.getElementById('cerca-grid');
//   if (!objetos || objetos.length === 0) return;
//   grid.innerHTML = objetos.map(obj => `
//     <div class="cerca-card">
//       <div class="cerca-img">
//         <img src="${obj.imagenUrl || 'img/placeholder.png'}"
//              alt="${obj.nombreObjeto}"
//              onerror="this.src='img/placeholder.png'"/>
//       </div>
//       <div class="cerca-info">
//         <span class="cerca-cat">${obj.categoria || 'General'}</span>
//         <h4>${obj.nombreObjeto}</h4>
//         <p class="cerca-zona">📍 ${obj.zona || 'Sin zona'}</p>
//         <div class="cerca-footer">
//           <span class="cerca-precio">$${obj.precio}<small>/día</small></span>
//           <a href="detalle-objeto.html?id=${obj.idObjeto}" class="obj-btn">Ver más</a>
//         </div>
//       </div>
//     </div>
//   `).join('');
// }

// cargarCerca();