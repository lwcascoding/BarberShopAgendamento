const DEFAULT_HEADERS = { 'Content-Type': 'application/json' };

export async function apiRequest(path, options = {}) {
  const response = await fetch(path, {
    ...options,
    headers: { ...DEFAULT_HEADERS, ...options.headers },
  });

  if (response.status === 204) return null;

  const payload = await response.json().catch(() => null);

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error('Usuário ou senha de administrador inválidos.');
    }
    throw new Error(payload?.message ?? `Falha na requisição: ${response.status}`);
  }

  return payload;
}
