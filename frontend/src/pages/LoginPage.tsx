import { useState } from 'react';
import type { FormEvent } from 'react';
import axios from 'axios';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { descreverErro } from '../services/apiService';
import { API_URL } from '../services/http';

function mensagemDeFalha(erro: unknown): string {
  if (axios.isAxiosError(erro)) {
    if (!erro.response) return `Backend indisponível em ${API_URL}. Verifique se o Spring Boot está em execução.`;
    if (erro.response.status === 401) return 'E-mail ou senha inválidos.';
    if (erro.response.status === 403) return 'Requisição recusada (403): o token CSRF não foi enviado ou é inválido.';
  }
  return descreverErro(erro);
}

export function LoginPage() {
  const { usuario, verificando, aviso, entrar } = useAuth();
  const location = useLocation();
  const voltarPara = (location.state as { voltarPara?: string } | null)?.voltarPara ?? '/';

  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);

  if (verificando) {
    return <p className="pagina-simples">Verificando sessão...</p>;
  }
  if (usuario) {
    return <Navigate to={voltarPara} replace />;
  }

  async function enviar(evento: FormEvent) {
    evento.preventDefault();
    setErro(null);
    setEnviando(true);
    try {
      await entrar(email, senha);
    } catch (falha) {
      setErro(mensagemDeFalha(falha));
      setSenha('');
      setEnviando(false);
    }
  }

  return (
    <div className="pagina-login">
      <form className="caixa-login" onSubmit={enviar}>
        <h1>Login</h1>
        <p className="info">
          Autenticação pelo Spring Security em <code>POST /auth/login</code>. Backend: <code>{API_URL}</code>
        </p>

        {aviso && <p className="mensagem erro">{aviso}</p>}

        <label htmlFor="email">E-mail</label>
        <input
          id="email"
          type="email"
          autoComplete="username"
          required
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <label htmlFor="senha">Senha</label>
        <input
          id="senha"
          type="password"
          autoComplete="current-password"
          required
          value={senha}
          onChange={(e) => setSenha(e.target.value)}
        />

        {erro && <p className="mensagem erro">{erro}</p>}

        <button type="submit" disabled={enviando}>
          {enviando ? 'Entrando...' : 'Entrar'}
        </button>
      </form>
    </div>
  );
}
