import axios from 'axios';
import { http } from './http';
import type { UsuarioLogado } from '../types/api';

// Usuário da sessão atual, ou null se o backend responder 401
export async function buscarUsuarioLogado(): Promise<UsuarioLogado | null> {
  try {
    const resposta = await http.get<UsuarioLogado>('/auth/me');
    return resposta.data;
  } catch (erro) {
    if (axios.isAxiosError(erro) && erro.response?.status === 401) {
      return null;
    }
    throw erro;
  }
}

// POST /auth/login (form-urlencoded, campos email e senha), processado pelo Spring Security
export async function entrar(email: string, senha: string): Promise<UsuarioLogado> {
  // Garante o cookie XSRF-TOKEN antes do POST (qualquer resposta do backend o envia)
  await buscarUsuarioLogado();

  await http.post('/auth/login', new URLSearchParams({ email, senha }));

  const usuario = await buscarUsuarioLogado();
  if (!usuario) {
    throw new Error('O backend não confirmou a sessão após o login.');
  }
  return usuario;
}

// POST /auth/logout invalida a sessão no backend
export async function sair(): Promise<void> {
  await http.post('/auth/logout');
}
