'use strict';

/* ════════════════════════════════════════════════════════════════════
   State
════════════════════════════════════════════════════════════════════ */
const state = {
  username: '',
  roomId: '',
  stompClient: null,
  connected: false,
  users: new Set(),
  pollInterval: null,
  typingUsers: new Set(),
  typingTimer: null,
};

/* ════════════════════════════════════════════════════════════════════
   Constants
════════════════════════════════════════════════════════════════════ */
const WS_URL     = 'http://localhost:8080/ws';
const API_BASE_URL = 'http://localhost:8080';

const AVATAR_COLORS = [
  '#e74c3c', '#3498db', '#2ecc71', '#9b59b6',
  '#e67e22', '#1abc9c', '#e91e63', '#0097a7',
  '#f39c12', '#8e24aa',
];

/* ════════════════════════════════════════════════════════════════════
   Helpers
════════════════════════════════════════════════════════════════════ */

/** DOM selector shorthand */
const $ = (id) => document.getElementById(id);

/** Escape HTML to prevent XSS */
function esc(str) {
  return String(str ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

/** Deterministic avatar color from username */
function avatarColor(name) {
  let h = 0;
  for (let i = 0; i < name.length; i++) h = name.charCodeAt(i) + ((h << 5) - h);
  return AVATAR_COLORS[Math.abs(h) % AVATAR_COLORS.length];
}

/** Format a timestamp (ISO string or ms) to HH:MM */
function fmtTime(ts) {
  const d = ts ? new Date(ts) : new Date();
  return d.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' });
}

/** Scroll messages area to the bottom */
function scrollBottom() {
  const c = $('messages-container');
  c.scrollTop = c.scrollHeight;
}

/* ════════════════════════════════════════════════════════════════════
   Screen management
════════════════════════════════════════════════════════════════════ */
function showScreen(screen) {
  const joinEl = $('join-screen');
  const chatEl = $('chat-screen');

  if (screen === 'chat') {
    joinEl.classList.add('d-none');
    chatEl.classList.remove('d-none');
    chatEl.classList.add('d-flex');
  } else {
    chatEl.classList.add('d-none');
    chatEl.classList.remove('d-flex');
    joinEl.classList.remove('d-none');
  }
}

/* ════════════════════════════════════════════════════════════════════
   Connection badge
════════════════════════════════════════════════════════════════════ */
function setConnected(online) {
  state.connected = online;
  const badge = $('connection-badge');
  const input = $('message-input');
  const sendBtn = $('send-btn');

  if (online) {
    badge.classList.remove('offline');
    badge.innerHTML = '<i class="bi bi-circle-fill" style="font-size:.45rem;"></i> <span>Conectado</span>';
    input.disabled = false;
    sendBtn.disabled = false;
    input.focus();
  } else {
    badge.classList.add('offline');
    badge.innerHTML = '<i class="bi bi-circle-fill" style="font-size:.45rem;"></i> <span>Desconectado</span>';
    input.disabled = true;
    sendBtn.disabled = true;
  }
}

/* ════════════════════════════════════════════════════════════════════
   Messages rendering
════════════════════════════════════════════════════════════════════ */
function appendSystem(text) {
  const list = $('messages-list');
  const div = document.createElement('div');
  div.className = 'system-msg';
  div.innerHTML = `<span>${esc(text)}</span>`;
  list.appendChild(div);
  scrollBottom();
}

function appendMessage(msg) {
  const isOwn = msg.sender === state.username;
  const list = $('messages-list');

  const row = document.createElement('div');
  row.className = `msg-row ${isOwn ? 'own' : 'other'}`;

  const senderHtml = !isOwn
    ? `<div class="msg-sender">${esc(msg.sender)}</div>`
    : '';

  row.innerHTML = `
    ${senderHtml}
    <div class="msg-bubble">${esc(msg.content)}</div>
    <div class="msg-time">${fmtTime(msg.timestamp)}</div>
  `;

  list.appendChild(row);
  scrollBottom();
}

/* ════════════════════════════════════════════════════════════════════
   Users list
════════════════════════════════════════════════════════════════════ */
function renderUsers() {
  const list = $('users-list');
  list.innerHTML = '';

  state.users.forEach((name) => {
    const initials = name.slice(0, 2).toUpperCase();
    const color = avatarColor(name);
    const isMe = name === state.username;

    const div = document.createElement('div');
    div.className = 'user-item';
    div.innerHTML = `
      <div class="user-avatar" style="background:${color}">${esc(initials)}</div>
      <span class="user-name">${esc(name)}${isMe ? ' <small class="text-muted">(tú)</small>' : ''}</span>
    `;
    list.appendChild(div);
  });

  $('user-count').textContent = state.users.size;
}

/* ════════════════════════════════════════════════════════════════════
   Typing indicator
════════════════════════════════════════════════════════════════════ */
function renderTyping() {
  const indicator = $('typing-indicator');
  const textEl    = $('typing-text');
  const others = [...state.typingUsers].filter((u) => u !== state.username);

  if (others.length === 0) {
    indicator.classList.add('d-none');
    return;
  }

  indicator.classList.remove('d-none');
  if (others.length === 1) {
    textEl.textContent = `${others[0]} está escribiendo`;
  } else if (others.length === 2) {
    textEl.textContent = `${others[0]} y ${others[1]} están escribiendo`;
  } else {
    textEl.textContent = 'Varios participantes están escribiendo';
  }
}

function showTypingIndicator(sender) {
  state.typingUsers.add(sender);
  renderTyping();
}

function hideTypingIndicator(sender) {
  state.typingUsers.delete(sender);
  renderTyping();
}

/** Publica TYPING al servidor */
function publishTyping() {
  if (!state.connected) return;
  state.stompClient.publish({
    destination: `/app/chat/${state.roomId}/typing`,
    body: JSON.stringify({ type: 'TYPING', sender: state.username }),
  });
}

function publishStopTyping() {
  clearTimeout(state.typingTimer);
  state.typingTimer = null;
  if (!state.connected) return;
  state.stompClient.publish({
    destination: `/app/chat/${state.roomId}/typing`,
    body: JSON.stringify({ type: 'STOP_TYPING', sender: state.username }),
  });
}

/* ════════════════════════════════════════════════════════════════════
   Participants REST API
════════════════════════════════════════════════════════════════════ */
/**
 * Sincroniza la lista de participantes desde la API REST.
 * Si la petición falla, se mantiene el estado local sin cambios.
 * @param {number} [delay=0] - ms a esperar antes de hacer la petición
 */
async function fetchParticipants(delay = 0) {
  if (delay > 0) await new Promise((r) => setTimeout(r, delay));
  // Sólo sincronizar si seguimos conectados a la misma sala
  if (!state.connected) return;
  try {
    const url = `${API_BASE_URL}/api/rooms/${encodeURIComponent(state.roomId)}/participants`;
    const response = await fetch(url);
    if (!response.ok) {
      console.warn('fetchParticipants: respuesta no OK', response.status);
      return;
    }
    const data = await response.json();
    if (!Array.isArray(data)) return;
    // Acepta string[] u object[] con campo username/name
    state.users = new Set(
      data.map((p) => (typeof p === 'string' ? p : (p.username ?? p.name ?? String(p))))
    );
    renderUsers();
  } catch (err) {
    console.warn('fetchParticipants: error de red, usando estado local', err);
  }
}

/* ════════════════════════════════════════════════════════════════════
   STOMP / WebSocket
════════════════════════════════════════════════════════════════════ */
function connect() {
  const socket = new SockJS(WS_URL);

  const client = new StompJs.Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,

    onConnect: () => {
      setConnected(true);

      // Subscribe to the room topic
      client.subscribe(`/topic/chat/${state.roomId}`, onMessage);

      // Subscribe to the typing topic
      client.subscribe(`/topic/chat/${state.roomId}/typing`, (frame) => {
        const msg = JSON.parse(frame.body);
        if (msg.sender === state.username) return;
        if (msg.type === 'TYPING') {
          showTypingIndicator(msg.sender);
        } else {
          hideTypingIndicator(msg.sender);
        }
      });

      // Announce ourselves
      client.publish({
        destination: `/app/chat/${state.roomId}/join`,
        body: JSON.stringify({
          sender: state.username,
          roomId: state.roomId,
          type: 'JOIN',
        }),
      });

      // Añadir al usuario actual de inmediato (feedback instantáneo)
      state.users.add(state.username);
      renderUsers();
      appendSystem(`✓ Te uniste a la sala #${state.roomId}`);
      // Sincronizar con la API una vez que el servidor procese el JOIN
      fetchParticipants(600);
      // Polling periódico para mantener la lista siempre actualizada
      startPolling();
    },

    onDisconnect: () => {
      stopPolling();
      setConnected(false);
    },

    onStompError: (frame) => {
      console.error('STOMP error:', frame);
      setConnected(false);
      appendSystem('⚠ Error de conexión con el servidor.');
    },
  });

  state.stompClient = client;
  client.activate();
}

