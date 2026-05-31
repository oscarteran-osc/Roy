const placeholder = 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=80&q=80';

document.getElementById('searchInput')?.addEventListener('keydown', e => {
  if (e.key === 'Enter' && e.target.value.trim())
    window.location.href = 'resultados.html?q=' + encodeURIComponent(e.target.value.trim());
});

async function cargarChats() {
  const session = getSession();
  if (!session) { window.location.href = 'login.html'; return; }

  const lista = document.getElementById('chats-lista');
  const userId = session.idUsuario;

  try {
    const [resRentador, resOfertante] = await Promise.all([
      fetch(`${API}/api/solicitudes/arrendatario/${userId}`),
      fetch(`${API}/api/solicitudes/arrendador/${userId}`)
    ]);

    const comRentador  = resRentador.ok  ? await resRentador.json()  : [];
    const comOfertante = resOfertante.ok ? await resOfertante.json() : [];

    // Combinar y etiquetar rol
    const todas = [
      ...comRentador.map(s => ({ ...s, miRol: 'rentador' })),
      ...comOfertante.map(s => ({ ...s, miRol: 'ofertante' }))
    ];

    if (todas.length === 0) {
      lista.innerHTML = '<p style="color:#888;text-align:center;padding:40px;">No tienes chats activos aún.</p>';
      return;
    }

    lista.innerHTML = todas.map(s => {
      const otroUsuario  = s.miRol === 'rentador' ? s.idUsArrendador   : s.idUsArrendatario;
      const otroNombre   = s.miRol === 'rentador' ? (s.nombreArrendador || 'Arrendador') : (s.nombreArrendatario || 'Arrendatario');
      const rol          = s.miRol === 'rentador' ? 'Como rentador' : 'Como ofertante';
      const estadoColor  = { pagada: '#134299', aceptada: '#4CAF50', pendiente: '#FF9800', rechazada: '#F44336' };
      const estado       = (s.estado || '').toLowerCase();
      const color        = estadoColor[estado] || '#888';
      const chatUrl      = `chat.html?idSolicitud=${s.idSolicitud}&otroUsuario=${otroUsuario}&nombre=${encodeURIComponent(otroNombre)}`;

      return `
        <div style="background:#fff;border-radius:14px;box-shadow:0 2px 8px rgba(0,0,0,.08);padding:16px 20px;display:flex;align-items:center;gap:16px;">
          <img src="${s.imagenObjeto || placeholder}" alt="${s.nombreObjeto || 'Objeto'}"
               onerror="this.src='${placeholder}'"
               style="width:64px;height:64px;object-fit:cover;border-radius:10px;flex-shrink:0;"/>
          <div style="flex:1;min-width:0;">
            <p style="margin:0 0 2px;font-weight:600;font-family:Poppins,sans-serif;color:#1a1a2e;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">
              ${s.nombreObjeto || 'Objeto'}
            </p>
            <p style="margin:0 0 2px;font-size:13px;color:#666;font-family:Poppins,sans-serif;">
              Con: <strong>${otroNombre}</strong>
            </p>
            <p style="margin:0;font-size:12px;color:#aaa;font-family:Poppins,sans-serif;">
              ${rol} · <span style="color:${color};font-weight:600;">${s.estado || '—'}</span>
            </p>
          </div>
          <a href="${chatUrl}" style="background:#134299;color:#fff;border-radius:10px;padding:10px 18px;font-family:Poppins,sans-serif;font-size:14px;font-weight:600;text-decoration:none;white-space:nowrap;flex-shrink:0;">
            💬 Abrir
          </a>
        </div>`;
    }).join('');

  } catch(e) {
    lista.innerHTML = '<p style="color:#888;text-align:center;padding:40px;">No se pudieron cargar los chats.</p>';
  }
}

cargarChats();
