// ── Seleccionar método de pago ──
function seleccionarMetodo(metodo, label) {
  // Quitar active de todos los radios
  document.querySelectorAll('.metodo-radio').forEach(r => r.classList.remove('active'));
  document.getElementById('radio-' + metodo).classList.add('active');

  // Mostrar/ocultar form de tarjeta
  document.getElementById('tarjeta-form').style.display =
    metodo === 'tarjeta' ? 'block' : 'none';
}

// ── Formatear número de tarjeta con espacios ──
document.getElementById('num-tarjeta').addEventListener('input', function() {
  let val = this.value.replace(/\D/g, '').substring(0, 16);
  this.value = val.replace(/(.{4})/g, '$1 ').trim();
});

// ── Procesar pago ──
function procesarPago() {
  const acepto = document.getElementById('check-acepto').checked;
  if (!acepto) {
    alert('Debes aceptar los términos y condiciones para continuar.');
    return;
  }
  // TODO: conectar a POST /api/paypal/crear-orden o /api/transacciones
  alert('¡Pago procesado con éxito! Tu renta ha sido confirmada.');
  window.location.href = 'solicitudes.html';
}

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