function startPolling() {
  stopPolling();
  state.pollInterval = setInterval(() => fetchParticipants(), 8000);
}

function stopPolling() {
  if (state.pollInterval) {
    clearInterval(state.pollInterval);
    state.pollInterval = null;
  }
}

function onMessage(frame) {
  let msg;
  try {
    msg = JSON.parse(frame.body);
  } catch {
    return;
  }

  // Normalize: if no explicit type but has content, treat as CHAT
  const type = (msg.type ?? '').toUpperCase();

  if (type === 'JOIN') {
    if (msg.sender !== state.username) {
      appendSystem(`${msg.sender} se unió a la sala`);
      state.users.add(msg.sender);
      renderUsers();
      fetchParticipants(300);
    }
  } else if (type === 'LEAVE') {
    appendSystem(`${msg.sender} salió de la sala`);
    state.users.delete(msg.sender);
    renderUsers();
    fetchParticipants(300);
  } else if (type === 'CHAT' || msg.content) {
    appendMessage(msg);
  }
}

function sendMessage() {
  const input = $('message-input');
  const content = input.value.trim();

  if (!content || !state.connected) return;

  state.stompClient.publish({
    destination: `/app/chat/${state.roomId}/send`,
    body: JSON.stringify({
      sender: state.username,
      roomId: state.roomId,
      content,
      type: 'CHAT',
    }),
  });

  publishStopTyping();
  input.value = '';
  $('char-count').textContent = '0 / 500';
}

