const API = 'https://roy-production-b7a0.up.railway.app';

function getSession() {
  const data = localStorage.getItem('roy_session');
  return data ? JSON.parse(data) : null;
}

function setSession(data) {
  localStorage.setItem('roy_session', JSON.stringify(data));
}

function clearSession() {
  localStorage.removeItem('roy_session');
}

function requireAuth() {
  if (!getSession()) {
    window.location.href = 'login.html';
    return false;
  }
  return true;
}
