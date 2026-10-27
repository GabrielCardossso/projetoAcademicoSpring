import axios from 'axios';

export const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

// Única instância do axios usada pelo frontend
export const http = axios.create({
  baseURL: API_URL,
  // Envia o cookie de sessão (JSESSIONID) nas chamadas ao backend
  withCredentials: true,
  // Lê o cookie XSRF-TOKEN do Spring Security e devolve no cabeçalho X-XSRF-TOKEN (proteção CSRF)
  withXSRFToken: true,
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
});

// Quando o backend responde 401 fora das rotas /auth, a sessão acabou: avisa o AuthContext
let aoPerderSessao: () => void = () => {};

export function definirAoPerderSessao(callback: () => void) {
  aoPerderSessao = callback;
}

function verificar401(status: number | undefined, url: string | undefined) {
  if (status === 401 && !url?.startsWith('/auth/')) {
    aoPerderSessao();
  }
}

http.interceptors.response.use(
  (resposta) => {
    verificar401(resposta.status, resposta.config.url);
    return resposta;
  },
  (erro) => {
    if (axios.isAxiosError(erro)) {
      verificar401(erro.response?.status, erro.config?.url);
    }
    return Promise.reject(erro);
  },
);
