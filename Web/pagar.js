const idSolicitud = new URLSearchParams(window.location.search).get('id');

async function cargarSolicitud() {
  if (!idSolicitud) return;
  try {
    const res = await fetch(`${API}/api/solicitudes/${idSolicitud}`);
    if (!res.ok) return;
    const s = await res.json();

    document.getElementById('pagar-total').textContent = s.monto ? `$${s.monto}` : '$—';

    // Cargar datos del objeto
    if (s.idObjeto) {
      try {
        const objRes = await fetch(`${API}/api/objeto/objeto/${s.idObjeto}`);
        if (objRes.ok) {
          const obj = await objRes.json();
          document.getElementById('pagar-nombre').textContent = obj.nombreObjeto || 'Objeto';
          document.getElementById('pagar-img').src = obj.imagenUrl || '';
          document.getElementById('pagar-ver-detalles').href = `detalle-objeto.html?id=${s.idObjeto}`;
        }
      } catch (e) {}
    }

    // Cargar nombre del arrendador
    if (s.idUsArrendador) {
      try {
        const uRes = await fetch(`${API}/Roy/api/usuario/${s.idUsArrendador}`);
        if (uRes.ok) {
          const u = await uRes.json();
          document.getElementById('pagar-arrendador').textContent = `${u.nombre} ${u.apellido}`;
        }
      } catch (e) {}
    }

    // Cargar domicilio del usuario actual
    const session = getSession();
    if (session) {
      try {
        const uRes = await fetch(`${API}/Roy/api/usuario/${session.idUsuario}`);
        if (uRes.ok) {
          const u = await uRes.json();
          document.getElementById('pagar-domicilio').value = u.direccion || '';
        }
      } catch (e) {}
    }

  } catch (e) {
    console.warn('No se pudo cargar la solicitud.');
  }
}

async function procesarPago() {
  const acepto = document.getElementById('check-acepto').checked;
  if (!acepto) { alert('Debes aceptar los términos y condiciones para continuar.'); return; }

  const btn = document.querySelector('.btn-pagar-final');
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

cargarSolicitud();