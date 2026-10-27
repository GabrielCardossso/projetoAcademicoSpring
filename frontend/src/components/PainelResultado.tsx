import { textoStatus } from '../services/apiService';
import type { Registro } from '../types/api';
import type { Entidade, ResultadoRequisicao } from '../types/endpoints';
import { MetodoTag } from './MetodoTag';
import { TabelaRegistros } from './TabelaRegistros';

interface Props {
  resultado: ResultadoRequisicao;
  entidade: Entidade;
}

export function PainelResultado({ resultado, entidade }: Props) {
  const { dados } = resultado;
  const lista = resultado.sucessoHttp && Array.isArray(dados) ? (dados as Registro[]) : null;
  const textoDoBackend = typeof dados === 'string';

  return (
    <section className="painel">
      <h2>Resultado</h2>
      <table className="tabela-info">
        <tbody>
          <tr>
            <th>Requisição</th>
            <td>
              <MetodoTag metodo={resultado.metodo} /> <code>{resultado.caminho}</code>
            </td>
          </tr>
          {resultado.corpoEnviado && (
            <tr>
              <th>Corpo enviado</th>
              <td>
                <code>{resultado.corpoEnviado}</code> <span className="info">(application/x-www-form-urlencoded)</span>
              </td>
            </tr>
          )}
          <tr>
            <th>Status HTTP</th>
            <td className={resultado.sucessoHttp ? 'status-ok' : 'status-erro'}>
              {resultado.status !== undefined ? textoStatus(resultado.status) : 'sem resposta'}
            </td>
          </tr>
          <tr>
            <th>Tempo</th>
            <td>{resultado.duracaoMs} ms</td>
          </tr>
          <tr>
            <th>{textoDoBackend ? 'Resposta do backend' : 'Mensagem'}</th>
            <td>
              {textoDoBackend ? <pre className="texto-resposta">{resultado.mensagem}</pre> : resultado.mensagem}
            </td>
          </tr>
        </tbody>
      </table>

      {textoDoBackend && resultado.metodo !== 'GET' && (
        <p className="info">
          Este endpoint devolve um texto com status 200 tanto quando a operação é feita quanto quando uma regra de
          negócio a recusa. O resultado está na mensagem acima.
        </p>
      )}

      {lista && <TabelaRegistros entidade={entidade} registros={lista} />}

      {dados !== undefined && !textoDoBackend && (
        <details open={!lista}>
          <summary>Corpo da resposta (JSON)</summary>
          <pre className="json">{JSON.stringify(dados, null, 2)}</pre>
        </details>
      )}
    </section>
  );
}
