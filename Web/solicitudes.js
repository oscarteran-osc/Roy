function cambiarTab(tab) {
  document.getElementById('panel-rentador').style.display  = tab === 'rentador'  ? 'block' : 'none';
  document.getElementById('panel-ofertante').style.display = tab === 'ofertante' ? 'block' : 'none';
  document.getElementById('btn-rentador').classList.toggle('active',  tab === 'rentador');
  document.getElementById('btn-ofertante').classList.toggle('active', tab === 'ofertante');
}

document.getElementById('searchInput').addEventListener('keydown', function(e) {
  if (e.key === 'Enter' && this.value.trim() !== '')
    window.location.href = 'resultados.html?q=' + encodeURIComponent(this.value.trim());
});

document.querySelectorAll('.cat-nav-btn').forEach(btn => {
  btn.addEventListener('click', function() {
    const cat = this.dataset.cat;
    window.location.href = cat === 'todas' ? 'dashboard.html' : 'resultados.html?cat=' + encodeURIComponent(cat);
  });
});