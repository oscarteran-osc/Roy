function seleccionarMetodo(metodo) {
  document.querySelectorAll('.metodo-radio').forEach(r => r.classList.remove('active'));
  document.getElementById('radio-' + metodo).classList.add('active');
  document.getElementById('tarjeta-form').style.display = metodo === 'tarjeta' ? 'block' : 'none';
}

document.getElementById('num-tarjeta')?.addEventListener('input', function() {
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

// ✅ PayPal Sandbox integration
let montoSolicitud = 0;
const idSolicitud = new URLSearchParams(window.location.search).get('id');

async function cargarDatosSolicitud() {
  if (!idSolicitud) return;
  try {
    const res = await fetch(`${API}/api/solicitudes/${idSolicitud}`);
    if (!res.ok) return;
    const s = await res.json();

    montoSolicitud = s.monto ? parseFloat((s.monto / 17).toFixed(2)) : 0;
    const totalEl = document.getElementById('pagar-total');
    if (totalEl) totalEl.textContent = s.monto ? `$${s.monto} MXN` : '$—';

    if (s.idObjeto) {
      try {
        const objRes = await fetch(`${API}/api/objeto/objeto/${s.idObjeto}`);
        if (objRes.ok) {
          const obj = await objRes.json();
          const nombreEl = document.getElementById('pagar-nombre');
          const imgEl = document.getElementById('pagar-img');
          const verEl = document.getElementById('pagar-ver-detalles');
          if (nombreEl) nombreEl.textContent = obj.nombreObjeto || 'Objeto';
          if (imgEl) imgEl.src = obj.imagenUrl || '';
          if (verEl) verEl.href = `detalle-objeto.html?id=${s.idObjeto}`;
        }
      } catch(e) {}
    }
  } catch(e) {}

  iniciarPayPal();
}

function iniciarPayPal() {
  if (typeof paypal === 'undefined') return;

  paypal.Buttons({
    style: { layout: 'vertical', color: 'blue', shape: 'pill', label: 'pay' },

    createOrder: async function(data, actions) {
      const acepto = document.getElementById('check-acepto').checked;
      if (!acepto) {
        alert('Debes aceptar los términos y condiciones para continuar.');
        return Promise.reject(new Error('Términos no aceptados'));
      }
      const res = await fetch(
        `${API}/api/paypal/crear-orden?total=${montoSolicitud}&moneda=USD&descripcion=Pago%20de%20renta%20-%20Solicitud%20%23${idSolicitud}`,
        { method: 'POST' }
      );
      if (!res.ok) throw new Error('Error al crear orden');
      const data2 = await res.json();
      return data2.orderId || data2.id;
    },

    onApprove: async function(data, actions) {
      const msgEl = document.getElementById('paypal-msg');
      if (msgEl) msgEl.textContent = '⏳ Confirmando pago...';
      try {
        await fetch(`${API}/api/paypal/capturar-pago`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ orderID: data.orderID })
        });
        await fetch(`${API}/api/solicitudes/${idSolicitud}/completar`, { method: 'PUT' });
        if (msgEl) msgEl.textContent = '✅ ¡Pago exitoso!';
        setTimeout(() => window.location.href = 'solicitudes.html', 1500);
      } catch(e) {
        if (msgEl) msgEl.textContent = '❌ Error al confirmar el pago.';
      }
    },

    onCancel: function() {
      const msgEl = document.getElementById('paypal-msg');
      if (msgEl) msgEl.textContent = 'Pago cancelado.';
    },

    onError: function(err) {
      const msgEl = document.getElementById('paypal-msg');
      if (msgEl) msgEl.textContent = '❌ Ocurrió un error con PayPal.';
    }

  }).render('#paypal-button-container');
}

cargarDatosSolicitud();