function leaveRoom() {
  if (state.stompClient && state.connected) {
    state.stompClient.publish({
      destination: `/app/chat/${state.roomId}/leave`,
      body: JSON.stringify({
        sender: state.username,
        roomId: state.roomId,
        type: 'LEAVE',
      }),
    });
    state.stompClient.deactivate();
  }

  stopPolling();

  // Reset
  state.users.clear();
  state.typingUsers.clear();
  renderTyping();
  $('messages-list').innerHTML = '';
  $('users-list').innerHTML = '';
  $('char-count').textContent = '0 / 500';
  $('message-input').value = '';
  setConnected(false);
  showScreen('join');
}

/* ════════════════════════════════════════════════════════════════════
   Join handler
════════════════════════════════════════════════════════════════════ */
function handleJoin() {
  const username = $('username').value.trim();
  const roomId = $('roomId').value.trim();
  const errorEl = $('join-error');

  if (!username || !roomId) {
    errorEl.textContent = 'Por favor completa tu nombre y el ID de sala.';
    errorEl.classList.remove('d-none');
    return;
  }

  // Basic input sanitation
  if (!/^[\w\s\-áéíóúÁÉÍÓÚñÑ.]+$/u.test(username)) {
    errorEl.textContent = 'El nombre contiene caracteres no permitidos.';
    errorEl.classList.remove('d-none');
    return;
  }

  if (!/^[\w\-]+$/.test(roomId)) {
    errorEl.textContent = 'El ID de sala solo puede contener letras, números y guiones.';
    errorEl.classList.remove('d-none');
    return;
  }

  errorEl.classList.add('d-none');
  state.username = username;
  state.roomId = roomId;

  $('header-room').textContent = `#${roomId}`;
  $('header-user').textContent = username;

  showScreen('chat');
  connect();
}

/* ════════════════════════════════════════════════════════════════════
   Event listeners
════════════════════════════════════════════════════════════════════ */
document.addEventListener('DOMContentLoaded', () => {
  // Join
  $('join-btn').addEventListener('click', handleJoin);
  ['username', 'roomId'].forEach((id) => {
    $(id).addEventListener('keydown', (e) => {
      if (e.key === 'Enter') handleJoin();
    });
  });

  // Send
  $('send-btn').addEventListener('click', sendMessage);
  $('message-input').addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  });

  // Character counter + typing events
  $('message-input').addEventListener('input', () => {
    const len = $('message-input').value.length;
    const counter = $('char-count');
    counter.textContent = `${len} / 500`;
    counter.classList.toggle('text-danger', len >= 480);

    publishTyping();

    clearTimeout(state.typingTimer);
    state.typingTimer = setTimeout(publishStopTyping, 2000);
  });

  // Leave
  $('leave-btn').addEventListener('click', leaveRoom);

  // Notify server before tab closes
  window.addEventListener('beforeunload', () => {
    if (state.connected) leaveRoom();
  });
});
