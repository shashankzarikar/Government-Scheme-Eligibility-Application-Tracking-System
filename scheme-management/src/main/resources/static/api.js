// ─────────────────────────────────────────────────────────────────────────────
// api.js  —  Shared API helper for GovScheme Portal
// Include this file in every HTML page via <script src="api.js"></script>
// ─────────────────────────────────────────────────────────────────────────────

const API_BASE = 'http://localhost:8080';

function isFormData(value) {
    return typeof FormData !== 'undefined' && value instanceof FormData;
}

// ── Token helpers ─────────────────────────────────────────────────────────────
function saveAuth(token, role, fullName, userId) {
    localStorage.setItem('token', token);
    localStorage.setItem('role', role);
    localStorage.setItem('fullName', fullName);
    localStorage.setItem('userId', userId);
}

function getToken()    { return localStorage.getItem('token'); }
function getRole()     { return localStorage.getItem('role'); }
function getFullName() { return localStorage.getItem('fullName'); }
function getUserId()   { return localStorage.getItem('userId'); }

function clearAuth() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('fullName');
    localStorage.removeItem('userId');
}

function logout() {
    clearAuth();
    window.location.href = 'login.html';
}

// ── Redirect if not logged in ─────────────────────────────────────────────────
function requireAuth(expectedRole) {
    const token = getToken();
    const role  = getRole();
    if (!token) { window.location.href = 'login.html'; return; }
    if (expectedRole && role !== expectedRole) {
        alert('Access denied. You are not authorized to view this page.');
        window.location.href = 'login.html';
    }
}

// ── Core fetch wrapper ────────────────────────────────────────────────────────
async function apiFetch(endpoint, options = {}) {
    const token = getToken();
    const bodyIsFormData = isFormData(options.body);
    const headers = { ...(options.headers || {}) };
    if (!bodyIsFormData && !headers['Content-Type']) {
        headers['Content-Type'] = 'application/json';
    }
    if (token) headers['Authorization'] = 'Bearer ' + token;

    const fetchOptions = { ...options, headers };
    if (!bodyIsFormData && options.body && typeof options.body !== 'string' && headers['Content-Type'] === 'application/json') {
        fetchOptions.body = JSON.stringify(options.body);
    }

    const res = await fetch(API_BASE + endpoint, fetchOptions);

    if (res.status === 401) { logout(); return; }

    const text = await res.text();
    let data;
    try { data = JSON.parse(text); } catch { data = text; }

    if (!res.ok) {
        throw new Error(typeof data === 'string' ? data : (data.message || 'Request failed'));
    }
    return data;
}

async function apiDownload(endpoint) {
    const token = getToken();
    const headers = {};
    if (token) headers['Authorization'] = 'Bearer ' + token;

    const res = await fetch(API_BASE + endpoint, { method: 'GET', headers });

    if (res.status === 401) { logout(); return; }

    if (!res.ok) {
        const text = await res.text();
        throw new Error(text || 'Download failed');
    }

    const blob = await res.blob();
    const disposition = res.headers.get('Content-Disposition') || '';
    const match = disposition.match(/filename="?([^";]+)"?/i);
    const filename = match ? match[1] : endpoint.split('/').pop() || 'download';
    return { blob, filename };
}

// ── Convenience methods ───────────────────────────────────────────────────────
const api = {
    get:    (url)          => apiFetch(url, { method: 'GET' }),
    post:   (url, body)    => apiFetch(url, { method: 'POST',   body }),
    put:    (url, body)    => apiFetch(url, { method: 'PUT',    body }),
    delete: (url)          => apiFetch(url, { method: 'DELETE' }),
    download: (url)        => apiDownload(url),
};
