import { NavLink } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { ENTIDADES, operacoesDa, podeExecutar } from '../services/endpoints';
import { MetodoTag } from './MetodoTag';

interface Props {
  mostrarSemPermissao: boolean;
  onMostrarSemPermissao: (valor: boolean) => void;
}

export function Sidebar({ mostrarSemPermissao, onMostrarSemPermissao }: Props) {
  const { usuario, sair } = useAuth();
  if (!usuario) return null;

  return (
    <aside className="sidebar">
      <div className="sidebar-usuario">
        <div><strong>{usuario.nome}</strong></div>
        <div>{usuario.email}</div>
        <div>Perfil: {usuario.perfil}</div>
        <label className="opcao">
          <input
            type="checkbox"
            checked={mostrarSemPermissao}
            onChange={(e) => onMostrarSemPermissao(e.target.checked)}
          />
          Mostrar operações sem permissão
        </label>
        <button type="button" onClick={sair}>
          Sair
        </button>
      </div>

      <nav>
        <NavLink to="/" end className="sidebar-link">
          Funcionalidades disponíveis
        </NavLink>

        {ENTIDADES.map((entidade) => {
          const operacoes = operacoesDa(entidade.chave).filter(
            (o) => mostrarSemPermissao || podeExecutar(o, usuario.perfil),
          );
          return (
            <div key={entidade.chave} className="sidebar-grupo">
              <h2>{entidade.nome}</h2>
              <ul>
                {operacoes.map((o) => (
                  <li key={o.id}>
                    <NavLink to={`/${o.entidade}/${o.id}`} className="sidebar-link">
                      <MetodoTag metodo={o.metodo} /> {o.titulo}
                      {!podeExecutar(o, usuario.perfil) && <span className="sem-permissao"> (sem permissão)</span>}
                    </NavLink>
                  </li>
                ))}
              </ul>
            </div>
          );
        })}
      </nav>
    </aside>
  );
}
