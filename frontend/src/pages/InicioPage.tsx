import { Link } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { useContextoLayout } from '../components/Layout';
import { MetodoTag } from '../components/MetodoTag';
import { ENTIDADES, operacoesDa, podeExecutar } from '../services/endpoints';
import { API_URL } from '../services/http';

export function InicioPage() {
  const { usuario } = useAuth();
  const { mostrarSemPermissao } = useContextoLayout();
  if (!usuario) return null;

  return (
    <>
      <h1>Funcionalidades disponíveis</h1>
      <p>
        Usuário: <strong>{usuario.nome}</strong> ({usuario.perfil}). Backend: <code>{API_URL}</code>
      </p>
      <p className="info">
        A lista mostra as operações liberadas para o seu perfil. A permissão é verificada de novo no backend pelo
        Spring Security em cada requisição. O backend não tem DELETE: a exclusão é lógica, pelo endpoint{' '}
        <code>setStatus</code>, que alterna o registro entre ativo e inativo.
      </p>

      {ENTIDADES.map((entidade) => {
        const operacoes = operacoesDa(entidade.chave).filter(
          (o) => mostrarSemPermissao || podeExecutar(o, usuario.perfil),
        );
        return (
          <section key={entidade.chave}>
            <h2>{entidade.nome}</h2>
            <div className="tabela-rolagem">
              <table className="tabela">
                <thead>
                  <tr>
                    <th>Método</th>
                    <th>Endpoint</th>
                    <th>Operação</th>
                    <th>Descrição</th>
                    <th>Perfis</th>
                  </tr>
                </thead>
                <tbody>
                  {operacoes.map((o) => (
                    <tr key={o.id}>
                      <td>
                        <MetodoTag metodo={o.metodo} />
                      </td>
                      <td>
                        <code>{o.caminho}</code>
                      </td>
                      <td>
                        <Link to={`/${o.entidade}/${o.id}`}>{o.titulo}</Link>
                      </td>
                      <td>{o.descricao}</td>
                      <td>{o.perfis.join(', ')}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        );
      })}
    </>
  );
}
