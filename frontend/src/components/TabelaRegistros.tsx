import type { Registro } from '../types/api';
import type { Entidade } from '../types/endpoints';

// Lê atributos aninhados, ex.: "equipe.nome"
export function lerAtributo(registro: unknown, caminho: string): unknown {
  return caminho.split('.').reduce<unknown>((valor, chave) => {
    if (valor !== null && typeof valor === 'object') {
      return (valor as Record<string, unknown>)[chave];
    }
    return undefined;
  }, registro);
}

function formatar(valor: unknown): string {
  if (valor === null || valor === undefined) return '-';
  if (typeof valor === 'boolean') return valor ? 'sim' : 'não';
  return String(valor);
}

interface Props {
  entidade: Entidade;
  registros: Registro[];
}

export function TabelaRegistros({ entidade, registros }: Props) {
  if (registros.length === 0) {
    return <p className="vazio">Nenhum registro encontrado.</p>;
  }

  return (
    <div className="tabela-rolagem">
      <table className="tabela">
        <thead>
          <tr>
            {entidade.colunas.map((c) => (
              <th key={c.atributo}>{c.titulo}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {registros.map((registro) => (
            <tr key={registro.id}>
              {entidade.colunas.map((c) => (
                <td key={c.atributo}>{formatar(lerAtributo(registro, c.atributo))}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
