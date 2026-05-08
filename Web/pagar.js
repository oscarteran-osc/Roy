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

// ✅ PayPal Sandbox integration - redirige a approvalUrl
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
}

async function pagarConPayPal() {
  const acepto = document.getElementById('check-acepto').checked;
  if (!acepto) {
    alert('Debes aceptar los términos y condiciones para continuar.');
    return;
  }

  const btn = document.getElementById('btn-paypal');
  btn.disabled = true;
  btn.textContent = '⏳ Conectando con PayPal...';

  try {
    const res = await fetch(
      `${API}/api/paypal/crear-orden?total=${montoSolicitud}&moneda=USD&descripcion=Pago%20de%20renta%20-%20Solicitud%20%23${idSolicitud}&origen=web`,
      { method: 'POST' }
    );
    if (!res.ok) throw new Error('Error al crear orden');
    const data = await res.json();

    // Guardar idSolicitud en sessionStorage para usarlo al volver
    sessionStorage.setItem('paypal_idSolicitud', idSolicitud);

    // Redirigir a PayPal Sandbox
    if (data.approvalUrl) {
      window.location.href = data.approvalUrl;
    } else {
      throw new Error('No se recibió URL de aprobación');
    }
  } catch(e) {
    alert('❌ Error al conectar con PayPal: ' + e.message);
    btn.disabled = false;
    btn.textContent = '💳 Pagar con PayPal';
  }
}

// Verificar si regresamos de PayPal con pago aprobado
async function verificarRetornoPayPal() {
  const params = new URLSearchParams(window.location.search);
  const paymentId = params.get('paymentId');
  const payerId = params.get('PayerID');
  const idSol = sessionStorage.getItem('paypal_idSolicitud') || idSolicitud;

  if (paymentId && payerId) {
    const msgEl = document.getElementById('paypal-msg');
    if (msgEl) msgEl.textContent = '⏳ Confirmando pago...';

    try {
      const res = await fetch(`${API}/api/paypal/capturar-pago`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ paymentId, payerId, idSolicitud: parseInt(idSol) })
      });

      if (res.ok) {
        sessionStorage.removeItem('paypal_idSolicitud');
        if (msgEl) msgEl.textContent = '✅ ¡Pago exitoso!';
        setTimeout(() => window.location.href = 'solicitudes.html', 1500);
      } else {
        if (msgEl) msgEl.textContent = '❌ Error al confirmar el pago.';
      }
    } catch(e) {
      if (document.getElementById('paypal-msg'))
        document.getElementById('paypal-msg').textContent = '❌ Error de conexión.';
    }
  }
}

cargarDatosSolicitud();
verificarRetornoPayPal();


