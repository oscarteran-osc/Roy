function seleccionarMetodo(metodo) {
  document.querySelectorAll('.metodo-radio').forEach(r => r.classList.remove('active'));
  document.getElementById('radio-' + metodo).classList.add('active');
  document.getElementById('tarjeta-form').style.display = metodo === 'tarjeta' ? 'block' : 'none';
}

document.getElementById('num-tarjeta').addEventListener('input', function() {
  let val = this.value.replace(/\D/g, '').substring(0, 16);
  this.value = val.replace(/(.{4})/g, '$1 ').trim();
});

async function procesarPago() {
  const acepto = document.getElementById('check-acepto').checked;
  if (!acepto) {
    alert('Debes aceptar los términos y condiciones para continuar.');
    return;
  }

  const params      = new URLSearchParams(window.location.search);
  const idSolicitud = params.get('id');
  const btn         = document.querySelector('.btn-pagar-final');

  btn.disabled    = true;
  btn.textContent = 'Procesando...';

  try {
    if (idSolicitud) {
      const res = await fetch(`${API}/api/solicitudes/${idSolicitud}/completar`, { method: 'PUT' });
      if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        alert(err.error || 'No se pudo procesar el pago.');
        return;
      }
    }
    alert('¡Pago procesado con éxito! Tu renta ha sido confirmada.');
    window.location.href = 'solicitudes.html';
  } catch (e) {
    alert('No se pudo conectar con el servidor.');
  } finally {
    btn.disabled    = false;
    btn.textContent = 'Pagar';
  }
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
