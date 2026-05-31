// ===== CHATBOT ROY con Claude AI =====

const SYSTEM_PROMPT = `Eres ROY Assistant, el asistente virtual de la plataforma ROY (Renta de Objetos y más).
ROY es una app donde los usuarios pueden rentar objetos entre sí, como herramientas, tecnología, artículos para eventos, etc.

Responde SIEMPRE en español, de forma amable, breve y útil. Solo responde preguntas relacionadas con ROY.

Información sobre ROY:
- Para rentar un objeto: busca en el feed o por categorías, entra al detalle y toca "Enviar Solicitud". Elige las fechas y confirma.
- Para publicar un objeto: ve a "Mis Objetos" → botón "+" → llena nombre, precio por día, categoría, descripción y ciudad.
- El pago se realiza con PayPal Sandbox desde la sección de Solicitudes → botón "Pagar".
- Las solicitudes tienen estados: PENDIENTE → APROBADA → PAGADA.
- El arrendador puede aprobar o rechazar solicitudes desde su sección de Solicitudes.
- Puedes chatear con el arrendador/arrendatario desde el botón 💬 en cada solicitud.
- Las calificaciones son de 1 a 5 estrellas y el promedio se muestra en el detalle del objeto.
- El historial de objetos rentados aparece en tu perfil.
- La sección "Cerca de ti" muestra objetos de tu misma ciudad o estado.
- Si tienes problemas técnicos, intenta cerrar sesión y volver a entrar.

Si alguien pregunta algo que no tiene que ver con ROY, responde amablemente que solo puedes ayudar con temas de la plataforma.`;

let chatbotHistorial = [];
let chatbotAbierto = false;

function crearChatbotUI() {
  // Botón flotante
  const fab = document.createElement('button');
  fab.id = 'chatbot-fab';
  fab.title = 'Asistente ROY';
  fab.innerHTML = '🤖';
  fab.onclick = toggleChatbot;
  document.body.appendChild(fab);

  // Ventana del chat
  const win = document.createElement('div');
  win.id = 'chatbot-window';
  win.classList.add('oculto');
  win.innerHTML = `
    <div id="chatbot-header">
      <div><span>🤖</span> ROYer</div>
      <button id="chatbot-close" onclick="toggleChatbot()">✕</button>
    </div>
    <div id="chatbot-mensajes">
      <div class="cb-msg bot">¡Hola! Soy ROYer, tu asistente de ROY. ¿En qué puedo ayudarte? 😊</div>
    </div>
    <div id="chatbot-input">
      <input type="text" id="chatbot-texto" placeholder="Escribe tu pregunta..." maxlength="300"/>
      <button onclick="enviarChatbot()">➤</button>
    </div>
  `;
  document.body.appendChild(win);

  // Enter para enviar
  setTimeout(() => {
    document.getElementById('chatbot-texto')?.addEventListener('keydown', e => {
      if (e.key === 'Enter') enviarChatbot();
    });
  }, 100);
}

function toggleChatbot() {
  chatbotAbierto = !chatbotAbierto;
  const win = document.getElementById('chatbot-window');
  const fab = document.getElementById('chatbot-fab');
  if (chatbotAbierto) {
    win.classList.remove('oculto');
    fab.innerHTML = '✕';
    document.getElementById('chatbot-texto')?.focus();
  } else {
    win.classList.add('oculto');
    fab.innerHTML = '🤖';
  }
}

async function enviarChatbot() {
  const input = document.getElementById('chatbot-texto');
  const texto = input.value.trim();
  if (!texto) return;
  input.value = '';

  agregarMensajeChatbot(texto, 'user');
  chatbotHistorial.push({ role: 'user', content: texto });

  const typing = agregarMensajeChatbot('Escribiendo...', 'bot typing');

  try {
    const res = await fetch(`${API}/api/chatbot/mensaje`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        system: SYSTEM_PROMPT,
        messages: chatbotHistorial
      })
    });

    typing.remove();

    if (!res.ok) {
      agregarMensajeChatbot('Lo siento, tuve un problema al conectarme. Intenta de nuevo.', 'bot');
      return;
    }

    const data = await res.json();
    const respuesta = data.respuesta || 'No pude generar una respuesta.';
    chatbotHistorial.push({ role: 'assistant', content: respuesta });
    agregarMensajeChatbot(respuesta, 'bot');

    // Limitar historial a últimos 10 mensajes
    if (chatbotHistorial.length > 10) chatbotHistorial = chatbotHistorial.slice(-10);

  } catch(e) {
    typing.remove();
    agregarMensajeChatbot('Error de conexión. Verifica tu internet e intenta de nuevo.', 'bot');
  }
}

function agregarMensajeChatbot(texto, tipo) {
  const container = document.getElementById('chatbot-mensajes');
  const div = document.createElement('div');
  div.className = `cb-msg ${tipo}`;
  div.textContent = texto;
  container.appendChild(div);
  container.scrollTop = container.scrollHeight;
  return div;
}

// Inicializar cuando cargue la página
document.addEventListener('DOMContentLoaded', crearChatbotUI);
