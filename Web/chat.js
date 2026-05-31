const params       = new URLSearchParams(window.location.search);
const idSolicitud  = parseInt(params.get('idSolicitud'));
const otroUsuario  = parseInt(params.get('otroUsuario'));
const nombreOtro   = params.get('nombre') || 'Usuario';

let miUsuarioId = null;
let intervalId  = null;

// Navegación
document.getElementById('searchInput')?.addEventListener('keydown', e => {
  if (e.key === 'Enter' && e.target.value.trim())
    window.location.href = 'resultados.html?q=' + encodeURIComponent(e.target.value.trim());
});
document.querySelectorAll('.cat-nav-btn').forEach(btn => {
  btn.addEventListener('click', function() {
    const cat = this.dataset.cat;
    window.location.href = cat === 'todas' ? 'dashboard.html' : 'resultados.html?cat=' + encodeURIComponent(cat);
  });
});

function getSession() {
  try { return JSON.parse(localStorage.getItem('roy_session')); } catch { return null; }
}

async function init() {
  const session = getSession();
  if (!session) { window.location.href = 'login.html'; return; }
  miUsuarioId = session.idUsuario;
  document.getElementById('chat-titulo').textContent = `Chat con ${decodeURIComponent(nombreOtro)}`;
  await cargarMensajes();
  // Refrescar mensajes cada 5 segundos
  intervalId = setInterval(cargarMensajes, 5000);
}

async function cargarMensajes() {
  if (!idSolicitud) return;
  try {
    const res = await fetch(`${API}/api/mensajes/solicitud/${idSolicitud}`);
    if (!res.ok) return;
    const mensajes = await res.json();
    renderMensajes(mensajes);
  } catch(e) {}
}

function renderMensajes(mensajes) {
  const container = document.getElementById('chat-mensajes');
  if (!mensajes || mensajes.length === 0) {
    container.innerHTML = '<p class="sin-mensajes">No hay mensajes aún. ¡Sé el primero en escribir!</p>';
    return;
  }

  const wasAtBottom = container.scrollHeight - container.scrollTop <= container.clientHeight + 50;

  container.innerHTML = mensajes.map(m => {
    const esMio = m.idRemitente === miUsuarioId;
    const hora  = m.fechaEnvio ? new Date(m.fechaEnvio).toLocaleTimeString('es-MX', { hour: '2-digit', minute: '2-digit' }) : '';
    return `
      <div class="burbuja ${esMio ? 'mia' : 'otro'}">
        ${escapeHtml(m.contenido)}
        <div class="hora">${hora}</div>
      </div>`;
  }).join('');

  if (wasAtBottom) container.scrollTop = container.scrollHeight;
}

async function enviarMensaje() {
  const input = document.getElementById('inputMensaje');
  const texto = input.value.trim();
  if (!texto || !miUsuarioId || !otroUsuario) return;

  input.value = '';

  try {
    const res = await fetch(`${API}/api/mensajes/enviar/${miUsuarioId}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        idDestinatario: otroUsuario,
        idSolicitud: idSolicitud || null,
        contenido: texto
      })
    });
    if (res.ok) await cargarMensajes();
  } catch(e) {}
}

// Enviar con Enter
document.getElementById('inputMensaje')?.addEventListener('keydown', e => {
  if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); enviarMensaje(); }
});

function escapeHtml(text) {
  const div = document.createElement('div');
  div.appendChild(document.createTextNode(text));
  return div.innerHTML;
}

// Limpiar intervalo al salir
window.addEventListener('beforeunload', () => { if (intervalId) clearInterval(intervalId); });

init();

