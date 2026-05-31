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

    const todas = [
      ...comRentador.map(s => ({ ...s, miRol: 'rentador' })),
      ...comOfertante.map(s => ({ ...s, miRol: 'ofertante' }))
    ];

    if (todas.length === 0) {
      lista.innerHTML = '<p style="color:#888;text-align:center;padding:40px;">No tienes chats activos aún.</p>';
      return;
    }

    // Cargar mensajes de cada solicitud en paralelo
    const conMensajes = await Promise.all(todas.map(async s => {
      try {
        const res = await fetch(`${API}/api/mensajes/solicitud/${s.idSolicitud}`);
        const mensajes = res.ok ? await res.json() : [];
        const noLeidos = mensajes.filter(m => !m.leido && m.idDestinatario === userId).length;
        const ultimo = mensajes.length > 0 ? mensajes[mensajes.length - 1] : null;
        const ultimaFecha = ultimo?.fechaEnvio ? new Date(ultimo.fechaEnvio) : new Date(0);
        return { ...s, noLeidos, ultimaFecha, ultimoMensaje: ultimo?.contenido || null };
      } catch {
        return { ...s, noLeidos: 0, ultimaFecha: new Date(0), ultimoMensaje: null };
      }
    }));

    // Ordenar por más reciente
    conMensajes.sort((a, b) => b.ultimaFecha - a.ultimaFecha);

    const estadoColor = { pagada: '#134299', aceptada: '#4CAF50', pendiente: '#FF9800', rechazada: '#F44336' };

    lista.innerHTML = conMensajes.map(s => {
      const otroUsuario = s.miRol === 'rentador' ? s.idUsArrendador   : s.idUsArrendatario;
      const otroNombre  = s.miRol === 'rentador' ? (s.nombreArrendador || 'Arrendador') : (s.nombreArrendatario || 'Arrendatario');
      const rol         = s.miRol === 'rentador' ? 'Como rentador' : 'Como ofertante';
      const estado      = (s.estado || '').toLowerCase();
      const color       = estadoColor[estado] || '#888';
      const chatUrl     = `chat.html?idSolicitud=${s.idSolicitud}&otroUsuario=${otroUsuario}&nombre=${encodeURIComponent(otroNombre)}`;
      const badgeHtml   = s.noLeidos > 0
        ? `<span style="background:#e53935;color:#fff;border-radius:50%;width:20px;height:20px;display:inline-flex;align-items:center;justify-content:center;font-size:11px;font-weight:700;margin-left:6px;">${s.noLeidos}</span>`
        : '';
      const preview     = s.ultimoMensaje
        ? `<p style="margin:2px 0 0;font-size:12px;color:#aaa;font-family:Poppins,sans-serif;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;max-width:300px;">${s.ultimoMensaje}</p>`
        : '';
      const fechaStr    = s.ultimaFecha.getTime() > 0
        ? s.ultimaFecha.toLocaleDateString('es-MX', { day:'2-digit', month:'short' })
        : '';

      return `
        <div style="background:#fff;border-radius:14px;box-shadow:0 2px 8px rgba(0,0,0,.08);padding:16px 20px;display:flex;align-items:center;gap:16px;">
          <img src="${s.imagenObjeto || placeholder}" alt="${s.nombreObjeto || 'Objeto'}"
               onerror="this.src='${placeholder}'"
               style="width:60px;height:60px;object-fit:cover;border-radius:10px;flex-shrink:0;"/>
          <div style="flex:1;min-width:0;">
            <div style="display:flex;align-items:center;">
              <p style="margin:0;font-weight:600;font-family:Poppins,sans-serif;color:#1a1a2e;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">
                ${s.nombreObjeto || 'Objeto'}
              </p>
              ${badgeHtml}
            </div>
            <p style="margin:2px 0;font-size:13px;color:#666;font-family:Poppins,sans-serif;">
              Con: <strong>${otroNombre}</strong> · <span style="color:${color};font-weight:600;">${s.estado || '—'}</span>
            </p>
            ${preview}
          </div>
          <div style="display:flex;flex-direction:column;align-items:flex-end;gap:8px;flex-shrink:0;">
            ${fechaStr ? `<span style="font-size:11px;color:#bbb;font-family:Poppins,sans-serif;">${fechaStr}</span>` : ''}
            <a href="${chatUrl}" style="background:#134299;color:#fff;border-radius:10px;padding:8px 16px;font-family:Poppins,sans-serif;font-size:13px;font-weight:600;text-decoration:none;">
              💬 Abrir
            </a>
          </div>
        </div>`;
    }).join('');

  } catch(e) {
    lista.innerHTML = '<p style="color:#888;text-align:center;padding:40px;">No se pudieron cargar los chats.</p>';
  }
}

cargarChats();
