import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { FormularioOperacao } from '../components/FormularioOperacao';
import { useContextoLayout } from '../components/Layout';
import { MetodoTag } from '../components/MetodoTag';
import { PainelResultado } from '../components/PainelResultado';
import { TabelaRegistros } from '../components/TabelaRegistros';
import { useListagens } from '../hooks/useListagens';
import { executarOperacao } from '../services/apiService';
import type { Valores } from '../services/apiService';
import { buscarEntidade, buscarOperacao, podeExecutar } from '../services/endpoints';
import type { UsuarioLogado } from '../types/api';
import type { ChaveEntidade, Entidade, Operacao, ResultadoRequisicao } from '../types/endpoints';

export function OperacaoPage() {
  const params = useParams();
  const { usuario } = useAuth();
  const entidade = buscarEntidade(params.entidade ?? '');
  const operacao = buscarOperacao(params.entidade ?? '', params.operacao ?? '');

  if (!usuario) return null;
  if (!entidade || !operacao) {
    return <p>Operação não encontrada.</p>;
  }

  // A key recria a tela ao trocar de operação (limpa formulário e resultado)
  return (
    <TelaOperacao key={`${entidade.chave}/${operacao.id}`} entidade={entidade} operacao={operacao} usuario={usuario} />
  );
}

interface Props {
  entidade: Entidade;
  operacao: Operacao;
  usuario: UsuarioLogado;
}

function TelaOperacao({ entidade, operacao, usuario }: Props) {
  const { mostrarSemPermissao } = useContextoLayout();
  const permitido = podeExecutar(operacao, usuario.perfil);
  const alteraDados = operacao.metodo !== 'GET';

  // Listagens necessárias: entidades citadas nos campos de ID e, se a operação altera dados, a própria entidade
  const entidadesUsadas: ChaveEntidade[] = operacao.campos
    .map((c) => c.entidade)
    .filter((e): e is ChaveEntidade => e !== undefined);
  if (alteraDados || operacao.preencherPor) entidadesUsadas.push(operacao.entidade);
  const { listas, erros, carregando, recarregar } = useListagens(entidadesUsadas);

  const [resultado, setResultado] = useState<ResultadoRequisicao | null>(null);
  const [executando, setExecutando] = useState(false);

  async function executar(valores: Valores) {
    setExecutando(true);
    setResultado(null);
    const novo = await executarOperacao(operacao, valores);
    setResultado(novo);
    setExecutando(false);
    if (alteraDados && novo.sucessoHttp) {
      recarregar();
    }
  }

  return (
    <>
      <p className="trilha">
        {entidade.nome} / {operacao.titulo}
      </p>
      <h1>{operacao.titulo}</h1>
      <p>
        <MetodoTag metodo={operacao.metodo} /> <code>{operacao.caminho}</code>
      </p>
      <p>{operacao.descricao}</p>
      <p className="info">Perfis com acesso no backend: {operacao.perfis.join(', ')}</p>

      {!permitido && (
        <p className="mensagem alerta">
          O perfil {usuario.perfil} não tem permissão para esta operação.
          {mostrarSemPermissao && ' A requisição pode ser enviada mesmo assim para ver o bloqueio do backend (403).'}
        </p>
      )}

      {(permitido || mostrarSemPermissao) && (
        <section className="painel">
          <h2>Requisição</h2>
          <FormularioOperacao
            operacao={operacao}
            usuario={usuario}
            listas={listas}
            executando={executando}
            onExecutar={executar}
          />
        </section>
      )}

      {resultado && <PainelResultado resultado={resultado} entidade={entidade} />}

      {alteraDados && (
        <section className="painel">
          <h2>
            Registros atuais <span className="info">(GET {entidade.caminhoListagem})</span>
          </h2>
          <button type="button" onClick={recarregar} disabled={carregando}>
            Recarregar
          </button>
          {carregando && <p className="info">Carregando...</p>}
          {!carregando && erros[entidade.chave] && <p className="mensagem erro">{erros[entidade.chave]}</p>}
          {!carregando && listas[entidade.chave] && (
            <TabelaRegistros entidade={entidade} registros={listas[entidade.chave] ?? []} />
          )}
        </section>
      )}
    </>
  );
}
