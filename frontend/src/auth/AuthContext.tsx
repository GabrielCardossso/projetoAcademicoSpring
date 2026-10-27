import { createContext, useCallback, useContext, useEffect, useState } from 'react';
import type { ReactNode } from 'react';
import * as authService from '../services/authService';
import { definirAoPerderSessao } from '../services/http';
import { descreverErro } from '../services/apiService';
import type { UsuarioLogado } from '../types/api';

interface AuthContextValor {
  // Só é preenchido depois que o backend confirma a sessão (GET /auth/me)
  usuario: UsuarioLogado | null;
  verificando: boolean;
  aviso: string | null;
  entrar: (email: string, senha: string) => Promise<void>;
  sair: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValor | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<UsuarioLogado | null>(null);
  const [verificando, setVerificando] = useState(true);
  const [aviso, setAviso] = useState<string | null>(null);

  // Ao abrir a página, pergunta ao backend se já existe uma sessão válida
  useEffect(() => {
    authService
      .buscarUsuarioLogado()
      .then(setUsuario)
      .catch((erro) => setAviso(`Não foi possível verificar a sessão. ${descreverErro(erro)}`))
      .finally(() => setVerificando(false));
  }, []);

  // Um 401 em qualquer requisição derruba a sessão no frontend
  useEffect(() => {
    definirAoPerderSessao(() => {
      setUsuario(null);
      setAviso('A sessão expirou ou não é mais válida. Entre novamente.');
    });
  }, []);

  const entrar = useCallback(async (email: string, senha: string) => {
    const logado = await authService.entrar(email, senha);
    setAviso(null);
    setUsuario(logado);
  }, []);

  const sair = useCallback(async () => {
    try {
      await authService.sair();
      setAviso(null);
    } catch (erro) {
      setAviso(`O logout não foi confirmado pelo backend. ${descreverErro(erro)}`);
    } finally {
      setUsuario(null);
    }
  }, []);

  return (
    <AuthContext.Provider value={{ usuario, verificando, aviso, entrar, sair }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValor {
  const contexto = useContext(AuthContext);
  if (!contexto) {
    throw new Error('useAuth precisa estar dentro de <AuthProvider>.');
  }
  return contexto;
}
